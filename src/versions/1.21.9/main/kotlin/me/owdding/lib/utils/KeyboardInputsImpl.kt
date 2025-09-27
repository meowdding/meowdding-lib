@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.utils

import me.owdding.lib.platform.screens.KeyEvent
import me.owdding.lib.platform.screens.into
import net.minecraft.client.KeyMapping
import net.minecraft.resources.ResourceLocation

private val categoryCache = mutableMapOf<ResourceLocation, KeyMapping.Category>()

internal actual fun keyMapping(translationKey: String, keyCode: Int, category: ResourceLocation): KeyMapping {
    return KeyMapping(translationKey, keyCode, categoryCache.getOrPut(category) { KeyMapping.Category(category) })
}

actual fun KeyMapping.matches(event: KeyEvent): Boolean = this.matches(event.into())
