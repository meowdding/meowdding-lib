package me.owdding.lib.displays

import net.minecraft.client.gui.GuiGraphicsExtractor
import tech.thatgravyboat.skyblockapi.utils.extentions.translated

interface Display {

    fun getWidth(): Int
    fun getHeight(): Int

    //? < 26.1 {
    /*@Deprecated(message = "Outdated naming", replaceWith = ReplaceWith("extract(graphics)"), level = DeprecationLevel.ERROR)
    fun render(graphics: GuiGraphicsExtractor): Unit = this.extract(graphics)
    *///? }

    fun extract(graphics: GuiGraphicsExtractor): Unit
        //? < 26.1
        //= @Suppress("DEPRECATION_ERROR") this.render(graphics)

    //? < 26.1 {
    /*@Deprecated(message = "Outdated naming", replaceWith = ReplaceWith("extract"))
    fun render(graphics: GuiGraphicsExtractor, x: Int, y: Int, alignmentX: Float = 0f, alignmentY: Float = 0f): Unit = _extract(graphics, x, y, alignmentX, alignmentY)
    *///? }
    fun extract(graphics: GuiGraphicsExtractor, x: Int, y: Int, alignmentX: Float = 0f, alignmentY: Float = 0f) {
        @Suppress("DEPRECATION")
        //~ if >= 26.1 'render' -> '_extract'
        _extract(graphics, x, y, alignmentX, alignmentY)
    }
}

private fun Display._extract(graphics: GuiGraphicsExtractor, x: Int, y: Int, alignmentX: Float = 0f, alignmentY: Float = 0f) {
    graphics.translated((x - getWidth() * alignmentX).toInt(), (y - getHeight() * alignmentY).toInt()) {
        extract(graphics)
    }
}
