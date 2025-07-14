package me.owdding.lib.displays.circle

import com.google.common.base.Supplier
import com.google.common.base.Suppliers
import com.mojang.blaze3d.buffers.Std140Builder
import com.mojang.blaze3d.buffers.Std140SizeCalculator
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.shaders.UniformType
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import earth.terrarium.olympus.client.pipelines.uniforms.RenderPipelineUniforms
import me.owdding.lib.MeowddingLib
import net.minecraft.client.renderer.DynamicUniformStorage
import net.minecraft.client.renderer.RenderPipelines
import org.joml.Vector4f
import java.nio.ByteBuffer

data class TexturedCircleUniform(val uvs: Vector4f) : RenderPipelineUniforms {

    constructor(u0: Float, v0: Float, u1: Float, v1: Float) : this(Vector4f(u0, v0, u1, v1))

    override fun name(): String = TexturedCirclePipeline.UNIFORM_NAME
    override fun write(byteBuffer: ByteBuffer) {
        Std140Builder.intoBuffer(byteBuffer).putVec4(uvs).get()
    }
}

object TexturedCirclePipeline {

    const val UNIFORM_NAME = "TexturedCircleUniform"
    val UNIFORM_STORAGE: Supplier<DynamicUniformStorage<TexturedCircleUniform>> = Suppliers.memoize {
        DynamicUniformStorage<TexturedCircleUniform>(
            "Textured Circle UBO",
            Std140SizeCalculator().putVec4().get(),
            2
        )
    }

    val PIPELINE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(MeowddingLib.id("pipeline/circle_tex.fsh"))
            .withFragmentShader(MeowddingLib.id("circle_tex"))
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withUniform(UNIFORM_NAME, UniformType.UNIFORM_BUFFER)
            .withDepthWrite(false)
            .build(),
    )
}
