package me.owdding.lib.displays

import net.minecraft.client.gui.GuiGraphicsExtractor
import tech.thatgravyboat.skyblockapi.utils.extentions.translated

interface Display {

    fun getWidth(): Int
    fun getHeight(): Int


    fun extract(graphics: GuiGraphicsExtractor): Unit

    fun extract(graphics: GuiGraphicsExtractor, x: Int, y: Int, alignmentX: Float = 0f, alignmentY: Float = 0f) {
        graphics.translated((x - getWidth() * alignmentX).toInt(), (y - getHeight() * alignmentY).toInt()) {
            extract(graphics)
        }
    }
}
