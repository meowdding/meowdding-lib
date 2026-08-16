package me.owdding.lib.rendering.text.builtin

//? >= 26.2
import com.mojang.blaze3d.pipeline.BindGroupLayout
import com.mojang.blaze3d.pipeline.DepthStencilState
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.CompareOp
//? 26.1
//import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.lib.MeowddingLib
import me.owdding.lib.extensions.withShaderDefine
import me.owdding.lib.generated.EnumCodec
import me.owdding.lib.rendering.text.TextShader
import me.owdding.lib.utils.MeowddingUtil
//? 26.1
//import me.owdding.lib.utils.MeowddingPipelines
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import org.joml.Vector2f
import org.joml.Vector4f


//? >= 26.2 {
private val layout = BindGroupLayout.builder()
    .build()

private val pipelineCreator0: (GradientTextShader, Font.DisplayMode?, Boolean) -> RenderPipeline = MeowddingUtil.memoize { shader, displayMode, grayscale ->
    RenderPipelines.register(
        RenderPipeline.builder(
            *buildList {
                when (displayMode) {
                    Font.DisplayMode.NORMAL, Font.DisplayMode.POLYGON_OFFSET -> add(RenderPipelines.WORLD_TEXT_SNIPPET)
                    Font.DisplayMode.SEE_THROUGH -> add(RenderPipelines.TEXT_SNIPPET)
                    null -> add(RenderPipelines.GUI_TEXT_SNIPPET)
                }
            }.toTypedArray(),
        ).withLocation(MeowddingLib.id("gradient_text/${displayMode?.name?.lowercase()}${if (grayscale) "_grayscale" else ""}"))
            .withVertexShader(MeowddingLib.id("text/gradient"))
            .withFragmentShader(MeowddingLib.id("text/gradient"))
            .withBindGroupLayout(layout)
            .apply {
                if (grayscale) {
                    withShaderDefine("IS_GRAYSCALE")
                }

                when (displayMode) {
                    Font.DisplayMode.SEE_THROUGH -> {
                        withShaderDefine("IS_SEE_THROUGH")
                    }

                    Font.DisplayMode.POLYGON_OFFSET -> {
                        withDepthStencilState(DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true, 1.0F, 10.0F))
                    }

                    null -> {
                        withShaderDefine("IS_GUI")
                    }

                    else -> {}
                }
            }
            .withShaderDefine(
                "COLORS",
                shader.gradientProvider
                    .getColors()
                    .map { color -> Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color)) }
                    .toTypedArray(),
            )
            .withShaderDefine(
                "DIRECTION",
                shader.direction.vec,
            )
            .withShaderDefine(
                "SPEED",
                shader.speed,
            )
            .build(),
    )
}
//? } else {
/*private val pipelineCreator0: (GradientTextShader) -> RenderPipeline = MeowddingUtil.memoize {
    RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET, MeowddingPipelines.GAME_TIME_SNIPPET)
            .withLocation(MeowddingLib.id("gradient_text"))
            .withVertexShader(MeowddingLib.id("text/gradient"))
            .withFragmentShader(MeowddingLib.id("text/gradient"))
            .withSampler("Sampler0")
            .withDepthStencilState(DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false, -1f, -10f))
            .withShaderDefine(
                "COLORS",
                it.gradientProvider
                    .getColors()
                    .map { color -> Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color)) }
                    .toTypedArray(),
            )
            .withShaderDefine(
                "DIRECTION",
                it.direction.vec,
            )
            .withShaderDefine(
                "SPEED",
                it.speed,
            )
            .build(),
    )
}
*///? }

class GradientTextShader(val gradientProvider: GradientProvider, val direction: Direction, val speed: Float) : TextShader {
    constructor(gradientProvider: GradientProvider) : this(gradientProvider, Direction.RIGHT, 1f)
    constructor(colors: List<Int>) : this({ colors })
    constructor(colors: List<Int>, direction: Direction = Direction.RIGHT, speed: Float = 1f) : this({ colors }, direction, speed)
    constructor(vararg colors: TextColor) : this(colors.map { it.value })
    constructor(vararg colors: TextColor, direction: Direction = Direction.RIGHT, speed: Float = 1f) : this(colors.map { it.value }, direction, speed)

    override val id: Identifier = ID

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
    //? 26.1
    //override val pipeline: RenderPipeline = pipelineCreator(this)

    //? >= 26.2
    override val pipeline: (Font.DisplayMode?, Boolean) -> RenderPipeline = { first, second -> pipelineCreator(this, first, second) }

    companion object {
        val ID = MeowddingLib.id("gradient")
        val CODEC: MapCodec<GradientTextShader> = RecordCodecBuilder.mapCodec {
            it.group(
                GradientProvider.CODEC.fieldOf("colors").forGetter { it.gradientProvider },
                EnumCodec.forKCodec(Direction.entries.toTypedArray()).optionalFieldOf("direction", Direction.RIGHT).forGetter { it.direction },
                Codec.FLOAT.optionalFieldOf("speed", 1f).forGetter { it.speed },
            ).apply(it, ::GradientTextShader)
        }

        //? >= 26.2 {
        val pipelineCreator: (GradientTextShader, Font.DisplayMode?, Boolean) -> RenderPipeline = pipelineCreator0
        //? } else
        //private val pipelineCreator: (GradientTextShader) -> RenderPipeline = pipelineCreator0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GradientTextShader) return false

        if (speed != other.speed) return false
        if (gradientProvider.getColors() != other.gradientProvider.getColors()) return false
        if (direction != other.direction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = speed.hashCode()
        result = 31 * result + gradientProvider.getColors().hashCode()
        result = 31 * result + direction.hashCode()
        return result
    }


}

fun interface GradientProvider {
    fun getColors(): List<Int>

    companion object {
        val CODEC: Codec<GradientProvider> = Codec.INT.listOf(1, Int.MAX_VALUE).xmap(
            { GradientProvider { it } },
            { it.getColors() },
        )
    }
}
