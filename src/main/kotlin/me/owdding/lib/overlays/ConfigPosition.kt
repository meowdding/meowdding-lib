package me.owdding.lib.overlays

import com.teamresourceful.resourcefulconfigkt.api.ObjectKt

class ConfigPosition(x: Int, y: Int, scale: Float = 1f, alignment: OverlayAlignment = OverlayAlignment.START) : ObjectKt(), Position {
    val initialPosition = x to y

    override var x: Int by int("x", x)
    override var y: Int by int("y", y)
    override var scale: Float by float("scale", scale)
    override var alignment: OverlayAlignment by enum("alignment", alignment)

    override fun resetPosition() {
        val (x, y) = initialPosition
        this.x = x
        this.y = y
    }
}
