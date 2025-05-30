package me.owdding.lib.compat.meowdding

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.builder.LayoutBuilder.Companion.setPos
import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asButton
import me.owdding.lib.displays.asLayer
import me.owdding.lib.layouts.ExpandingWidget
import net.minecraft.Util
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

class MeowddingModsScreen : Screen(Text.of("Meowdding Mods")) {

    override fun init() {
        val elements = MeowddingModsParser.mods.map(::createElement)
        val rows = elements.chunked(5).map { LayoutFactory.horizontal(1) { widget(it) } }
        LayoutFactory.vertical(1) {
            rows.forEach { it ->
                widget(it, LayoutSettings::alignHorizontallyCenter)
            }
        }.setPos(5, 5).visitWidgets(this::addRenderableWidget)
    }

    private fun createElement(mod: MeowddingMod): AbstractWidget {
        val height = (McClient.window.guiScaledHeight / 5).coerceIn(50, 100)

        val main = DisplayFactory.vertical {
            // TODO: multiline text based on width
            textDisplay(shadow = true) {
                append(mod.name)
                color = TextColor.PINK
            }
        }

        val layered = listOf(main, Displays.empty(height, height)).asLayer()

        val button = Displays.background(mod.iconImage, layered).asButton {
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
