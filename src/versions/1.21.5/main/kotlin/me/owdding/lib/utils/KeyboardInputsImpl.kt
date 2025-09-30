@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.utils

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.lib.platform.screens.KeyEvent
import net.minecraft.client.KeyMapping
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.helpers.McClient

internal actual fun keyMapping(translationKey: String, keyCode: Int, category: ResourceLocation): KeyMapping {
    return KeyMapping(translationKey, keyCode, category.toLanguageKey("key.category"))
}

actual fun KeyMapping.matches(event: KeyEvent): Boolean = this.matches(event.key, event.scancode)

internal actual fun isDown(key: Int): Boolean = InputConstants.isKeyDown(McClient.window.window, key)
