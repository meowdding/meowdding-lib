@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.utils

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.lib.platform.screens.KeyEvent
import me.owdding.lib.platform.screens.into
import net.minecraft.client.KeyMapping
import net.minecraft.resources.ResourceLocation
import org.lwjgl.glfw.GLFW
import tech.thatgravyboat.skyblockapi.helpers.McClient

private val categoryCache = mutableMapOf<ResourceLocation, KeyMapping.Category>()

internal actual fun keyMapping(translationKey: String, keyCode: Int, category: ResourceLocation): KeyMapping {
    return KeyMapping(translationKey, keyCode, categoryCache.getOrPut(category) { KeyMapping.Category(category) })
}

actual fun KeyMapping.matches(event: KeyEvent): Boolean = this.matches(event.into())

internal actual fun isDown(key: Int): Boolean = InputConstants.isKeyDown(McClient.window, key)

internal actual fun isMouseKeyDown(key: Int): Boolean = (GLFW.glfwGetMouseButton(McClient.window.handle(), key) == 1)
