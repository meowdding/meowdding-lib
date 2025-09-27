package me.owdding.lib.platform.screens

import net.minecraft.client.input.CharacterEvent as McCharacterEvent
import net.minecraft.client.input.KeyEvent as McKeyEvent
import net.minecraft.client.input.MouseButtonEvent as McMouseButtonEvent
import net.minecraft.client.input.MouseButtonInfo as McMouseButtonInfo

internal fun McMouseButtonInfo.into() = MouseButtonInfo(this.button(), this.modifiers())
internal fun McMouseButtonEvent.into() = MouseButtonEvent(this.x, this.y, this.buttonInfo().into())
internal fun McKeyEvent.into() = KeyEvent(this.key, this.scancode, this.modifiers)
internal fun McCharacterEvent.into() = CharacterEvent(this.codepoint, this.modifiers)

internal fun MouseButtonInfo.into() = McMouseButtonInfo(this.button, this.modifiers)
internal fun MouseButtonEvent.into() = McMouseButtonEvent(this.x, this.y, this.buttonInfo.into())
internal fun KeyEvent.into() = McKeyEvent(this.key, this.scancode, this.modifiers)
internal fun CharacterEvent.into() = McCharacterEvent(this.codepoint, this.modifiers)
