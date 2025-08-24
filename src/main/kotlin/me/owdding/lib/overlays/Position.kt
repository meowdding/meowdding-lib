package me.owdding.lib.overlays

import tech.thatgravyboat.skyblockapi.helpers.McClient

interface Position {
    var x: Int
    var y: Int
    var scale: Float

    operator fun component1(): Int = if (x < 0) McClient.window.guiScaledWidth + x else x
    operator fun component2(): Int = if (y < 0) McClient.window.guiScaledHeight + y else y
    operator fun component3(): Float = scale

    fun isRight(): Boolean = x < 0
    fun isBottom(): Boolean = y < 0

    fun resetPosition()
}
