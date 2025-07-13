package me.owdding.lib.utils

import net.minecraft.WorldVersion

internal actual val WorldVersion.name: String
    get() = this.name()
