package me.owdding.lib.platform.screens

import com.mojang.blaze3d.platform.InputConstants
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.helpers.McScreen

data class CharacterEvent(
    val codepoint: Int,
    val modifiers: Int,
) {
    fun codepointAsString(): String = Character.toString(codepoint)
    fun isAllowedInChat() = isAllowedChatCharacter(this.codepoint.toChar())

    fun isAllowedChatCharacter(char: Char): Boolean {
        return char.code != 167 && char >= ' ' && char.code != 127
    }
}

data class KeyEvent(
    val key: Int,
    val scancode: Int,
    override val modifiers: Int,
) : InputWithModifiers {
    override val input: Int get() = key
}

data class MouseButtonInfo(
    val button: Int,
    override val modifiers: Int,
) : InputWithModifiers {
    override val input: Int get() = button
}

data class MouseButtonEvent(
    val x: Double,
    val y: Double,
    val buttonInfo: MouseButtonInfo,
) : InputWithModifiers by buttonInfo {
    constructor(x: Double, y: Double, button: Int) : this(x, y, MouseButtonInfo(button, 0))
    val button get() = buttonInfo.button
    fun isLeftClick() = button == 0
    fun isRightClick() = button == 1
    fun isMiddleClick() = button == 2
}


interface InputWithModifiers {

    val input: Int

    @get:ApiStatus.Internal
    val modifiers: Int

    fun isSelection() = this.input == InputConstants.KEY_RETURN || this.input == InputConstants.KEY_SPACE || this.input == InputConstants.KEY_NUMPADENTER
    fun isConfirmation() = this.input == InputConstants.KEY_RETURN || this.input == InputConstants.KEY_NUMPADENTER
    fun isEscape() = this.input == InputConstants.KEY_ESCAPE
    fun isLeft() = this.input == InputConstants.KEY_LEFT
    fun isRight() = this.input == InputConstants.KEY_RIGHT
    fun isUp() = this.input == InputConstants.KEY_UP
    fun isDown() = this.input == InputConstants.KEY_DOWN
    fun isCycleFocus() = this.input == InputConstants.KEY_TAB

    fun getDigit(): Int {
        return if (this.input in InputConstants.KEY_0..InputConstants.KEY_9) this.input - InputConstants.KEY_0 else -1
    }

    fun hasAltDown() = McScreen.isAltDown
    fun hasShiftDown() = McScreen.isShiftDown
    fun hasControlDown() = McScreen.isControlDown

    fun isSelectAll(): Boolean {
        return this.input == InputConstants.KEY_A && this.hasControlDown() && !this.hasShiftDown() && !this.hasAltDown()
    }

    fun isCopy(): Boolean {
        return this.input == InputConstants.KEY_C && this.hasControlDown() && !this.hasShiftDown() && !this.hasAltDown()
    }

    fun isPaste(): Boolean {
        return this.input == InputConstants.KEY_V && this.hasControlDown() && !this.hasShiftDown() && !this.hasAltDown()
    }

    fun isCut(): Boolean {
        return this.input == InputConstants.KEY_X && this.hasControlDown() && !this.hasShiftDown() && !this.hasAltDown()
    }

}
