@file:JvmName("KeyboardInputsImplKt")
package me.owdding.lib.utils

import net.minecraft.client.KeyMapping
import net.minecraft.client.input.KeyEvent

@Deprecated("This method is only here for backwards compatibility", level = DeprecationLevel.HIDDEN)
@JvmName("matches")
internal fun KeyMapping.oldMatches(event: KeyEvent): Boolean = this.matches(event)
