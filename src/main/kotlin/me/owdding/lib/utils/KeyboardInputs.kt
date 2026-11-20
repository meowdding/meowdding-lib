package me.owdding.lib.utils

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import me.owdding.lib.platform.isMouseDown
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.input.KeyEvent
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyReleasedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McScreen

//? if < 26.3 {
/*import me.owdding.lib.platform.screens.into
import me.owdding.lib.platform.screens.KeyEvent as MeowddingKeyEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
*///? }
//~ if >= 26.3 'glfw.GLFW' -> 'sdl.SDLKeyboard'
import org.lwjgl.sdl.SDLKeyboard

internal fun isDown(key: Int): Boolean {
    return InputConstants.isKeyDown(
        //? if < 26.3
        //McClient.window,
        key,
    )
}

internal fun isMouseKeyDown(button: Int): Boolean {
    return McScreen.isMouseDown(button)
}

public data class KeyboardInputs(
    val symbols: Set<String>,
    val keys: Set<Int>,
    val mouseButtons: Set<Int>,
) {

    public fun isDown(event: KeyEvent): Boolean {
        //~ if >= 26.3 'scancode' -> 'keycode()'
        return isDown(event.key, event.keycode())
    }

    //? if < 26.3
    //public fun isDown(event: MeowddingKeyEvent): Boolean = isDown(event.key, event.scancode)

    public fun isDown(key: Int, scanCode: Int): Boolean {
        //~ if >= 26.3 'GLFW.glfwGetKeyName(key, scanCode)' -> '(SDLKeyboard.SDL_GetKeyName(key) ?: return false)'
        return key in keys || (SDLKeyboard.SDL_GetKeyName(key) ?: return false) in symbols
    }

    public fun isDown(): Boolean {
        return keys.any { isDown(it) } || mouseButtons.any { isMouseKeyDown(it) }
    }

    public class Builder internal constructor() {
        internal val symbols = mutableSetOf<String>()
        internal val keys = mutableSetOf<Int>()
        internal val mouseButtons = mutableSetOf<Int>()

        public fun withSymbol(symbol: String): Boolean = symbols.add(symbol)
        public fun withKey(key: Int): Boolean = keys.add(key)
        public fun withButton(button: Int): Boolean = mouseButtons.add(button)
    }
}

public fun keys(action: KeyboardInputs.Builder.() -> Unit): KeyboardInputs {
    val builder = KeyboardInputs.Builder().also(action)
    return KeyboardInputs(
        symbols = builder.symbols,
        keys = builder.keys,
        mouseButtons = builder.mouseButtons,
    )
}

public fun keysOf(vararg keys: Int): KeyboardInputs = KeyboardInputs(
    keys = keys.toSet(),
    mouseButtons = emptySet(),
    symbols = emptySet(),
)

public fun keysOf(vararg symbols: String): KeyboardInputs = KeyboardInputs(
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
/*public fun KeyMapping.matches(event: MeowddingKeyEvent): Boolean {
    return this.matches(event.into())
}
*///? }

public open class MeowddingKeybind(
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

    public val key: KeyMapping = KeyMappingHelper.registerKeyMapping(keyMapping(translationKey, keyCode, category))

    public val isDown: Boolean get() = key.isDown

    //? if >= 26.3
    public fun matches(key: Int): Boolean = this.key.matches(KeyEvent(key, 0, 0))
    //? else {
    /*@JvmOverloads
    public fun matches(key: Int, scancode: Int = 0): Boolean = this.key.matches(KeyEvent(key, scancode, 0))
    *///?}

    //~ if >= 26.3 'key, event.scanCode)' -> 'key)'
    public fun matches(event: ScreenKeyReleasedEvent): Boolean = matches(event.key)

    //~ if >= 26.3 'key, event.scanCode)' -> 'key)'
    public fun matches(event: ScreenKeyPressedEvent): Boolean = matches(event.key)
    public fun matches(event: KeyEvent): Boolean = key.matches(event)

    //? if < 26.3
    //public fun matches(event: MeowddingKeyEvent): Boolean = key.matches(event)

    @Module
    public companion object {
        private val knownKeybinds = mutableListOf<MeowddingKeybind>()

        @Subscription(event = [TickEvent::class])
        private fun onTick() {
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
