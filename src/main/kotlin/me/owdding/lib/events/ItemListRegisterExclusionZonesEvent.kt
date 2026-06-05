package me.owdding.lib.events

import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

data class ItemListRegisterExclusionZonesEvent(val screen: Screen, private val registrar: (Int, Int, Int, Int) -> Unit) : CancellableSkyBlockEvent() {

    fun register(x: Int, y: Int, width: Int, height: Int) {
        registrar(x, y, width, height)
    }

    fun register(layout: LayoutElement) = register(layout.x, layout.y, layout.width, layout.height)
}
