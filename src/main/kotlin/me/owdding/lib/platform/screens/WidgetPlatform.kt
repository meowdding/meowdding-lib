package me.owdding.lib.platform.screens

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.events.GuiEventListener

expect fun GuiEventListener.mouseClicked(event: MouseButtonEvent, doubleClicked: Boolean): Boolean
expect fun GuiEventListener.mouseReleased(event: MouseButtonEvent): Boolean
expect fun GuiEventListener.mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean
expect fun GuiEventListener.keyPressed(event: KeyEvent): Boolean
expect fun GuiEventListener.keyReleased(event: KeyEvent): Boolean
expect fun GuiEventListener.charTyped(event: CharacterEvent): Boolean
