package me.owdding.lib.displays

import me.owdding.lib.displays.circle.TexturedCircleState
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

internal actual fun roundedTextureDisplay(width: Int, height: Int, texture: ResourceLocation): Display = TexturedCircleDisplay(width, height, texture)

class TexturedCircleDisplay(@JvmField val width: Int, @JvmField val height: Int, private val texture: ResourceLocation) : Display {
    override fun getHeight(): Int = height
    override fun getWidth(): Int = width

    override fun render(graphics: GuiGraphics) {
        graphics.guiRenderState.submitPicturesInPictureState(
            TexturedCircleState(
                0, 0,
                width, height,
                graphics.scissorStack.peek(),
                graphics.pose(),
                texture,
            ),
        )
    }
}
