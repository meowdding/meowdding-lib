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
import me.owdding.lib.utils.GradientDirection
import me.owdding.lib.utils.GradientProvider as NewGradientProvider
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
                shader.gradient
                    .getColors()
                    .map { color -> Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color)) }
                    .toTypedArray(),
            )
            .withShaderDefine(
                "DIRECTION",
                shader.gradientDirection.vec,
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

public class GradientTextShader(
    internal val gradient: NewGradientProvider,
    internal val gradientDirection: GradientDirection,
    internal val speed: Float
) : TextShader {

    //? if < 26.4 {
    @Deprecated("Use GradientDirection instead", level = DeprecationLevel.HIDDEN)
    public constructor(gradientProvider: GradientProvider, direction: Direction, speed: Float) : this(gradientProvider.asNew(), direction.asNewDirection(), speed)
    @Deprecated("Use GradientDirection instead", level = DeprecationLevel.HIDDEN)
    public constructor(colors: List<Int>, direction: Direction = Direction.RIGHT, speed: Float = 1f) : this({ colors }, direction.asNewDirection(), speed)
    @Deprecated("Use GradientDirection instead", level = DeprecationLevel.HIDDEN)
    public constructor(vararg colors: TextColor, direction: Direction = Direction.RIGHT, speed: Float = 1f) : this(colors.map { it.value }, direction.asNewDirection(), speed)
    @Deprecated("Use GradientDirection instead", level = DeprecationLevel.HIDDEN)
    public constructor(gradientProvider: GradientProvider) : this(gradientProvider.asNew())

    //? }

    public constructor(gradientProvider: NewGradientProvider) : this(gradientProvider, GradientDirection.RIGHT, 1f)
    public constructor(colors: List<Int>) : this({ colors })
    public constructor(colors: List<Int>, direction: GradientDirection = GradientDirection.RIGHT, speed: Float = 1f) : this({ colors }, direction, speed)
    public constructor(vararg colors: TextColor) : this(colors.map { it.value })
    public constructor(vararg colors: TextColor, direction: GradientDirection = GradientDirection.RIGHT, speed: Float = 1f) : this(colors.map { it.value }, direction, speed)

    override val id: Identifier = ID

    override val useWhite: Boolean get() = true
    override val hasShadow: Boolean get() = true

    // moved to src/main/kotlin/me/owdding/lib/utils/GradientUtils.kt
    //? < 26.4 {
    public val gradientProvider: GradientProvider = { gradient.getColors() }
    public val direction: Direction = gradientDirection.toOld() ?: Direction.RIGHT

    @Deprecated("Use GradientDirection instead")
    public enum class Direction(public val vec: Vector2f, public val direction: GradientDirection) {
        LEFT(GradientDirection.LEFT),
        RIGHT(GradientDirection.RIGHT),
        UP(GradientDirection.UP),
        DOWN(GradientDirection.DOWN),
        DOWN_LEFT(GradientDirection.DOWN_LEFT),
        DOWN_RIGHT(GradientDirection.DOWN_RIGHT),
        UP_LEFT(GradientDirection.UP_LEFT),
        UP_RIGHT(GradientDirection.UP_RIGHT),
        ;


        constructor(direction: GradientDirection) : this(direction.vec, direction)

        public fun asNewDirection(): GradientDirection = direction
    }
    //? }

    //? 26.1
    //override val pipeline: RenderPipeline = pipelineCreator0(this)

    override val pipeline: (Font.DisplayMode?, Boolean) -> PipelineResult = { first, second -> pipelineCreator0(this, first, second) }

    public companion object {
        internal val ID = MeowddingLib.id("gradient")
        public val CODEC: MapCodec<GradientTextShader> = RecordCodecBuilder.mapCodec {
            it.group(
                NewGradientProvider.CODEC.fieldOf("colors").forGetter { it.gradient },
                EnumCodec.forKCodec(GradientDirection.entries.toTypedArray()).optionalFieldOf("direction", GradientDirection.RIGHT).forGetter { it.gradientDirection },
                Codec.FLOAT.optionalFieldOf("speed", 1f).forGetter { it.speed },
            ).apply(it, ::GradientTextShader)
        }

        public val pipelineCreator: (GradientTextShader, Font.DisplayMode?, Boolean) -> PipelineResult = pipelineCreator0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GradientTextShader) return false

        if (speed != other.speed) return false
        if (gradient.getColors() != other.gradient.getColors()) return false
        if (gradientDirection != other.gradientDirection) return false

        return true
    }

    override fun hashCode(): Int {
        var result = speed.hashCode()
        result = 31 * result + gradient.getColors().hashCode()
        result = 31 * result + gradientDirection.hashCode()
        return result
    }

}

//? if < 26.4 {
@Deprecated("Use the other gradient provider", replaceWith = ReplaceWith("me.owdding.lib.utils.GradientProvider"))
public fun interface GradientProvider {
    public companion object {
        public val CODEC: Codec<GradientProvider> = Codec.INT.listOf(1, Int.MAX_VALUE).xmap(
            { GradientProvider { it } },
            { it.getColors() },
        )
    }

    public fun getColors(): List<Int>
    public fun asNew(): NewGradientProvider = NewGradientProvider { getColors() }
}
//? }
