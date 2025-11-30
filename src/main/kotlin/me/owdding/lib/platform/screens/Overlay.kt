package me.owdding.lib.platform.screens

import earth.terrarium.olympus.client.ui.Overlay
import net.minecraft.client.gui.screens.Screen

//? > 1.21.8 {
import net.minecraft.client.input.CharacterEvent as McCharacterEvent
import net.minecraft.client.input.KeyEvent as McKeyEvent
import net.minecraft.client.input.MouseButtonEvent as McMouseButtonEvent
//?}

abstract class Overlay(parent: Screen?) : Overlay(parent) {

    open fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean): Boolean {
        //? if 1.21.8 {
        /*return super.mouseClicked(mouseEvent.into(), doubleClicked)
        *///?}
    }
    open fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean {
        return super.mouseReleased(mouseEvent.into())
    }
    open fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        return super.mouseDragged(mouseEvent.into(), deltaX, deltaY)
    }
    open fun keyPressed(keyEvent: KeyEvent): Boolean {
        return super.keyPressed(keyEvent.into())
    }
    open fun keyReleased(keyEvent: KeyEvent): Boolean {
        return super.keyReleased(keyEvent.into())
    }
    open fun charTyped(characterEvent: CharacterEvent): Boolean {
        return super.charTyped(characterEvent.into())
    }

    //? if > 1.21.8 {
    override fun mouseClicked(event: McMouseButtonEvent, doubleClicked: Boolean) = this.mouseClicked(event.into(), doubleClicked)
    override fun mouseReleased(event: McMouseButtonEvent) = this.mouseReleased(event.into())
    override fun mouseDragged(event: McMouseButtonEvent, deltaX: Double, deltaY: Double) = this.mouseDragged(event.into(), deltaX, deltaY)
    override fun keyPressed(event: McKeyEvent) = this.keyPressed(event.into())
    override fun keyReleased(event: McKeyEvent) = this.keyReleased(event.into())
    override fun charTyped(event: McCharacterEvent) = this.charTyped(event.into())
    //?} else {
    //?}
}

