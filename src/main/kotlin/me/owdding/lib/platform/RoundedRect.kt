package me.owdding.lib.platform

import net.minecraft.client.gui.GuiGraphics
import net.msrandom.stub.Stub

@Stub
expect fun GuiGraphics.drawRoundedRectange(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    backgroundColor: UInt,
    borderColor: UInt,
    borderRadius: Float,
    borderWidth: Int,
)
