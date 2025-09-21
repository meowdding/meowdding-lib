package me.owdding.lib.platform.screens

import net.minecraft.client.gui.components.events.GuiEventListener

expect interface GuiEventListenerPlatform : GuiEventListener {

    fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean): Boolean

    fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean

    fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean

    fun keyPressed(keyEvent: KeyEvent): Boolean

    fun keyReleased(keyEvent: KeyEvent): Boolean

    fun charTyped(characterEvent: CharacterEvent): Boolean

}
