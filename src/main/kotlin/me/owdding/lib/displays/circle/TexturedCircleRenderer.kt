package me.owdding.lib.displays.circle

//? >= 26.2
import com.mojang.blaze3d.PrimitiveTopology
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import earth.terrarium.olympus.client.pipelines.renderer.PipelineRenderer
import earth.terrarium.olympus.client.utils.TextureUtils
import me.owdding.lib.rendering.MeowddingPipState
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.data.AtlasIds
import net.minecraft.resources.Identifier
import org.joml.Matrix3x2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.util.function.Supplier

//? 26.1 {
//import net.minecraft.client.renderer.MultiBufferSource
//import java.util.function.Function
//import com.mojang.blaze3d.vertex.Tesselator
//import com.mojang.blaze3d.vertex.VertexFormat
//? }

//~ if >= 26.2 '(buffer: MultiBufferSource.BufferSource) : ' -> '() : ', '(buffer)' -> '()'
class TexturedCircleRenderer() : PictureInPictureRenderer<TexturedCircleState>() {

    override fun getRenderStateClass(): Class<TexturedCircleState> = TexturedCircleState::class.java

    override fun renderToTexture(state: TexturedCircleState, poseStack: PoseStack/*? >= 26.2 >> ')'*/, submitNodeCollector: SubmitNodeCollector) {
        val bounds = state.bounds

        val scale = McClient.window.guiScale.toFloat()
        val scaledWidth = bounds.width * scale
        val scaledHeight = bounds.height * scale

        //? >= 26.2 {
        ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_TEX_COLOR.vertexSize * 4).use {
            val bufferBuilder = BufferBuilder(it, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
            //? } else
            //val bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)

            bufferBuilder.addVertex(0f, 0f, 0f).setUv(-1f, -1f).setColor(-1)
            bufferBuilder.addVertex(0f, scaledHeight, 0f).setUv(-1f, 1f).setColor(-1)
            bufferBuilder.addVertex(scaledWidth, scaledHeight, 0f).setUv(1f, 1f).setColor(-1)
            bufferBuilder.addVertex(scaledWidth, 0f, 0f).setUv(1f, -1f).setColor(-1)

            val sprite = McClient.self.atlasManager.getAtlasOrThrow(AtlasIds.GUI).getSprite(state.texture)

            val texture = TextureUtils.single(sprite.atlasLocation())

            PipelineRenderer.builder(TexturedCirclePipeline.PIPELINE, bufferBuilder.buildOrThrow())
                .textures(texture)
                .uniform(TexturedCirclePipeline.UNIFORM_STORAGE, TexturedCircleUniform(sprite.u0, sprite.u1, sprite.v0, sprite.v1))
                .color(-1)
                .draw()

            //? >= 26.2
        }
    }

    override fun getTextureLabel(): String = "meowdding_lib_textured_circle"

}


data class TexturedCircleState(
    override val bounds: ScreenRectangle,
    override val scissorArea: ScreenRectangle?,
    override val pose: Matrix3x2f,
    val texture: Identifier,
) : MeowddingPipState<TexturedCircleState>() {
    //? if > 26.1 {
    override fun getFactory(): Supplier<PictureInPictureRenderer<TexturedCircleState>> = Supplier { TexturedCircleRenderer() }
    //? } else
    //override fun getFactory(): Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<TexturedCircleState>> = Function { buffer -> TexturedCircleRenderer(buffer) }

    override val shrinkToScissor: Boolean
        get() = false

    override val x0: Int = bounds.left()
    override val x1: Int = bounds.right()
    override val y0: Int = bounds.top()
    override val y1: Int = bounds.bottom()
}
