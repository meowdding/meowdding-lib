package me.owdding.lib.rendering

import com.mojang.renderpearl.api.pipeline.RenderPipeline
import me.owdding.lib.MeowddingLib.id
import me.owdding.lib.extensions.withShaderDefine
import me.owdding.lib.utils.GradientDirection
import me.owdding.lib.utils.GradientProvider
import me.owdding.lib.utils.MemoizeUtil
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.util.ARGB
import org.joml.Vector4f

public object GuiRenderTypes {

    private val GUI_TEXTURED_GRADIENT = MemoizeUtil.memoize { colors: GradientProvider, speed: Float, direction: GradientDirection, grayscale: Boolean ->
        RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
                .withLocation(id("gui_textured_gradient"))
                .withFragmentShader(id("gui/gradient"))
                .withShaderDefine(
                    "COLORS",
                    colors
                        .getColors()
                        .map { color -> Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color)) }
                        .toTypedArray(),
                )
                .withShaderDefine("DIRECTION", direction.vec)
                .withShaderDefine("SPEED", speed)
                .apply {
                    if (grayscale) withShaderDefine("GRAYSCALE")
                }
                .build(),
        )
    }

    public fun guiTexturedGradient(
        speed: Float = 1f,
        direction: GradientDirection = GradientDirection.LEFT,
        grayscale: Boolean = true,
        colors: GradientProvider,
    ): RenderPipeline = GUI_TEXTURED_GRADIENT(colors, speed, direction, grayscale)
}
