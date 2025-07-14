package me.owdding.lib.displays

import me.owdding.lib.displays.circle.TexturedCircleRenderer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix3x2f

class TexturedCircleDisplay(@JvmField val width: Int, @JvmField val height: Int, private val texture: ResourceLocation) : Display {
    override fun getHeight(): Int = height

    override fun getWidth(): Int = width

    override fun render(graphics: GuiGraphics) {
        graphics.guiRenderState.submitPicturesInPictureState(TexturedCircleRenderer.State(
            0, 0, width, height, texture, Matrix3x2f(graphics.pose()), graphics.scissorStack.peek(),
            PictureInPictureRenderState.getBounds(0, 0, width, height, graphics.scissorStack.peek())
        ))
    }
}
