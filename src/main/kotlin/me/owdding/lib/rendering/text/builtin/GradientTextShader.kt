package me.owdding.lib.rendering.text.builtin

import com.mojang.renderpearl.api.pipeline.BlendFunction
import com.mojang.renderpearl.api.pipeline.ColorTargetState
import com.mojang.renderpearl.api.pipeline.CompareOp
import com.mojang.renderpearl.api.pipeline.DepthStencilState
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.lib.MeowddingLib
import me.owdding.lib.extensions.withShaderDefine
import me.owdding.lib.generated.EnumCodec
import me.owdding.lib.rendering.text.PipelineResult
import me.owdding.lib.rendering.text.TextShader
import me.owdding.lib.utils.MemoizeUtil
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import org.joml.Vector2f
import org.joml.Vector4f
import com.mojang.renderpearl.api.pipeline.BindGroupLayout

import me.owdding.lib.rendering.text.builtin.GradientTextShader.Direction as GradientDirection

//? if >= 26.4
//import me.owdding.lib.utils.GradientProvider
//? >= 26.3
import net.minecraft.client.renderer.oit.OitPipelineSet

//? >= 26.3 {
private val pipelineCreator0: (GradientTextShader, Font.DisplayMode?, Boolean) -> Pair<RenderPipeline, OitPipelineSet?> =
    MemoizeUtil.memoize { shader, displayMode, grayscale ->
        val location = MeowddingLib.id("gradient_text/${displayMode?.name?.lowercase()}${if (grayscale) "_grayscale" else ""}")
        val builder = RenderPipeline.builder(
            *buildList {
                when (displayMode) {
                    Font.DisplayMode.NORMAL, Font.DisplayMode.POLYGON_OFFSET -> add(RenderPipelines.WORLD_TEXT_SNIPPET)
                    Font.DisplayMode.SEE_THROUGH -> add(RenderPipelines.TEXT_SNIPPET)
                    null -> add(RenderPipelines.GUI_TEXT_SNIPPET)
                }
            }.toTypedArray(),
        ).withLocation(location)
            .withVertexShader(MeowddingLib.id("text/gradient"))
            .withFragmentShader(MeowddingLib.id("text/gradient"))
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

        if (displayMode == null) {
            return@memoize RenderPipelines.register(builder.withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT)).build()) to null
        }

        val snippet = builder.buildSnippet()

        RenderPipelines.register(
            RenderPipeline.builder(snippet).withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT)).withLocation(location).build(),
        ) to RenderPipelines.register(OitPipelineSet.builder("meowdding/gradient_text_shader", builder).build())
    }
//? } = 26.2 {
/*private val layout = BindGroupLayout.builder()
    .build()

private val pipelineCreator0: (GradientTextShader, Font.DisplayMode?, Boolean) -> RenderPipeline = MemoizeUtil.memoize { shader, displayMode, grayscale ->
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
*///? }

class GradientTextShader(val gradientProvider: GradientProvider, val direction: GradientDirection, val speed: Float) : TextShader {
    constructor(gradientProvider: GradientProvider) : this(gradientProvider, Direction.RIGHT, 1f)
    constructor(colors: List<Int>) : this({ colors })
    constructor(colors: List<Int>, direction: GradientDirection = GradientDirection.RIGHT, speed: Float = 1f) : this({ colors }, direction, speed)
    constructor(vararg colors: TextColor) : this(colors.map { it.value })
    constructor(vararg colors: TextColor, direction: GradientDirection = GradientDirection.RIGHT, speed: Float = 1f) : this(colors.map { it.value }, direction, speed)

    override val id: Identifier = ID

    override val useWhite: Boolean get() = true
    override val hasShadow: Boolean get() = true

    // moved to src/main/kotlin/me/owdding/lib/utils/GradientUtils.kt
    //? < 26.4 {
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
    //? }

    //? 26.1
    //override val pipeline: RenderPipeline = pipelineCreator0(this)

    override val pipeline: (Font.DisplayMode?, Boolean) -> PipelineResult = { first, second -> pipelineCreator0(this, first, second) }

    companion object {
        val ID = MeowddingLib.id("gradient")
        val CODEC: MapCodec<GradientTextShader> = RecordCodecBuilder.mapCodec {
            it.group(
                GradientProvider.CODEC.fieldOf("colors").forGetter { it.gradientProvider },
                EnumCodec.forKCodec(GradientDirection.entries.toTypedArray()).optionalFieldOf("direction", GradientDirection.RIGHT).forGetter { it.direction },
                Codec.FLOAT.optionalFieldOf("speed", 1f).forGetter { it.speed },
            ).apply(it, ::GradientTextShader)
        }

        val pipelineCreator: (GradientTextShader, Font.DisplayMode?, Boolean) -> PipelineResult = pipelineCreator0
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

