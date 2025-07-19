package me.owdding.lib.displays.circle

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import earth.terrarium.olympus.client.pipelines.renderer.PipelineRenderer
import me.owdding.lib.render.MeowddingPipState
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix3x2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.util.function.Function

class TexturedCircleRenderer(buffer: MultiBufferSource.BufferSource) : PictureInPictureRenderer<TexturedCircleState>(buffer) {

    override fun getRenderStateClass(): Class<TexturedCircleState> = TexturedCircleState::class.java

    override fun renderToTexture(state: TexturedCircleState, stack: PoseStack) {
        val bounds = state.bounds
        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)

        val scale = McClient.window.guiScale.toFloat()
        val scaledWidth = bounds.width * scale
        val scaledHeight = bounds.height * scale


        buffer.addVertex(0f, 0f, 0f).setUv(-1f, -1f).setColor(-1)
        buffer.addVertex(0f, scaledHeight, 0f).setUv(-1f, 1f).setColor(-1)
        buffer.addVertex(scaledWidth, scaledHeight, 0f).setUv(1f, 1f).setColor(-1)
        buffer.addVertex(scaledWidth, 0f, 0f).setUv(1f, -1f).setColor(-1)

        val sprite = McClient.self.guiSprites.getSprite(state.texture)

        val abstractTexture: AbstractTexture = McClient.self.textureManager.getTexture(sprite.atlasLocation())

        RenderSystem.setShaderTexture(0, abstractTexture.textureView)

        PipelineRenderer.builder(TexturedCirclePipeline.PIPELINE, buffer.buildOrThrow())
            .uniform(TexturedCirclePipeline.UNIFORM_STORAGE, TexturedCircleUniform(sprite.u0, sprite.u1, sprite.v0, sprite.v1))
            .color(-1)
            .draw()
    }

    override fun getTextureLabel(): String = "meowdding_lib_textured_circle"

}


data class TexturedCircleState(
    override val bounds: ScreenRectangle,
    override val scissorArea: ScreenRectangle?,
    override val pose: Matrix3x2f,
    val texture: ResourceLocation,
) : MeowddingPipState<TexturedCircleState>() {
    override fun getFactory(): Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<TexturedCircleState>> =
        Function { buffer -> TexturedCircleRenderer(buffer) }

    override val shrinkToScissor: Boolean
        get() = false

    override val x0: Int = bounds.left()
    override val x1: Int = bounds.right()
    override val y0: Int = bounds.top()
    override val y1: Int = bounds.bottom()
}
