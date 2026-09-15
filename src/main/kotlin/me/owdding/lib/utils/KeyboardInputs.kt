package me.owdding.lib.utils

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import me.owdding.lib.platform.isMouseDown
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.input.*
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyReleasedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McScreen

//? < 26.3 {
//import me.owdding.lib.platform.screens.into
//import tech.thatgravyboat.skyblockapi.helpers.McClient
//? }
//~ if >= 26.3 'glfw.GLFW' -> 'sdl.SDLKeyboard'
import org.lwjgl.sdl.SDLKeyboard

internal fun isDown(key: Int): Boolean {
    return InputConstants.isKeyDown(
        //? < 26.3
        //McClient.window,
        key,
    )
}

internal fun isMouseKeyDown(button: Int): Boolean {
    return McScreen.isMouseDown(button)
}

data class KeyboardInputs(
    val symbols: Set<String>,
    val keys: Set<Int>,
    val mouseButtons: Set<Int>,
) {

    fun isDown(event: KeyEvent): Boolean {
        //~ if >= 26.3 'scancode' -> 'keycode()'
        return isDown(event.key, event.keycode())
    }

    fun isDown(key: Int, scanCode: Int): Boolean {
        //~ if >= 26.3 'GLFW.glfwGetKeyName(key, scanCode)' -> '(SDLKeyboard.SDL_GetKeyName(key) ?: return false)'
        return key in keys || (SDLKeyboard.SDL_GetKeyName(key) ?: return false) in symbols
    }

    fun isDown(): Boolean {
        return keys.any { isDown(it) } || mouseButtons.any { isMouseKeyDown(it) }
    }

    class Builder internal constructor() {
        internal val symbols = mutableSetOf<String>()
        internal val keys = mutableSetOf<Int>()
        internal val mouseButtons = mutableSetOf<Int>()

        fun withSymbol(symbol: String) = symbols.add(symbol)
        fun withKey(key: Int) = keys.add(key)
        fun withButton(button: Int) = mouseButtons.add(button)
    }
}

fun keys(action: KeyboardInputs.Builder.() -> Unit): KeyboardInputs {
    val builder = KeyboardInputs.Builder().also(action)
    return KeyboardInputs(
        symbols = builder.symbols,
        keys = builder.keys,
        mouseButtons = builder.mouseButtons,
    )
}

fun keysOf(vararg keys: Int) = KeyboardInputs(
    keys = keys.toSet(),
    mouseButtons = emptySet(),
    symbols = emptySet(),
)

fun keysOf(vararg symbols: String) = KeyboardInputs(
    keys = emptySet(),
    mouseButtons = emptySet(),
    symbols = symbols.toSet(),
)

private val categoryCache = mutableMapOf<Identifier, KeyMapping.Category>()

internal fun keyMapping(translationKey: String, keyCode: Int, category: Identifier): KeyMapping {
    val category = categoryCache.getOrPut(category) { KeyMapping.Category(category) }

    return KeyMapping(translationKey, keyCode, category)
}

//? < 26.3 {
/*fun KeyMapping.matches(event: KeyEvent): Boolean {
    return this.matches(event.into())
}
*///? }

open class MeowddingKeybind(
    category: Identifier,
    translationKey: String,
    keyCode: Int,
    private val allowMultipleExecutions: Boolean = false,
    private val runnable: (() -> Unit)? = null,
) {
    init {
        if (runnable != null) {
            knownKeybinds.add(this)
        }
    }

    val key: KeyMapping = KeyMappingHelper.registerKeyMapping(keyMapping(translationKey, keyCode, category))

    val isDown get() = key.isDown

    @JvmOverloads
    //~ if >= 26.3 ', scancode: Int = 0) = ' -> ') = ', 'key, scancode' -> 'key, 0'
    fun matches(key: Int) = this.key.matches(KeyEvent(key, 0, 0))

    //~ if >= 26.3 'key, event.scanCode)' -> 'key)'
    fun matches(event: ScreenKeyReleasedEvent) = matches(event.key)

    //~ if >= 26.3 'key, event.scanCode)' -> 'key)'
    fun matches(event: ScreenKeyPressedEvent) = matches(event.key)
    fun matches(event: KeyEvent) = key.matches(event)

    @Module
    companion object {
        private val knownKeybinds = mutableListOf<MeowddingKeybind>()

        @Subscription(event = [TickEvent::class])
        fun onTick() {
            knownKeybinds.forEach { keybind ->
                if (keybind.allowMultipleExecutions && keybind.isDown) {
                    keybind.runnable?.invoke()
                } else if (keybind.key.consumeClick()) {
                    keybind.runnable?.invoke()
                }
            }
        }
    }
}
