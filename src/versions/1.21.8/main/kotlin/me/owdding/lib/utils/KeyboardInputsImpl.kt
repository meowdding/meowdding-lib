@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.utils

import me.owdding.lib.platform.screens.KeyEvent
import net.minecraft.client.KeyMapping
import net.minecraft.resources.ResourceLocation

internal actual fun keyMapping(translationKey: String, keyCode: Int, category: ResourceLocation): KeyMapping {
    return KeyMapping(translationKey, keyCode, category.toLanguageKey("key.category"))
}

actual fun KeyMapping.matches(event: KeyEvent): Boolean = this.matches(event.key, event.scancode)
