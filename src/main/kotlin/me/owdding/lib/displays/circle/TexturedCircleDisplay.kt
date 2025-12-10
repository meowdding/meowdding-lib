package me.owdding.lib.displays.circle


import net.minecraft.resources.Identifier
import me.owdding.lib.displays.Display
import net.minecraft.client.gui.GuiGraphics

//? if > 1.21.5 {
import net.minecraft.client.gui.navigation.ScreenRectangle
//?} else {
/*import earth.terrarium.olympus.client.pipelines.PipelineRenderer
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.shaders.UniformType
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import me.owdding.lib.MeowddingLib
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.texture.AbstractTexture
import tech.thatgravyboat.skyblockapi.helpers.McClient
 *///?}

class TexturedCircleDisplay(@JvmField val width: Int, @JvmField val height: Int, private val texture: Identifier) : Display {
    override fun getHeight(): Int = height
    override fun getWidth(): Int = width

    override fun render(graphics: GuiGraphics) {
        //? if > 1.21.5 {
        val bounds = ScreenRectangle(0, 0, width, height).transformMaxBounds(graphics.pose())

        graphics.guiRenderState.submitPicturesInPictureState(
            TexturedCircleState(
                bounds,
                graphics.scissorStack.peek(),
                graphics.pose(),
                texture,
            ),
        )
        //?} else {
        /*val matrix = graphics.pose().last().pose()
        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)

        buffer.addVertex(matrix, 0f, 0f, 0f).setUv(-1f, -1f)
        buffer.addVertex(matrix, 0f, height.toFloat(), 0f).setUv(-1f, 1f)
        buffer.addVertex(matrix, width.toFloat(), height.toFloat(), 0f).setUv(1f, 1f)
        buffer.addVertex(matrix, width.toFloat(), 0f, 0f).setUv(1f, -1f)

        val sprite = McClient.self.guiSprites.getSprite(texture)

        val abstractTexture: AbstractTexture = McClient.self.textureManager.getTexture(
            sprite.atlasLocation(),
        )

        RenderSystem.setShaderTexture(0, abstractTexture.texture)

        PipelineRenderer.draw(renderPipeline, buffer.buildOrThrow()) {
            it.setUniform("uvs", sprite.u0, sprite.u1, sprite.v0, sprite.v1)
        }
        *///?}
    }

    //? if <= 1.21.5 {
    /*private val renderPipeline: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(MeowddingLib.id("circle"))
            .withFragmentShader(MeowddingLib.id("core/circle_tex"))
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withUniform("uvs", UniformType.VEC4)
            .withDepthWrite(false)
            .build(),
    )
    *///?}

}

internal fun roundedTextureDisplay(width: Int, height: Int, texture: Identifier): Display {
    return TexturedCircleDisplay(width, height, texture)

}
