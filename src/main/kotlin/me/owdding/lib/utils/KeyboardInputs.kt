package me.owdding.lib.utils

import me.owdding.ktmodules.Module
import me.owdding.lib.platform.screens.KeyEvent
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.resources.ResourceLocation
import net.msrandom.stub.Stub
import org.lwjgl.glfw.GLFW
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyReleasedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent

@Stub
internal expect fun isDown(key: Int): Boolean

@Stub
internal expect fun isMouseKeyDown(key: Int): Boolean

data class KeyboardInputs(
    val symbols: Set<String>,
    val keys: Set<Int>,
    val mouseButtons: Set<Int>,
) {

    fun isDown(event: KeyEvent): Boolean {
        return isDown(event.key, event.scancode)
    }

    fun isDown(key: Int, scanCode: Int): Boolean {
        return key in keys || GLFW.glfwGetKeyName(key, scanCode) in symbols
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

@Stub
internal expect fun keyMapping(translationKey: String, keyCode: Int, category: ResourceLocation): KeyMapping

@Stub
expect fun KeyMapping.matches(event: KeyEvent): Boolean

open class MeowddingKeybind(
    category: ResourceLocation,
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

    val key: KeyMapping = KeyBindingHelper.registerKeyBinding(keyMapping(translationKey, keyCode, category))

    val isDown get() = key.isDown

    fun matches(keyCode: Int, scancode: Int) = key.matches(KeyEvent(keyCode, scancode, 0))
    fun matches(event: ScreenKeyReleasedEvent) = matches(event.key, event.scanCode)
    fun matches(event: ScreenKeyPressedEvent) = matches(event.key, event.scanCode)
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
