package me.owdding.lib.utils

import org.lwjgl.glfw.GLFW

data class KeyboardInputs(
    val symbols: Set<String>,
    val keys: Set<Int>,
) {

    fun isDown(key: Int, scanCode: Int): Boolean {
        return key in keys || GLFW.glfwGetKeyName(key, scanCode) in symbols
    }

    class Builder internal constructor() {
        internal val symbols = mutableSetOf<String>()
        internal val keys = mutableSetOf<Int>()

        fun withSymbol(symbol: String) = symbols.add(symbol)
        fun withKey(key: Int) = keys.add(key)
    }
}

fun keys(action: KeyboardInputs.Builder.() -> Unit): KeyboardInputs {
    val builder = KeyboardInputs.Builder().also(action)
    return KeyboardInputs(
        symbols = builder.symbols,
        keys = builder.keys,
    )
}

fun keysOf(vararg keys: Int) = KeyboardInputs(
    keys = keys.toSet(),
    symbols = emptySet(),
)

fun keysOf(vararg symbols: String) = KeyboardInputs(
    keys = emptySet(),
    symbols = symbols.toSet(),
)
