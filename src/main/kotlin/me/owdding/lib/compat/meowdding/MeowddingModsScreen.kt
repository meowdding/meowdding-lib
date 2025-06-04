package me.owdding.lib.compat.meowdding

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.displays.*
import me.owdding.lib.layouts.ExpandingWidget
import net.minecraft.Util
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

class MeowddingModsScreen : Screen(Text.of("Meowdding Mods")) {

    override fun init() {
        val elements = MeowddingModsParser.mods.sortedByDescending { it.isInstalled }.map(::createElement)

        LayoutFactory.vertical(3, 0.5f) {
            horizontal { elements.forEach { widget(it) } }
        }.apply {
            FrameLayout.centerInRectangle(this, 0, 0, this@MeowddingModsScreen.width, this@MeowddingModsScreen.height)
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

        val button = withBackground.asButton {
            if (mod.isInstalled) {
                McClient.setScreenAsync(ResourcefulConfigScreen.getFactory(mod.configId).apply(this))
            } else {
                Util.getPlatform().openUri("https://modrinth.com/mod/${mod.modrinthSlug}")
            }
        }
        return ExpandingWidget(button, 5)
    }


    @Module
    companion object {
        @Subscription
        fun onCommand(event: RegisterCommandsEvent) {
            event.registerWithCallback("meowdding") {
                McClient.setScreenAsync(MeowddingModsScreen())
            }
        }
    }
}
