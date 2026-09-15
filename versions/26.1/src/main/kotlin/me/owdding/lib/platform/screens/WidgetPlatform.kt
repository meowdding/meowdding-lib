package me.owdding.lib.platform.screens

import net.minecraft.client.gui.components.events.GuiEventListener

fun GuiEventListener.mouseClicked(event: MouseButtonEvent, doubleClicked: Boolean): Boolean {
    return this.mouseClicked(event.into(), doubleClicked)
}

fun GuiEventListener.mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
    return this.mouseDragged(event.into(), deltaX, deltaY)

}

fun GuiEventListener.mouseReleased(event: MouseButtonEvent): Boolean {
    return this.mouseReleased(event.into())
}

fun GuiEventListener.keyPressed(event: KeyEvent): Boolean {
    return this.keyPressed(event.into())
}

fun GuiEventListener.keyReleased(event: KeyEvent): Boolean {
    return this.keyReleased(event.into())
}

fun GuiEventListener.charTyped(event: CharacterEvent): Boolean {
    return this.charTyped(event.into())
}
