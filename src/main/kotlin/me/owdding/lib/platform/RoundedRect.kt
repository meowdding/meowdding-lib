package me.owdding.lib.platform

import earth.terrarium.olympus.client.pipelines.RoundedRectangle
import net.minecraft.client.gui.GuiGraphics

fun GuiGraphics.drawRoundedRectangle(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    backgroundColor: UInt,
    borderColor: UInt,
    borderRadius: Float,
    borderWidth: Int,
) {
    //? 1.21.5 {
    /*RoundedRectangle.drawRelative(
    *///?} else
    RoundedRectangle.draw(
        this, x, y, width, height, backgroundColor.toInt(), borderColor.toInt(), borderRadius, borderWidth
    )
}
