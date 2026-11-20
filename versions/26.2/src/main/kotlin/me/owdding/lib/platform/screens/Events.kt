package me.owdding.lib.platform.screens

import com.mojang.blaze3d.platform.InputConstants
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.helpers.McScreen

public data class CharacterEvent(
    val codepoint: Int,
    val modifiers: Int,
) {
    public fun codepointAsString(): String = Character.toString(codepoint)
    public fun isAllowedInChat(): Boolean = isAllowedChatCharacter(this.codepoint.toChar())

    public fun isAllowedChatCharacter(char: Char): Boolean {
        return char.code != 167 && char >= ' ' && char.code != 127
    }
}

public data class KeyEvent(
    val key: Int,
    val scancode: Int,
    override val modifiers: Int,
) : InputWithModifiers {
    override val input: Int get() = key
}

public data class MouseButtonInfo(
    val button: Int,
    override val modifiers: Int,
) : InputWithModifiers {
    override val input: Int get() = button
}

public data class MouseButtonEvent(
    val x: Double,
    val y: Double,
    val buttonInfo: MouseButtonInfo,
) : InputWithModifiers by buttonInfo {
    public constructor(x: Double, y: Double, button: Int) : this(x, y, MouseButtonInfo(button, 0))
    public val button: Int get() = buttonInfo.button
    public fun isLeftClick(): Boolean = button == 0
    @get:JvmName("getIsLeft")
    val isLeft: Boolean get() = button == 0
    public fun isRightClick(): Boolean = button == 1
    @get:JvmName("getIsRight")
    val isRight: Boolean get() = button == 1
    public fun isMiddleClick(): Boolean = button == 2
    @get:JvmName("getIsMiddle")
    val isMiddle: Boolean get() = button == 2
}

public interface InputWithModifiers {

    public val input: Int

    @get:ApiStatus.Internal
    public val modifiers: Int

    public fun isSelection(): Boolean =
        this.input == InputConstants.KEY_RETURN ||
            this.input == InputConstants.KEY_SPACE ||
            this.input == InputConstants.KEY_NUMPADENTER

    public fun isConfirmation(): Boolean =
        this.input == InputConstants.KEY_RETURN ||
            this.input == InputConstants.KEY_NUMPADENTER

    public fun isEscape(): Boolean =
        this.input == InputConstants.KEY_ESCAPE

    public fun isLeft(): Boolean =
        this.input == InputConstants.KEY_LEFT

    public fun isRight(): Boolean =
        this.input == InputConstants.KEY_RIGHT

    public fun isUp(): Boolean =
        this.input == InputConstants.KEY_UP

    public fun isDown(): Boolean =
        this.input == InputConstants.KEY_DOWN

    public fun isCycleFocus(): Boolean =
        this.input == InputConstants.KEY_TAB

    public fun getDigit(): Int {
        return if (this.input in InputConstants.KEY_0..InputConstants.KEY_9) {
            this.input - InputConstants.KEY_0
        } else {
            -1
        }
    }

    public fun hasAltDown(): Boolean =
        McScreen.isAltDown

    public fun hasShiftDown(): Boolean =
        McScreen.isShiftDown

    public fun hasControlDown(): Boolean =
        McScreen.isControlDown

    public fun isSelectAll(): Boolean {
        return this.input == InputConstants.KEY_A &&
            this.hasControlDown() &&
            !this.hasShiftDown() &&
            !this.hasAltDown()
    }

    public fun isCopy(): Boolean {
        return this.input == InputConstants.KEY_C &&
            this.hasControlDown() &&
            !this.hasShiftDown() &&
            !this.hasAltDown()
    }

    public fun isPaste(): Boolean {
        return this.input == InputConstants.KEY_V &&
            this.hasControlDown() &&
            !this.hasShiftDown() &&
            !this.hasAltDown()
    }

    public fun isCut(): Boolean {
        return this.input == InputConstants.KEY_X &&
            this.hasControlDown() &&
            !this.hasShiftDown() &&
            !this.hasAltDown()
    }
}
