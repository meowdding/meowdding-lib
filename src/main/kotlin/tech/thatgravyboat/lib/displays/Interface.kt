package tech.thatgravyboat.lib.displays

import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.lib.extensions.translated

interface Display {

    fun getWidth(): Int
    fun getHeight(): Int

    fun render(graphics: GuiGraphics)

    fun render(graphics: GuiGraphics, x: Int, y: Int, alignmentX: Float = 0f, alignmentY: Float = 0f) {
        graphics.translated((x - getWidth() * alignmentX).toInt(), (y - getHeight() * alignmentY).toInt()) {
            render(graphics)
        }
    }
}