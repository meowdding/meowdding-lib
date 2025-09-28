package me.owdding.lib.platform.screens

import earth.terrarium.olympus.client.ui.Overlay
import net.minecraft.client.gui.screens.Screen
import net.msrandom.stub.Stub

@Stub
expect abstract class Overlay(parent: Screen?) : Overlay {

    open fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean): Boolean
    open fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean
    open fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean
    open fun keyPressed(keyEvent: KeyEvent): Boolean
    open fun keyReleased(keyEvent: KeyEvent): Boolean
    open fun charTyped(characterEvent: CharacterEvent): Boolean

}
