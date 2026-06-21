package me.owdding.lib.rendering.text

import com.mojang.blaze3d.pipeline.RenderPipeline
import me.owdding.lib.helper.TextShaderHolder
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.util.Util

data class TextShaderInfo(val pipeline: RenderPipeline, val texture: Identifier, val displayMode: Font.DisplayMode, val grayScale: Boolean)

val TEXT_RENDER_TYPE_CACHE: (TextShaderInfo) -> RenderType = Util.memoize<TextShaderInfo, RenderType> {
    RenderType.create(
        "meowddinglib/font_shader",
        RenderSetup.builder(it.pipeline)
            //? 26.1
            //.bufferSize(786432)
            .useLightmap()
            .sortOnUpload()
            .withTexture("Sampler0", it.texture)
            .createRenderSetup(),
    )
}::apply

fun createTextRenderType(
    shader: TextShader,
    location: Identifier,
    mode: Font.DisplayMode,
    grayScale: Boolean,
): RenderType {
    return TEXT_RENDER_TYPE_CACHE(TextShaderInfo(shader.getPipeline(mode, grayScale), location, mode, grayScale))
}

fun createTextRenderType(
    pipeline: RenderPipeline,
    location: Identifier,
    mode: Font.DisplayMode,
    grayScale: Boolean,
): RenderType {
    return TEXT_RENDER_TYPE_CACHE(TextShaderInfo(pipeline, location, mode, grayScale))
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
    //? 26.1
    //val pipeline: RenderPipeline


    //? >= 26.2
    val pipeline: (Font.DisplayMode?, Boolean) -> RenderPipeline

    val useWhite: Boolean get() = true
    val hasShadow: Boolean? get() = null

    fun getPipeline(mode: Font.DisplayMode?, grayScale: Boolean): RenderPipeline {
        //? 26.1
        //return pipeline
        //? >= 26.2
        return pipeline(mode, grayScale)
    }

    fun getRenderType(
        location: Identifier,
    ): RenderType = getRenderType(location, Font.DisplayMode.NORMAL, false)

    fun getRenderType(
        location: Identifier,
        mode: Font.DisplayMode,
        grayScale: Boolean,
    ): RenderType {
        return createTextRenderType(this, location, mode, grayScale)
    }
}
