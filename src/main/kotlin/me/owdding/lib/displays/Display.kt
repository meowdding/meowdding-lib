package me.owdding.lib.displays

import net.minecraft.client.gui.GuiGraphicsExtractor
import tech.thatgravyboat.skyblockapi.utils.extentions.translated

interface Display {

    fun getWidth(): Int
    fun getHeight(): Int

    //? < 26.1 {
    @Deprecated(message = "Outdated naming", replaceWith = ReplaceWith("extract(graphics)"), level = DeprecationLevel.ERROR)
    fun render(graphics: GuiGraphicsExtractor) = Unit
    //? }

    fun extract(graphics: GuiGraphicsExtractor) = @Suppress("DEPRECATION_ERROR") this.render(graphics)

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
