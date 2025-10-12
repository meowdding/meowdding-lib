package me.owdding.lib.compat

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigElementRenderer
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigUI
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfigElement
import com.teamresourceful.resourcefulconfig.api.types.elements.ResourcefulConfigEntryElement
import com.teamresourceful.resourcefulconfig.api.types.elements.ResourcefulConfigObjectEntryElement
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigValueEntry
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType
import kotlinx.coroutines.runBlocking
import net.fabricmc.loader.api.ModContainer
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.utils.extentions.asList
import tech.thatgravyboat.skyblockapi.utils.extentions.asMap
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.util.function.Predicate

object RemoteConfig {
    fun lockConfig(config: ResourcefulConfig, url: String, mod: ModContainer) {
        val result = runCatching {
            runBlocking {
                val data = Http.getResult<JsonObject>(url).getOrThrow()
                val patches = data.asMap { string, element -> VersionIntervalParser.parse(string) to element as? JsonObject }

                for ((version, data) in patches) {
                    if (data == null) continue
                    if (!version.test(mod.metadata.version)) continue

                    lockConfig(config, data)
                }
            }
        }
        result.exceptionOrNull()?.printStackTrace()
    }
}

internal class HiddenElement(
    val title: Component,
    val description: Component,
    val message: String?,
) : ResourcefulConfigElement {
    override fun search(p0: Predicate<String>): Boolean = false
    override fun renderer(): ResourceLocation = HiddenElementRenderer.ID
}

internal class HiddenElementRenderer(element: ResourcefulConfigElement) : ResourcefulConfigElementRenderer {

    private val entry: HiddenElement? = (element as? HiddenElement)

    override fun title(): Component = this.entry?.title ?: CommonText.EMPTY
    override fun description(): Component = this.entry?.description ?: CommonText.EMPTY

    override fun widgets(): List<AbstractWidget> {
        val button = ResourcefulConfigUI.button(
            0, 0, 96, 12,
            Text.of("Disabled") { this.color = TextColor.RED },
            { },
        )

        button.setTooltip(
            Tooltip.create(
                entry?.message?.let(Text::of) ?: Text.multiline(
                    Text.of("This config element is disabled.") { this.color = TextColor.RED },
                    Text.of("This config element is not available in this version of the mod.") { this.color = TextColor.GRAY },
                ),
            ),
        )

        return listOf(button)
    }

    companion object {

        val ID = ResourceLocation.fromNamespaceAndPath("mlib", "hidden_element_renderer")

        fun register() {
            ResourcefulConfigUI.registerElementRenderer(
                ID,
                ::HiddenElementRenderer,
            )
        }
    }
}

private fun lockConfig(config: ResourcefulConfig, data: JsonObject) {
    lockElements(config.elements(), data)
    for ((id, config) in config.categories()) {
        (data.get(id) as? JsonObject)?.let { data ->
            lockConfig(config, data)
        }
    }
}

private fun lockElements(entries: MutableList<ResourcefulConfigElement>, data: JsonObject) {
    entries.replaceAll { element ->
        when {
            element is ResourcefulConfigObjectEntryElement -> {
                (data.get(element.id()) as? JsonObject)?.let { data ->
                    lockElements(element.entry().elements(), data)
                }
            }

            element is ResourcefulConfigEntryElement && element.entry() is ResourcefulConfigValueEntry -> {
                val data = data.get(element.id())
                if (data is JsonObject) {
                    val value = data.get("@value")
                    val message = data.get("@message")?.asString?.takeUnless { it.isEmpty() }

                    lockEntry(element.entry() as ResourcefulConfigValueEntry, value)

                    return@replaceAll HiddenElement(
                        element.entry().options().title().toComponent(),
                        element.entry().options().comment().toComponent(),
                        message,
                    )
                }
            }
        }

        element
    }
}

private fun lockEntry(entry: ResourcefulConfigValueEntry, data: JsonElement) {
    if (entry.isArray) {
        val array = data.asList<Any?> { element ->
            when (entry.type()) {
                EntryType.BYTE -> element.asByte
                EntryType.SHORT -> element.asString
                EntryType.INTEGER -> element.asInt
                EntryType.LONG -> element.asLong
                EntryType.FLOAT -> element.asFloat
                EntryType.DOUBLE -> element.asDouble
                EntryType.STRING -> element.asString
                EntryType.BOOLEAN -> element.asBoolean
                EntryType.ENUM -> entry.objectType().enumConstants.find { (it as? Enum<*>)?.name == element.asString } as? Enum<*>
                EntryType.OBJECT -> null
            }
        }

        entry.array = array.toTypedArray()
    } else {
        when (entry.type()) {
            EntryType.BYTE -> entry.byte = data.asByte
            EntryType.SHORT -> entry.string = data.asString
            EntryType.INTEGER -> entry.int = data.asInt
            EntryType.LONG -> entry.long = data.asLong
            EntryType.FLOAT -> entry.float = data.asFloat
            EntryType.DOUBLE -> entry.double = data.asDouble
            EntryType.STRING -> entry.string = data.asString
            EntryType.BOOLEAN -> entry.boolean = data.asBoolean
            EntryType.ENUM -> entry.enum = entry.objectType().enumConstants.find { (it as? Enum<*>)?.name == data.asString } as? Enum<*>
            EntryType.OBJECT -> {}
        }
    }
}

