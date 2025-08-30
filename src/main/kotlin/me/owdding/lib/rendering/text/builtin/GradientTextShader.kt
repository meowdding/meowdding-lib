package me.owdding.lib.rendering.text.builtin

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.lib.MeowddingLib
import me.owdding.lib.extensions.withShaderDefine
import me.owdding.lib.rendering.text.TextShader
import me.owdding.lib.utils.MeowddingPipelines
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ARGB
import org.joml.Vector4f

class GradientTextShader(val gradientProvider: GradientProvider) : TextShader {
    constructor(colors: List<Int>) : this({ colors })
    constructor(vararg colors: TextColor) : this(colors.map { it.value })

    override val id: ResourceLocation = ID

    override val pipeline: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET, MeowddingPipelines.GAME_TIME_SNIPPET)
            .withLocation(MeowddingLib.id("gradient_text"))
            .withVertexShader(MeowddingLib.id("text/gradient"))
            .withFragmentShader(MeowddingLib.id("text/gradient"))
            .withSampler("Sampler0")
            .withDepthBias(-1.0f, -10.0f)
            .withShaderDefine(
                "COLORS",
                gradientProvider
                    .getColors()
                    .map { color -> Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color)) }
                    .toTypedArray(),
            )
            .build(),
    )

    override val useWhite: Boolean get() = true
    override val hasShadow: Boolean get() = true

    companion object {
        val ID = MeowddingLib.id("gradient")
        val CODEC: MapCodec<GradientTextShader> = RecordCodecBuilder.mapCodec {
            it.group(
                GradientProvider.CODEC.fieldOf("colors").forGetter { it.gradientProvider },
            ).apply(it, ::GradientTextShader)
        }
    }
}

fun interface GradientProvider {
    fun getColors(): List<Int>

    companion object {
        val CODEC: Codec<GradientProvider> = Codec.INT.listOf().xmap(
            { GradientProvider { it } },
            { it.getColors() },
        )
    }
}
