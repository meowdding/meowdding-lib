package me.owdding.lib.compat.meowdding

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.common.config.Configurations
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.olympus.client.utils.ListenableState
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.builder.MIDDLE
import me.owdding.lib.dev.DisplayTest
import me.owdding.lib.displays.*
import me.owdding.lib.layouts.BackgroundWidget
import me.owdding.lib.layouts.ExpandingWidget
import me.owdding.lib.layouts.asWidget
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.FrameLayout
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.net.URI

class MeowddingModsScreen : BaseCursorScreen(Text.of("Meowdding Mods")) {

    val background = MeowddingLib.id("background")
    val maxFeatureWidth by lazy { (MeowddingFeatures.features.flatMap { it.value }.maxOfOrNull { McFont.width(it) } ?: 0) + 5 }
    val modElementsWidth get() = width - maxFeatureWidth - 10

    override fun init() {
        val elements = MeowddingModsParser.mods.sortedByDescending { it.isInstalled }.map(::createElement)
        val maxElementsInRow = (modElementsWidth / elements.first().width).coerceAtLeast(1)
        val chunked = elements.chunked(maxElementsInRow)

        featureList().apply {
            setPosition(0, 0)
        }.visitWidgets(this::addRenderableWidget)

        LayoutFactory.vertical(3, 0.5f) {
            vertical(alignment = MIDDLE) {
                chunked.forEach { columns ->
                    horizontal {
                        columns.forEach { element -> widget(element) }
                    }
                }
            }
        }.apply {
            FrameLayout.centerInRectangle(this, maxFeatureWidth, 0, modElementsWidth, this@MeowddingModsScreen.height)
        }.visitWidgets(this::addRenderableWidget)
    }

    private fun createElement(mod: MeowddingMod): AbstractWidget {
        val height = (McClient.window.guiScaledHeight / 5).coerceIn(50, 100)
        val main = DisplayFactory.horizontal(5, Alignment.CENTER) {
            supplied {
                Displays.image(mod.uri, Displays.empty(height - 10, height - 10))
            }

            vertical {
                wrappedText(mod.name, height - 10, TextColor.PINK.toUInt(), true)
                wrappedText("Installed: ${if (mod.isInstalled) "§a✔" else "§c❌"}", height - 10, TextColor.GRAY.toUInt(), true)
            }
        }

        val withBackground = listOf(main, Displays.empty(height * 2, height - 10)).asLayer().withPadding(5).withBackground(0xAA000000u)

        val button = withBackground.asButtonLeft {
            if (mod.isInstalled) {
                McClient.setScreenAsync {
                    ResourcefulConfigScreen.getFactory(mod.configId).apply(this)
                }
            } else {
                McClient.openUri(URI("https://modrinth.com/mod/${mod.modrinthSlug}"))
            }
        }
        return ExpandingWidget(button, 5)
    }

    private val state: ListenableState<String> = ListenableState.of("")
    private lateinit var list: ListWidget

    private fun featureList() = BackgroundWidget(
        background,
        LayoutFactory.vertical {
            fun updateList(input: String = "") = LayoutFactory.vertical(5, 0.5f) {
                val features = MeowddingFeatures.features.filter { (mod, features) ->
                    input.isBlank() || mod.name.contains(input, ignoreCase = true) || features.any { it.contains(input, ignoreCase = true) }
                }

                spacer(2)
                features.forEach { (mod, features) ->
                    vertical(3, 0.5f) {
                        string(mod.name) {
                            color = TextColor.PINK
                        }

                        features.forEach { feature ->
                            if (!feature.contains(input, ignoreCase = true)) return@forEach
                            widget(
                                Displays.text(feature, color = { TextColor.GRAY.toUInt() }).asButtonLeft {
                                    val config = Configurations.INSTANCE.getConfig(mod.configId + "/config") ?: return@asButtonLeft
                                    McClient.setScreenAsync {
                                        ResourcefulConfigScreen.make(config)
                                            .withParent(this@MeowddingModsScreen)
                                            .withQuery(feature)
                                            .build()
                                    }
                                },
                            )
                        }
                        spacer(maxFeatureWidth)
                    }
                }
            }.apply {
                list.clear()
                list.add(this.asWidget())
                list.setSize(this.width, this@MeowddingModsScreen.height - 24)
            }

            textInput(
                state = state,
                placeholder = "Search for features...",
                width = if (this@MeowddingModsScreen::list.isInitialized) list.width else 150,
                onChange = { updateList(it) },
            )

            list = ListWidget(width, height).apply {
                withAutoFocus(false)
            }
            widget(list)
            updateList()
        },
        padding = 2,
    ).apply {
        setSize(this.width, this@MeowddingModsScreen.height)
    }


    @Module
    companion object {
        @Subscription
        fun onCommand(event: RegisterCommandsEvent) {
            event.registerWithCallback("meowdding") {
                McClient.runNextTick {
                    McClient.setScreen(MeowddingModsScreen())
                }
            }
            event.registerWithCallback("meowdding test") {
                McClient.runNextTick {
                    McClient.setScreen(DisplayTest)
                }
            }
        }
    }
}
