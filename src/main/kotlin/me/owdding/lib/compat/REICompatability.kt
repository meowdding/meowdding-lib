package me.owdding.lib.compat

import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

class REIRenderOverlayEvent(val screen: Screen, private val registrar: (Int, Int, Int, Int) -> Unit) : CancellableSkyBlockEvent() {

    fun register(x: Int, y: Int, width: Int, height: Int) {
        registrar(x, y, width, height)
    }

    fun register(layout: LayoutElement) = register(layout.x, layout.y, layout.width, layout.height)
}

object REICompatability : REIClientPlugin {

    override fun registerExclusionZones(zones: ExclusionZones) {
        zones.register(Screen::class.java) { screen ->
            val areas = mutableListOf<Rectangle>()
            val hide = REIRenderOverlayEvent(screen) { x, y, width, height ->
                areas.add(Rectangle(x, y, width, height))
            }.post(SkyBlockAPI.eventBus)

            if (hide) listOf(Rectangle(0, 0, screen.width, screen.height)) else areas
        }
    }
}
