package me.owdding.lib.utils.strings

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.datafixers.util.Either
import tech.thatgravyboat.skyblockapi.utils.json.JsonArray
import me.owdding.ktmodules.Module
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.lib.events.MeowddingLibRegisterCommandsEvent
import me.owdding.lib.events.StartRepoLoadingEvent
import me.owdding.lib.utils.FeatureName
import me.owdding.repo.RemoteRepo
import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KProperty

interface CompletedDelegate<Type> {
    val key: String
    val default: Type

    fun update()
}

data class CompletedElementDelegate<Type>(
    val registry: BaseRemoteStrings,
    override val key: String,
    val transform: (String) -> Type,
    override val default: Type,
) : CompletedDelegate<Type> {
    init {
        registry.register(this)
    }

    var data = get()

    fun get() = registry.stringOverwrites[key]?.let { transform(it.left().getOrNull() ?: return@let null) } ?: default

    override fun update() {
        data = get()
    }

    operator fun <This> getValue(ref: This, property: KProperty<*>): Type = data
}

data class ElementDelegate<Type>(
    val registry: BaseRemoteStrings,
    val path: String?,
    val prefix: String?,
    val transformer: (String) -> Type,
    val default: Type,
) {
    operator fun <This> provideDelegate(ref: This, property: KProperty<*>) = CompletedElementDelegate(
        registry,
        join(prefix, path ?: property.name) ?: property.name,
        transformer,
        default,
    )
}

data class CompletedListElementDelegate<Type>(
    val registry: BaseRemoteStrings,
    override val key: String,
    val transform: (String) -> Type,
    override val default: List<Type>,
) : CompletedDelegate<List<Type>> {
    init {
        registry.register(this)
    }

    var data = get()

    fun get() = registry.stringOverwrites[key]?.let { it.right().getOrNull()?.map(transform) ?: return@let null } ?: default

    override fun update() {
        data = get()
    }

    operator fun <This> getValue(ref: This, property: KProperty<*>): List<Type> = data
}

data class ListElementDelegate<Type>(
    val registry: BaseRemoteStrings,
    val path: String?,
    val prefix: String?,
    val transformer: (String) -> Type,
    val default: List<Type>,
) {
    operator fun <This> provideDelegate(ref: This, property: KProperty<*>) = CompletedListElementDelegate(
        registry,
        join(prefix, path ?: property.name) ?: property.name,
        transformer,
        default,
    )
}

private fun join(vararg parts: String?) = listOfNotNull(*parts).takeUnless { it.isEmpty() }?.joinToString(".")

interface StringGroup {
    val registry: BaseRemoteStrings

    fun strings(
        vararg defaults: String,
        path: String? = null,
        prefix: String? = null,
    ): ListElementDelegate<String>

    fun componentRegexes(
        @Language("RegExp") vararg regex: String,
        path: String? = null,
        prefix: String? = null,
    ): ListElementDelegate<ComponentRegex>

    fun regexes(
        @Language("RegExp") vararg regex: String,
        path: String? = null,
        prefix: String? = null,
    ): ListElementDelegate<Regex>


    fun string(
        default: String,
        path: String? = null,
        prefix: String? = null,
    ): ElementDelegate<String>


    fun componentRegex(
        @Language("RegExp") regex: String,
        path: String? = null,
        prefix: String? = null,
    ): ElementDelegate<ComponentRegex> = componentRegex(regex.toRegex(), path, prefix)

    fun componentRegex(
        regex: Regex,
        path: String? = null,
        prefix: String? = null,
    ): ElementDelegate<ComponentRegex>


    fun regex(
        @Language("RegExp") regex: String,
        path: String? = null,
        prefix: String? = null,
    ): ElementDelegate<Regex> = regex(regex.toRegex(), path, prefix)

    fun regex(
        regex: Regex,
        path: String? = null,
        prefix: String? = null,
    ): ElementDelegate<Regex>


    fun resolve(path: String): StringGroup

    companion object {
        internal val STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
        fun StringGroup.resolve(): StringGroup {
            val name = STACK_WALKER.callerClass.let { it.annotations.filterIsInstance<FeatureName>().firstOrNull()?.name ?: it.simpleName }
            return resolve(name)
        }
    }
}

data class StringGroupImpl(val prefix: String, val parent: StringGroup) : StringGroup {
    override val registry: BaseRemoteStrings get() = parent.registry

    override fun strings(vararg defaults: String, path: String?, prefix: String?): ListElementDelegate<String> = parent.strings(defaults = defaults, path, join(this.prefix, prefix))
    override fun componentRegexes(vararg regex: String, path: String?, prefix: String?, ): ListElementDelegate<ComponentRegex> = parent.componentRegexes(regex = regex, path, join(this.prefix, prefix))
    override fun regexes(vararg regex: String, path: String?, prefix: String?, ): ListElementDelegate<Regex> = parent.regexes(regex = regex, path, join(this.prefix, prefix))

    override fun string(default: String, path: String?, prefix: String?): ElementDelegate<String> = parent.string(default, path, join(this.prefix, prefix))
    override fun regex(regex: Regex, path: String?, prefix: String?): ElementDelegate<Regex> = parent.regex(regex, path, join(this.prefix, prefix))
    override fun componentRegex(regex: Regex, path: String?, prefix: String?): ElementDelegate<ComponentRegex> = parent.componentRegex(regex, path, join(this.prefix, prefix))



