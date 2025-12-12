package me.owdding.lib.platform.screens

import net.minecraft.client.gui.components.events.GuiEventListener

fun GuiEventListener.mouseClicked(event: MouseButtonEvent, doubleClicked: Boolean): Boolean {
    //? if > 1.21.8 {
    return this.mouseClicked(event.into(), doubleClicked)
    //?} else
    /*return this.mouseClicked(event.x, event.y, event.button)*/
}

fun GuiEventListener.mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
    //? if > 1.21.8 {
    return this.mouseDragged(event.into(), deltaX, deltaY)
    //?} else
    /*return this.mouseDragged(event.x, event.y, event.button, deltaX, deltaY)*/

}

fun GuiEventListener.mouseReleased(event: MouseButtonEvent): Boolean {
    //? if > 1.21.8 {
    return this.mouseReleased(event.into())
    //?} else
    /*return this.mouseReleased(event.x, event.y, event.button)*/
}

fun GuiEventListener.keyPressed(event: KeyEvent): Boolean {
    //? if > 1.21.8 {
    return this.keyPressed(event.into())
    //?} else
    /*return this.keyPressed(event.key, event.scancode, event.modifiers)*/
}

fun GuiEventListener.keyReleased(event: KeyEvent): Boolean {
    //? if > 1.21.8 {
    return this.keyReleased(event.into())
    //?} else
    /*return this.keyReleased(event.key, event.scancode, event.modifiers)*/
}

fun GuiEventListener.charTyped(event: CharacterEvent): Boolean {
    //? if > 1.21.8 {
    return this.charTyped(event.into())
    //?} else
    /*return this.charTyped(event.codepoint.toChar(), event.modifiers)*/
}
