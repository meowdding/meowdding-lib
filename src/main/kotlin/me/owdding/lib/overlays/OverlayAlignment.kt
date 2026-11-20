package me.owdding.lib.overlays

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

public enum class OverlayAlignment {
    START,
    CENTER,
    END,
    ;

    private val formattedName = toFormattedName()
    override fun toString(): String = formattedName
}
