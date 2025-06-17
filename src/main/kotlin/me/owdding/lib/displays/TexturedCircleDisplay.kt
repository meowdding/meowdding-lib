package me.owdding.lib.displays

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.shaders.UniformType
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import earth.terrarium.olympus.client.pipelines.PipelineRenderer
import me.owdding.lib.MeowddingLib.id
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.helpers.McClient

class TexturedCircleDisplay(@JvmField val width: Int, @JvmField val height: Int, private val texture: ResourceLocation) : Display {
    override fun getHeight(): Int = height

    override fun getWidth(): Int = width

    override fun render(graphics: GuiGraphics) {
        val matrix = graphics.pose().last().pose()
        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)

        buffer.addVertex(matrix, 0f, 0f, 0f).setUv(-1f, -1f)
        buffer.addVertex(matrix, 0f, height.toFloat(), 0f).setUv(-1f, 1f)
        buffer.addVertex(matrix, width.toFloat(), height.toFloat(), 0f).setUv(1f, 1f)
        buffer.addVertex(matrix, width.toFloat(), 0f, 0f).setUv(1f, -1f)

        val sprite = McClient.self.guiSprites.getSprite(texture)

        val abstractTexture: AbstractTexture = McClient.self.textureManager.getTexture(
            sprite.atlasLocation()
        )

        RenderSystem.setShaderTexture(0, abstractTexture.texture)

        PipelineRenderer.draw(renderPipeline, buffer.buildOrThrow()) {
            it.setUniform("uvs", sprite.u0, sprite.u1, sprite.v0, sprite.v1)
        }
    }

    private val renderPipeline: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(id("pipeline/circle_tex.fsh"))
            .withFragmentShader(id("circle_tex"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withUniform("uvs", UniformType.VEC4)
            .withDepthWrite(false)
            .build(),
    )
}
