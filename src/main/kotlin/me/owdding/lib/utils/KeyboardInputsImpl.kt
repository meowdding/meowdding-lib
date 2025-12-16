@file:JvmName("KeyboardInputsImplKt")
package me.owdding.lib.utils

import me.owdding.lib.platform.screens.KeyEvent
import net.minecraft.client.KeyMapping

@Deprecated("This method is only here for backwards compatibility", level = DeprecationLevel.HIDDEN)
@JvmName("matches")
internal fun KeyMapping.oldMatches(event: KeyEvent): Boolean = this.matches(event)
