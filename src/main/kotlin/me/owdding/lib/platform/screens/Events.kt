package me.owdding.lib.platform.screens

import net.minecraft.Util

data class CharacterEvent(
    val codepoint: Int,
    val modifiers: Int,
) {
    fun codepointAsString() = Char(codepoint)
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
    val button get() = buttonInfo.button
}


interface InputWithModifiers {

    val input: Int
    val modifiers: Int


    fun isSelection(): Boolean {
        return this.input == 257 || this.input == 32 || this.input == 335
    }

    fun isConfirmation(): Boolean {
        return this.input == 257 || this.input == 335
    }

    fun isEscape(): Boolean {
        return this.input == 256
    }

    fun isLeft(): Boolean {
        return this.input == 263
    }

    fun isRight(): Boolean {
        return this.input == 262
    }

    fun isUp(): Boolean {
        return this.input == 265
    }

    fun isDown(): Boolean {
        return this.input == 264
    }

    fun isCycleFocus(): Boolean {
        return this.input == 258
    }

    fun getDigit(): Int {
        val digitOffset: Int = this.input - 48
        return if (digitOffset in 0..9) digitOffset else -1
    }

    fun hasAltDown(): Boolean {
        return (this.modifiers and 4) != 0
    }

    fun hasShiftDown(): Boolean {
        return (this.modifiers and 1) != 0
    }

    fun hasControlDown(): Boolean {
        return (this.modifiers and (if (Util.getPlatform() == Util.OS.OSX) 8 else 2)) != 0
    }

    fun isSelectAll(): Boolean {
        return this.input == 65 && this.hasControlDown() && !this.hasShiftDown() && !this.hasAltDown()
    }

    fun isCopy(): Boolean {
        return this.input == 67 && this.hasControlDown() && !this.hasShiftDown() && !this.hasAltDown()
    }

    fun isPaste(): Boolean {
        return this.input == 86 && this.hasControlDown() && !this.hasShiftDown() && !this.hasAltDown()
    }

    fun isCut(): Boolean {
        return this.input == 88 && this.hasControlDown() && !this.hasShiftDown() && !this.hasAltDown()
    }

}
