package me.owdding.lib.platform.screens

import earth.terrarium.olympus.client.ui.Overlay
import net.minecraft.client.gui.screens.Screen

//? > 1.21.8 {
import net.minecraft.client.input.CharacterEvent as McCharacterEvent
import net.minecraft.client.input.KeyEvent as McKeyEvent
import net.minecraft.client.input.MouseButtonEvent as McMouseButtonEvent
//?}

abstract class Overlay(parent: Screen?) : Overlay(parent) {

    //? if < 1.21.9 {
    /*private var lastClickTime = 0L
    private var lastButton = 0
    *///?}

    open fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean = false): Boolean {
        //? if > 1.21.8 {
        return super.mouseClicked(mouseEvent.into(), doubleClicked)
        //?} else
        /*return super.mouseClicked(mouseEvent.x, mouseEvent.y, mouseEvent.button)*/
    }
    open fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean {
        //? if > 1.21.8 {
        return super.mouseReleased(mouseEvent.into())
        //?} else
        /*return super.mouseReleased(mouseEvent.x, mouseEvent.y, mouseEvent.button)*/
    }
    open fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        //? if > 1.21.8 {
        return super.mouseDragged(mouseEvent.into(), deltaX, deltaY)
        //?} else
        /*return super.mouseDragged(mouseEvent.x, mouseEvent.y, mouseEvent.button, deltaX, deltaY)*/
    }
    open fun keyPressed(keyEvent: KeyEvent): Boolean {
        //? if > 1.21.8 {
        return super.keyPressed(keyEvent.into())
        //?} else
        /*return super.keyPressed(keyEvent.key, keyEvent.scancode, keyEvent.modifiers)*/
    }
    open fun keyReleased(keyEvent: KeyEvent): Boolean {
        //? if > 1.21.8 {
        return super.keyReleased(keyEvent.into())
        //?} else
        /*return super.keyReleased(keyEvent.key, keyEvent.scancode, keyEvent.modifiers)*/
    }
    open fun charTyped(characterEvent: CharacterEvent): Boolean {
        //? if > 1.21.8 {
        return super.charTyped(characterEvent.into())
        //?} else
        /*return super.charTyped(characterEvent.codepoint.toChar(), characterEvent.modifiers)*/
    }

    //? if > 1.21.8 {
    override fun mouseClicked(event: McMouseButtonEvent, doubleClicked: Boolean) = this.mouseClicked(event.into(), doubleClicked)
    override fun mouseReleased(event: McMouseButtonEvent) = this.mouseReleased(event.into())
    override fun mouseDragged(event: McMouseButtonEvent, deltaX: Double, deltaY: Double) = this.mouseDragged(event.into(), deltaX, deltaY)
    override fun keyPressed(event: McKeyEvent) = this.keyPressed(event.into())
    override fun keyReleased(event: McKeyEvent) = this.keyReleased(event.into())
    override fun charTyped(event: McCharacterEvent) = this.charTyped(event.into())
    //?} else {
    /*override fun mouseClicked(x: Double, y: Double, button: Int): Boolean {
        val isDoubleClick = System.currentTimeMillis() - lastClickTime < 250 && lastButton == button
        lastButton = button
        lastClickTime = System.currentTimeMillis()
        return this.mouseClicked(mouseButtonEvent(x, y, button), isDoubleClick)
    }

    override fun mouseReleased(x: Double, y: Double, button: Int) = mouseReleased(mouseButtonEvent(x, y, button))
    override fun mouseDragged(x: Double, y: Double, button: Int, deltaX: Double, deltaY: Double) = mouseDragged(mouseButtonEvent(x, y, button), deltaX, deltaY)
    override fun keyPressed(key: Int, scancode: Int, modifier: Int) = keyPressed(KeyEvent(key, scancode, modifier))
    override fun keyReleased(key: Int, scancode: Int, modifier: Int) = keyReleased(KeyEvent(key, scancode, modifier))
    override fun charTyped(char: Char, modifiers: Int) = charTyped(CharacterEvent(char.code, modifiers))
    *///?}
}

