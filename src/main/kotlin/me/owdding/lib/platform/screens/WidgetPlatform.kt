package me.owdding.lib.platform.screens

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.msrandom.stub.Stub

@Stub
expect fun GuiEventListener.mouseClicked(event: MouseButtonEvent, doubleClicked: Boolean): Boolean
@Stub
expect fun GuiEventListener.mouseReleased(event: MouseButtonEvent): Boolean
@Stub
expect fun GuiEventListener.mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean
@Stub
expect fun GuiEventListener.keyPressed(event: KeyEvent): Boolean
@Stub
expect fun GuiEventListener.keyReleased(event: KeyEvent): Boolean
@Stub
expect fun GuiEventListener.charTyped(event: CharacterEvent): Boolean
