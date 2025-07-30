package me.owdding.lib.rendering.text.builtin

import com.mojang.blaze3d.pipeline.RenderPipeline
import me.owdding.lib.MeowddingLib
import me.owdding.lib.extensions.withShaderDefine
import me.owdding.lib.rendering.text.TextShader
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.TextColor
import net.minecraft.util.ARGB
import org.joml.Vector4f

class GradientTextShader(gradientProvider: GradientProvider) : TextShader {
    constructor(colors: List<Int>) : this({ colors })
    constructor(vararg colors: TextColor) : this(colors.map { it.value })

    override val pipeline: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET)
            .withLocation(MeowddingLib.id("gradient_text"))
            .withVertexShader(MeowddingLib.id("text/gradient"))
            .withFragmentShader(MeowddingLib.id("text/gradient"))
            .withSampler("Sampler0")
            .withDepthBias(-1.0f, -10.0f)
            .withShaderDefine("COLORS", gradientProvider
                .getColors()
                .map { color -> Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color)) }
                .toTypedArray()
            )
            .build(),
    )

    override val useWhite: Boolean get() = true
    override val hasShadow: Boolean? get() = true

}

fun interface GradientProvider {
    fun getColors(): List<Int>
}
