package me.owdding.lib.displays

import net.minecraft.client.gui.GuiGraphicsExtractor
import tech.thatgravyboat.skyblockapi.utils.extentions.translated

interface Display {

    fun getWidth(): Int
    fun getHeight(): Int

    //? < 26.1 {
    @Deprecated(message = "Outdated naming", replaceWith = ReplaceWith("extract"))
    fun render(graphics: GuiGraphicsExtractor) = extract(graphics)
    //? }
    fun extract(graphics: GuiGraphicsExtractor)

    //? < 26.1 {
    @Deprecated(message = "Outdated naming", replaceWith = ReplaceWith("extract"))
    fun render(graphics: GuiGraphicsExtractor, x: Int, y: Int, alignmentX: Float = 0f, alignmentY: Float = 0f) = extract(graphics, x, y, alignmentX, alignmentY)
    //? }
    fun extract(graphics: GuiGraphicsExtractor, x: Int, y: Int, alignmentX: Float = 0f, alignmentY: Float = 0f) {
        graphics.translated((x - getWidth() * alignmentX).toInt(), (y - getHeight() * alignmentY).toInt()) {
            extract(graphics)
        }
    }
}
