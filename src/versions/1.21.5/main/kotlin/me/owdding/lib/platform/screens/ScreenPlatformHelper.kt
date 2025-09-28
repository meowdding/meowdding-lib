package me.owdding.lib.platform.screens

internal fun mouseButtonInfo(button: Int) = MouseButtonInfo(button, 0)
internal fun mouseButtonEvent(x: Double, y: Double, button: Int) = MouseButtonEvent(x, y, mouseButtonInfo(button))
internal fun keyEvent(key: Char, scancode: Int, modifiers: Int) = KeyEvent(key.code, scancode, modifiers)
internal fun characterEvent(key: Char, modifiers: Int) = CharacterEvent(key.code, modifiers)
