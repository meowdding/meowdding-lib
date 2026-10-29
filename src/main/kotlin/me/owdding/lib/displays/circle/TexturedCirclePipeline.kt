package me.owdding.lib.displays.circle

import com.mojang.blaze3d.buffers.Std140Builder
import com.mojang.blaze3d.buffers.Std140SizeCalculator
import com.mojang.renderpearl.api.pipeline.BindGroupLayout
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import com.mojang.renderpearl.api.pipeline.UniformType
import earth.terrarium.olympus.client.pipelines.uniforms.RenderPipelineUniforms
import earth.terrarium.olympus.client.pipelines.uniforms.RenderPipelineUniformsStorage
import me.owdding.lib.MeowddingLib.id
import me.owdding.lib.displays.circle.TexturedCirclePipeline.UNIFORM_NAME
import net.minecraft.client.renderer.DynamicGpuDataStorage
import net.minecraft.client.renderer.RenderPipelines
import org.joml.Vector4f
import java.nio.ByteBuffer
import java.util.function.Supplier

data class TexturedCircleUniform(val uvs: Vector4f) : RenderPipelineUniforms {

    constructor(u0: Float, v0: Float, u1: Float, v1: Float) : this(Vector4f(u0, v0, u1, v1))

    override fun name(): String = UNIFORM_NAME
    override fun write(byteBuffer: ByteBuffer) {
        Std140Builder.intoBuffer(byteBuffer).putVec4(uvs).get()
    }
}

object TexturedCirclePipeline {

    const val UNIFORM_NAME = "MLibTexturedCircleUniform"
    val UNIFORM_STORAGE: Supplier<DynamicGpuDataStorage<TexturedCircleUniform>> =
        RenderPipelineUniformsStorage.register("Meowdding Textured Circle UBO", 2, Std140SizeCalculator().putVec4())

    val PIPELINE: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(id("circle_tex"))
            .withFragmentShader(id("core/circle_tex"))
            .withCull(false)
            .withBindGroupLayout(BindGroupLayout.builder().withUniform(UNIFORM_NAME, UniformType.UNIFORM_BUFFER).build())
            .build(),
    )
}
