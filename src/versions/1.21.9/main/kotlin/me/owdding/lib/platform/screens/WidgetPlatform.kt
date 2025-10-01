@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.platform.screens

import net.minecraft.client.gui.components.events.GuiEventListener

actual fun GuiEventListener.mouseClicked(event: MouseButtonEvent, doubleClicked: Boolean): Boolean = this.mouseClicked(event.into(), doubleClicked)
actual fun GuiEventListener.mouseReleased(event: MouseButtonEvent): Boolean = this.mouseReleased(event.into())
actual fun GuiEventListener.mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean = this.mouseDragged(event.into(), deltaX, deltaY)
actual fun GuiEventListener.keyPressed(event: KeyEvent): Boolean = this.keyPressed(event.into())
actual fun GuiEventListener.keyReleased(event: KeyEvent): Boolean = this.keyReleased(event.into())
actual fun GuiEventListener.charTyped(event: CharacterEvent): Boolean = this.charTyped(event.into())
