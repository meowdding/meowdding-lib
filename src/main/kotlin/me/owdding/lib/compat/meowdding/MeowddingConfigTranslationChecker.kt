package me.owdding.lib.compat.meowdding

import com.mojang.brigadier.arguments.StringArgumentType
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfigButton
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfigElement
import com.teamresourceful.resourcefulconfig.api.types.elements.ResourcefulConfigEntryElement
import com.teamresourceful.resourcefulconfig.api.types.elements.ResourcefulConfigSeparatorElement
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigEntry
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigListEntry
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigObjectEntry
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigInfo
import com.teamresourceful.resourcefulconfig.common.config.Configurations
import me.owdding.ktmodules.Module
import me.owdding.lib.builder.ComponentFactory
import me.owdding.lib.events.MeowddingLibRegisterCommandsEvent
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.clearAnd
import tech.thatgravyboat.skyblockapi.utils.extentions.filterValuesNotNull
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.underlined
import java.util.concurrent.CompletableFuture
import kotlin.collections.associateWith

@Module
object MeowddingConfigTranslationChecker {

    private val modsToWarn = mutableSetOf<String>()
    private var hasSent = false


    fun addModToWarn(modId: String) = modsToWarn.add(modId)

    @Subscription(ServerChangeEvent::class)
    fun onServerChange() {
        if (hasSent) return
        hasSent = true
        modsToWarn.clearAnd(::warnTranslationsOfMod)
    }

    // TODO: change this message to not be so stupid
    private fun warnTranslationsOfMod(modId: String) {
        if (!McClient.isDev) return
        val config = getConfig(modId) ?: return
        val missing = getMissingTranslations(config)
        if (missing.isEmpty()) return
        val metadata = FabricLoader.getInstance().getModContainer(modId).get().metadata
        ComponentFactory.multiline {
            string("!".repeat(100)) {
                color = TextColor.RED
            }
            newLine()

            string("${metadata.name.uppercase()} IS MISSING ") {
                append(missing.size.toFormattedString(), TextColor.GOLD)
                append(" TRANSLATIONS!!!!")
                color = TextColor.RED
                bold = true
            }
            newLine()
            string("CLICK TO COPY TRANSLATIONS") {
                color = TextColor.YELLOW
                bold = true
                underlined = true
            }

            newLine()
            string("!".repeat(100)) {
                color = TextColor.RED
            }
        }.apply {
            onClick {
                McClient.clipboard = missing.joinToString(separator = "\n")
                Text.of("Copied missing translations of ") {
                    append(metadata.name, TextColor.AQUA)
                    append(" to clipboard!")

                    color = TextColor.YELLOW
                }
            }
        }.send()
    }

    /** Gets all the translation keys used in [config] that don't have a value set */
    fun getMissingTranslations(config: ResourcefulConfig): Set<String> {
        return getAllTranslationKeys(config).filterTo(mutableSetOf()) {
            !Language.getInstance().has(it)
        }
    }

    /** Gets all the translation keys used in [config] */
    fun getAllTranslationKeys(config: ResourcefulConfig): Set<String> {
        val allTranslationKeys = buildSet { addConfigTranslations(config) }
        return allTranslationKeys.filterTo(mutableSetOf()) { it.isNotBlank() }
    }

    private fun MutableSet<String>.addConfigTranslations(config: ResourcefulConfig) {
        if (config.info().isHidden) return
        addConfigInfoTranslations(config.info())
        config.elements().forEach { addElementTranslations(it) }
        config.categories().values.forEach { addConfigTranslations(it) }
    }


    private fun Component.translationKeyOrEmpty(): String {
        return (contents as? TranslatableContents)?.key.orEmpty()
    }


    private fun MutableSet<String>.addEntryTranslations(entry: ResourcefulConfigEntry) {
        add(entry.options().title.translation)
        add(entry.options().comment.translation)
        when (this) {
            is ResourcefulConfigObjectEntry -> {
                elements().forEach { element ->
                    addElementTranslations(element)
                }
            }
            is ResourcefulConfigListEntry -> {
                for (i in 0..<size()) {
                    addEntryTranslations(get(i))
                    add(getTitle(i).translationKeyOrEmpty())
                    add(getDescription(i).translationKeyOrEmpty())
                }
            }
        }
    }

    private fun MutableSet<String>.addElementTranslations(element: ResourcefulConfigElement) {
        if (element.isHidden) return
        when (element) {
            is ResourcefulConfigSeparatorElement -> {
                add(element.title().translation)
                add(element.description().translation)
            }
            is ResourcefulConfigButton -> {
                add(element.title())
                add(element.description())
            }
            is ResourcefulConfigEntryElement -> {
                addEntryTranslations(element.entry())
            }
            else -> {}
        }
    }

    private fun MutableSet<String>.addConfigInfoTranslations(info: ResourcefulConfigInfo) {
        if (info.isHidden) return
        add(info.title().translation)
        add(info.description().translation)
        info.links().forEach { link ->
            add(link.text().translation)
        }
        info.buttons().forEach { button ->
            add(button.text().translation)
        }
    }


    @Suppress("UnstableApiUsage")
    private fun getConfig(modId: String): ResourcefulConfig? {
        return Configurations.INSTANCE.modToConfigs()[modId]?.firstOrNull()?.let { Configurations.INSTANCE.configs()[it] }
    }

    @Suppress("UnstableApiUsage")
    @Subscription
    internal fun onRegisterCommands(event: MeowddingLibRegisterCommandsEvent) {
        event.register("dev check translations") {
            val modIds = FabricLoader.getInstance().allMods
                .map { it.metadata.id }
                .associateWith(::getConfig)
                .filterValuesNotNull()


            thenCallback("mod_id", StringArgumentType.string(), modIds.keys) {
                val modId = argument<String>("mod_id")

                val config = modIds[modId] ?: run {
                    Text.of("No Rconfig with id $modId", TextColor.YELLOW).send()
                    return@thenCallback
                }

                Text.of("Getting all missing translation keys for mod ") {
                    append(modId, TextColor.AQUA)
                    append("...")
                    color = TextColor.YELLOW
                }.send()

                CompletableFuture.runAsync {
                    val translations = getMissingTranslations(config)
                    if (translations.isEmpty()) {
                        Text.of("Not missing any translation keys!", TextColor.YELLOW)
                    } else {
                        Text.of {
                            append("Missing ")
                            append(translations.size.toFormattedString(), TextColor.RED)
                            append(" translations. Click to copy.")

                            color = TextColor.YELLOW

                            onClick {
                                McClient.clipboard = translations.joinToString(separator = "\n")
                                Text.of("Copied missing translations to clipboard!", TextColor.YELLOW).send()
                            }
                        }.send()
                    }
                }

            }
        }
    }


}