    override fun resolve(path: String): StringGroup = StringGroupImpl(join(prefix, path) ?: path, this)
}

abstract class BaseRemoteStrings(val repoFilePath: String) : StringGroup {
    override val registry: BaseRemoteStrings get() = this

    internal val list: MutableList<CompletedDelegate<*>> = mutableListOf()
    val stringOverwrites: MutableMap<String, Either<String, List<String>>> = mutableMapOf()

    init {
        instances.add(this)
    }

    @Subscription(StartRepoLoadingEvent::class, inherited = true)
    fun repoStartLoading() {
        stringOverwrites.clear()
        list.forEach(CompletedDelegate<*>::update)
    }

    @Subscription(FinishRepoLoadingEvent::class, inherited = true)
    fun repoLoaded() {
        val data = RemoteRepo.getFileContentAsJson(repoFilePath) as? JsonObject ?: return

        val map = HashMap<String, Either<String, List<String>>>()
        data.discover("", map::put)
        stringOverwrites.putAll(map)
        list.forEach(CompletedDelegate<*>::update)
    }

    private fun JsonObject.discover(path: String, consumer: (String, Either<String, List<String>>) -> Unit) {
        this.entrySet().forEach { (key, value) ->
            val path = listOfNotNull(path.takeUnless { it.isEmpty() }, key).joinToString(".")
            when (value) {
                is JsonObject -> value.discover(path, consumer)
                is JsonPrimitive -> consumer(key, Either.left(value.asString))
                is JsonArray -> consumer(key, Either.right(value.asList().map { it.asString }))
            }
        }
    }

    override fun strings(
        vararg defaults: String,
        path: String?,
        prefix: String?,
    ): ListElementDelegate<String> = ListElementDelegate(this, path, prefix, transformer = { it }, defaults.toList())

    override fun componentRegexes(
        vararg regex: String,
        path: String?,
        prefix: String?,
    ): ListElementDelegate<ComponentRegex> = ListElementDelegate(this, path, prefix, transformer = { ComponentRegex(it) }, regex.map { ComponentRegex(it) })

    override fun regexes(
        vararg regex: String,
        path: String?,
        prefix: String?,
    ): ListElementDelegate<Regex> = ListElementDelegate(this, path, prefix, transformer = { Regex(it) }, regex.map { Regex(it) })

    override fun string(default: String, path: String?, prefix: String?): ElementDelegate<String> =
        ElementDelegate(this, path, prefix, transformer = { it }, default)

    override fun componentRegex(regex: Regex, path: String?, prefix: String?): ElementDelegate<ComponentRegex> =
        ElementDelegate(this, path, prefix, transformer = { ComponentRegex(it) }, ComponentRegex(regex))

    override fun regex(regex: Regex, path: String?, prefix: String?): ElementDelegate<Regex> =
        ElementDelegate(this, path, prefix, transformer = { Regex(it) }, regex)

    override fun resolve(path: String): StringGroup = StringGroupImpl(path, this)

    fun register(delegate: CompletedDelegate<*>) {
        validateKey(delegate.key)
        onRegister(delegate)
        list.add(delegate)
    }

    protected open fun onRegister(delegate: CompletedDelegate<*>) {}

    private fun isValidChar(char: Char): Boolean {
        return char.isLetterOrDigit() || char == '.' || char == '-' || char == '_'
    }

    private fun validateKey(key: String) {
        if (!McClient.isDev) return
        require(list.none { it.key.equals(key, ignoreCase = true) }) { "Regex Key '$key' is already in use" }
        require("@value" !in key) { "Regex key '$key' cannot contain '@value'" }
        require(key.split(".").none(String::isBlank)) { "Regex key '$key' has at least 2 '.' in a row, or ends/starts with '.'" }
        require(key.all(::isValidChar)) { "Regex key '$key' contains illegal characters" }
    }

    @Module
    companion object {
        private val instances = mutableListOf<BaseRemoteStrings>()

        @Subscription
        private fun onCommand(event: MeowddingLibRegisterCommandsEvent) {
            event.register("dev remote_strings copy") {
                thenCallback("file", StringArgumentType.greedyString(), instances.map { it.repoFilePath }) {
                    val file = argument<String>("file")
                    val instance = instances.find { it.repoFilePath == file } ?: run {
                        Text.of("Could not find registry for $file", TextColor.RED).send()
                        return@thenCallback
                    }

                    val json = JsonObject()
                    instance.list.forEach { delegate ->
                        val parts = delegate.key.split(".")
                        var current = json

                        for (i in 0 until parts.size - 1) {
                            val part = parts[i]
                            if (!current.has(part) || !current.get(part).isJsonObject) {
                                current.add(part, JsonObject())
                            }
                            current = current.getAsJsonObject(part)
                        }

                        val element = when (val data = delegate.default) {
                            is List<*> -> JsonArray { data.forEach { add(it.asString()) } }
                            else -> JsonPrimitive(data.asString())
                        }

                        current.add(parts.last(), element)
                    }

                    McClient.clipboard = json.toPrettyString()
                    Text.of("Copied JSON for $file to clipboard!", TextColor.GREEN).send()
                }
            }
        }

        private fun <Type> Type.asString() = when (this) {
            is ComponentRegex -> this.regex().pattern
            is Regex -> this.pattern
            else -> this.toString()
        }
    }
}
