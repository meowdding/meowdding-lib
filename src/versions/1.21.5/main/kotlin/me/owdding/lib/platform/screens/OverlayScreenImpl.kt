@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.platform.screens

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import earth.terrarium.olympus.client.ui.Overlay
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

actual abstract class Overlay actual constructor(parent: Screen?) : Overlay(parent) {
    private var lastClickTime = 0L
    private var lastButton = 0

    actual open fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean): Boolean {
        return super.mouseClicked(mouseEvent.x, mouseEvent.y, mouseEvent.button)
    }

    actual open fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean {
        return super.mouseReleased(mouseEvent.x, mouseEvent.y, mouseEvent.button)
    }

    actual open fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        return super.mouseDragged(mouseEvent.x, mouseEvent.y, mouseEvent.button, deltaX, deltaY)
    }

    actual open fun keyPressed(keyEvent: KeyEvent): Boolean {
        return super.keyPressed(keyEvent.key, keyEvent.scancode, keyEvent.modifiers)
    }

    actual open fun keyReleased(keyEvent: KeyEvent): Boolean {
        return super.keyReleased(keyEvent.key, keyEvent.scancode, keyEvent.modifiers)
    }

    actual open fun charTyped(characterEvent: CharacterEvent): Boolean {
        return super.charTyped(characterEvent.codepoint.toChar(), characterEvent.modifiers)
    }

    override fun mouseClicked(x: Double, y: Double, button: Int): Boolean {
        val isDoubleClick = System.currentTimeMillis() - lastClickTime < 250 && lastButton == button
        lastButton = button
        lastClickTime = System.currentTimeMillis()
        return this.mouseClicked(mouseButtonEvent(x, y, button), isDoubleClick)
    }

    override fun mouseReleased(x: Double, y: Double, button: Int) = mouseReleased(mouseButtonEvent(x, y, button))
    override fun mouseDragged(x: Double, y: Double, button: Int, deltaX: Double, deltaY: Double) = mouseDragged(mouseButtonEvent(x, y, button), deltaX, deltaY)
    override fun keyPressed(key: Int, scancode: Int, modifier: Int) = keyPressed(KeyEvent(key, scancode, modifier))
    override fun keyReleased(key: Int, scancode: Int, modifier: Int) = keyPressed(KeyEvent(key, scancode, modifier))
    override fun charTyped(char: Char, modifiers: Int) = charTyped(CharacterEvent(char.code, modifiers))
}
