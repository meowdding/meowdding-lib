package me.owdding.lib.platform

import earth.terrarium.olympus.client.pipelines.RoundedRectangle
import net.minecraft.client.gui.GuiGraphics

actual fun GuiGraphics.drawRoundedRectange(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    backgroundColor: UInt,
    borderColor: UInt,
    borderRadius: Float,
    borderWidth: Int,
) {
    RoundedRectangle.draw(this, x, y, width, height, backgroundColor.toInt(), borderColor.toInt(), borderRadius, borderWidth)
}
