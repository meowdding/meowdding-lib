package me.owdding.lib.rendering.text

import com.mojang.blaze3d.pipeline.RenderPipeline
import me.owdding.lib.helper.TextShaderHolder
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.util.Util
import java.util.function.BiFunction

val TEXT_RENDER_TYPE_CACHE: BiFunction<RenderPipeline, Identifier, RenderType> = Util.memoize { pipeline, location ->
    RenderType.create(
        "meowddinglib/font_shader",
        RenderSetup.builder(pipeline)
            .bufferSize(786432)
            .useLightmap()
            .withTexture("Sampler0", location)
            .createRenderSetup(),
    )
}

fun createTextRenderType(
    shader: TextShader,
    location: Identifier,
): RenderType {
    return TEXT_RENDER_TYPE_CACHE.apply(shader.pipeline, location)
}

fun createTextRenderType(
    pipeline: RenderPipeline,
    location: Identifier,
): RenderType {
    return TEXT_RENDER_TYPE_CACHE.apply(pipeline, location)
}

fun Style.textShader(): TextShader? {
    return (this as? TextShaderHolder)?.`meowddinglib$getTextShader`()
}

fun Style.withTextShader(shader: TextShader?): Style {
    return (this as? TextShaderHolder)?.`meowddinglib$withTextShader`(shader) ?: this
}

var MutableComponent.textShader: TextShader?
    get() = this.style.textShader()
    set(value) {
        this.withStyle(style.withTextShader(value))
    }

interface TextShader {

    val id: Identifier
    val pipeline: RenderPipeline

    val useWhite: Boolean get() = true
    val hasShadow: Boolean? get() = null

    fun getRenderType(location: Identifier): RenderType {
        return createTextRenderType(this, location)
    }
}
