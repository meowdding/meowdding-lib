package me.owdding.lib.platform.screens

import net.minecraft.client.gui.components.events.GuiEventListener

actual interface GuiEventListenerPlatform : GuiEventListener {
    fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean): Boolean

    fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean

    fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean

    fun keyPressed(keyEvent: KeyEvent): Boolean

    fun keyReleased(keyEvent: KeyEvent): Boolean

    fun charTyped(characterEvent: CharacterEvent): Boolean

    override fun mouseClicked(x: Double, y: Double, button: Int): Boolean = mouseClicked(MouseButtonEvent(x, y, MouseButtonInfo(button, 0)), false)

    override fun mouseReleased(x: Double, y: Double, button: Int): Boolean = mouseReleased(MouseButtonEvent(x, y, MouseButtonInfo(button, 0)))

    override fun mouseDragged(x: Double, y: Double, button: Int, deltaX: Double, deltaY: Double): Boolean =
        mouseDragged(MouseButtonEvent(x, y, MouseButtonInfo(button, 0)), deltaX, deltaY)

    override fun keyPressed(key: Int, scancode: Int, modifier: Int): Boolean = keyPressed(KeyEvent(key, scancode, modifier))

    override fun keyReleased(key: Int, scancode: Int, modifier: Int): Boolean = keyPressed(KeyEvent(key, scancode, modifier))

    override fun charTyped(char: Char, modifiers: Int): Boolean = charTyped(CharacterEvent(char.code, modifiers))
}
