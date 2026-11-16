package me.owdding.lib.rendering

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import me.owdding.lib.MeowddingLib.id
import me.owdding.lib.extensions.withShaderDefine
import me.owdding.lib.utils.GradientDirection
import me.owdding.lib.utils.GradientProvider
import me.owdding.lib.utils.MemoizeUtil
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenPosition
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
import net.minecraft.util.ARGB
import org.joml.Matrix3x2f
import org.joml.Vector4f
import tech.thatgravyboat.skyblockapi.helpers.McClient

object GuiRenderTypes {

    private val GUI_TEXTURED_GRADIENT = MemoizeUtil.memoize { colors: GradientProvider, speed: Float, direction: GradientDirection, grayscale: Boolean ->
        RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
                .withLocation(id("gui_textured_gradient"))
                .withFragmentShader(id("gui/tex_gradient"))
                .withShaderDefine(
                    "COLORS",
                    colors
                        .getColors()
                        .map { color -> Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color)) }
                        .toTypedArray(),
                )
                .withShaderDefine("DIRECTION", direction.vec)
                .withShaderDefine("SPEED", speed)
                .apply {
                    if (grayscale) withShaderDefine("GRAYSCALE")
                }
                .build(),
        )
    }

    private val GUI_GRADIENT = MemoizeUtil.memoize { colors: GradientProvider, speed: Float, direction: GradientDirection ->
        RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
                .withLocation(id("gui_textured_gradient"))
                .withFragmentShader(id("gui/gradient"))
                .withShaderDefine(
                    "COLORS",
                    colors
                        .getColors()
                        .map { color -> Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color)) }
                        .toTypedArray(),
                )
                .withShaderDefine("DIRECTION", direction.vec)
                .withShaderDefine("SPEED", speed)
                .build(),
        )
    }

    fun GuiGraphicsExtractor.animatedGradient(
        x: Int, y: Int,
        width: Int, height: Int,
        speed: Float = 1f, direction: GradientDirection = GradientDirection.RIGHT, colors: GradientProvider
    ) {
        val pose = Matrix3x2f(this.pose())
        val scissor = this.scissorStack.peek()
        val pipeline = GUI_GRADIENT(colors, speed, direction)
        val bounds = ScreenRectangle(ScreenPosition(x, y), width, height).transformAxisAligned(pose)
        //? if >= 26.3
        RenderSystem.getCompiledPipelineNullable(pipeline) ?: return // we skip, the compiling shouldn't take long
        this.guiRenderState.addGuiElement(object : GuiElementRenderState {
            override fun buildVertices(vertexConsumer: VertexConsumer) {
                vertexConsumer.addVertexWith2DPose(pose, x.toFloat(), y.toFloat()).setColor(-1)
                vertexConsumer.addVertexWith2DPose(pose, x.toFloat(), y.toFloat() + height).setColor(-1)
                vertexConsumer.addVertexWith2DPose(pose, x.toFloat() + width, y.toFloat() + height).setColor(-1)
                vertexConsumer.addVertexWith2DPose(pose, x.toFloat() + width, y.toFloat()).setColor(-1)
            }

            override fun pipeline(): RenderPipeline = pipeline
            override fun textureSetup(): TextureSetup = TextureSetup.noTexture()
            override fun scissorArea(): ScreenRectangle? = scissor
            override fun bounds(): ScreenRectangle = bounds
        })
    }

    fun guiTexturedGradient(speed: Float = 1f, direction: GradientDirection = GradientDirection.RIGHT, grayscale: Boolean = true, colors: GradientProvider): RenderPipeline = GUI_TEXTURED_GRADIENT(colors, speed, direction, grayscale)

}
