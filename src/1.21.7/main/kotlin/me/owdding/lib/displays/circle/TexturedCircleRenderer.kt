package me.owdding.lib.displays.circle

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState
import earth.terrarium.olympus.client.pipelines.renderer.PipelineRenderer
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix3x2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.util.function.Function

class TexturedCircleRenderer(buffer: MultiBufferSource.BufferSource) : PictureInPictureRenderer<TexturedCircleRenderer.State>(buffer) {

    override fun getRenderStateClass(): Class<State> = State::class.java

    override fun renderToTexture(state: State, stack: PoseStack) {
        val bounds = state.bounds() ?: return

        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)

        buffer.addVertex(0f, 0f, 0f).setUv(-1f, -1f)
        buffer.addVertex(0f, bounds.height.toFloat(), 0f).setUv(-1f, 1f)
        buffer.addVertex(bounds.width.toFloat(), bounds.height.toFloat(), 0f).setUv(1f, 1f)
        buffer.addVertex(bounds.width.toFloat(), 0f, 0f).setUv(1f, -1f)

        val sprite = McClient.self.guiSprites.getSprite(state.texture)
        RenderSystem.setShaderTexture(0, McClient.self.textureManager.getTexture(sprite.atlasLocation()).textureView)

        PipelineRenderer.builder(TexturedCirclePipeline.PIPELINE, buffer.buildOrThrow())
            .uniform(TexturedCirclePipeline.UNIFORM_STORAGE, TexturedCircleUniform(sprite.u0, sprite.v0, sprite.u1, sprite.v1))
            .draw()
    }

    override fun getTextureLabel(): String = "meowdding_lib_textured_circle"

    data class State(
        val x0: Int, val y0: Int, val x1: Int, val y1: Int,
        val texture: ResourceLocation,
        val pose: Matrix3x2f, val scissor: ScreenRectangle?, val bounds: ScreenRectangle?
    ) : OlympusPictureInPictureRenderState<State> {

        override fun getFactory(): Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<State>> =
            Function { TexturedCircleRenderer(it) }

        override fun x0(): Int = x0
        override fun x1(): Int = x1
        override fun y0(): Int = y0
        override fun y1(): Int = y1
        override fun scale(): Float = 1f

        override fun scissorArea(): ScreenRectangle? = scissor
        override fun bounds(): ScreenRectangle? = bounds

    }
}
