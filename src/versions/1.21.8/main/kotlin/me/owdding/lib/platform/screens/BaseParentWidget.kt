@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.platform.screens

import earth.terrarium.olympus.client.components.base.BaseParentWidget


actual abstract class BaseParentWidget : BaseParentWidget {
    actual constructor() : super()
    actual constructor(width: Int, height: Int) : super(width, height)

    private var lastClickTime = 0L
    private var lastButton = 0

    actual open fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {}
    actual open fun onRelease(event: MouseButtonEvent) {}
    actual open fun onDrag(event: MouseButtonEvent, deltaX: Double, deltaY: Double) {}

    actual open fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean = super.mouseClicked(event.x, event.y, event.button)
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val isDoubleClick = System.currentTimeMillis() - lastClickTime < 250 && lastButton == button
        lastButton = button
        lastClickTime = System.currentTimeMillis()
        return this.mouseClicked(mouseButtonEvent(mouseX, mouseY, button), isDoubleClick)
    }

    actual open fun mouseReleased(event: MouseButtonEvent): Boolean = super.mouseReleased(event.x, event.y, event.button)
    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return this.mouseReleased(mouseButtonEvent(mouseX, mouseY, button))
    }

    actual open fun mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        return super.mouseDragged(event.x, event.y, event.button, deltaX, deltaY)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        return this.mouseDragged(mouseButtonEvent(mouseX, mouseY, button), dragX, dragY)
    }

    protected actual open fun isValidClickButton(info: MouseButtonInfo): Boolean = super.isValidClickButton(info.button)
    override fun isValidClickButton(button: Int): Boolean = this.isValidClickButton(MouseButtonInfo(button, 0))

    actual open fun keyPressed(event: KeyEvent): Boolean = super.keyPressed(event.key, event.scancode, event.modifiers)
    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = keyPressed(keyEvent(keyCode.toChar(), scanCode, modifiers))

    actual open fun keyReleased(event: KeyEvent): Boolean = super.keyReleased(event.key, event.scancode, event.modifiers)
    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) = this.keyReleased(keyEvent(keyCode.toChar(), scanCode, modifiers))

    actual open fun charTyped(event: CharacterEvent): Boolean = super.charTyped(event.codepoint.toChar(), event.modifiers)
    override fun charTyped(codePoint: Char, modifiers: Int) = this.charTyped(characterEvent(codePoint, modifiers))
}
