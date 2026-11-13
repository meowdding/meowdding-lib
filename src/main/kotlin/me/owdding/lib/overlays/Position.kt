package me.owdding.lib.overlays

import tech.thatgravyboat.skyblockapi.helpers.McClient

public interface Position {
    public var x: Int
    public var y: Int
    public var scale: Float
    public var alignment: OverlayAlignment

    public operator fun component1(): Int = if (x < 0) McClient.window.guiScaledWidth + x else x
    public operator fun component2(): Int = if (y < 0) McClient.window.guiScaledHeight + y else y
    public operator fun component3(): Float = scale

    public fun isRight(): Boolean = x < 0
    public fun isBottom(): Boolean = y < 0

    public fun resetPosition()
}
