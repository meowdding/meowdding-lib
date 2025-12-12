package me.owdding.lib.rendering.text.builtin

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.lib.MeowddingLib
import me.owdding.lib.extensions.withShaderDefine
import me.owdding.lib.generated.EnumCodec
import me.owdding.lib.rendering.text.TextShader
import me.owdding.lib.utils.MeowddingPipelines
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import org.joml.Vector2f
import org.joml.Vector4f

class GradientTextShader(val gradientProvider: GradientProvider, val direction: Direction, val speed: Float) : TextShader {
    constructor(gradientProvider: GradientProvider) : this(gradientProvider, Direction.RIGHT, 1f)
    constructor(colors: List<Int>) : this({ colors })
    constructor(colors: List<Int>, direction: Direction = Direction.RIGHT, speed: Float = 1f) : this({ colors }, direction, speed)
    constructor(vararg colors: TextColor) : this(colors.map { it.value })
    constructor(vararg colors: TextColor, direction: Direction = Direction.RIGHT, speed: Float = 1f) : this(colors.map { it.value }, direction, speed)

    override val id: Identifier = ID

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
            .withShaderDefine(
                "DIRECTION",
                direction.vec,
            )
            .withShaderDefine(
                "SPEED",
                speed,
            )
            .build(),
    )

    override val useWhite: Boolean get() = true
    override val hasShadow: Boolean get() = true

    enum class Direction(val vec: Vector2f) {
        LEFT(Vector2f(1f, 0f)),
        RIGHT(Vector2f(-1f, 0f)),
        UP(Vector2f(0f, -1f)),
        DOWN(Vector2f(0f, 1f)),
        DOWN_LEFT(Vector2f(1f, 1f)),
        DOWN_RIGHT(Vector2f(-1f, 1f)),
        UP_LEFT(Vector2f(1f, -1f)),
        UP_RIGHT(Vector2f(-1f, -1f)),
        ;
    }

    companion object {
        val ID = MeowddingLib.id("gradient")
        val CODEC: MapCodec<GradientTextShader> = RecordCodecBuilder.mapCodec {
            it.group(
                GradientProvider.CODEC.fieldOf("colors").forGetter { it.gradientProvider },
                EnumCodec.forKCodec(Direction.entries.toTypedArray()).optionalFieldOf("direction", Direction.RIGHT).forGetter { it.direction },
                Codec.FLOAT.optionalFieldOf("speed", 1f).forGetter { it.speed },
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
