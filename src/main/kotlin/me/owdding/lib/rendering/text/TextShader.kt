package me.owdding.lib.rendering.text

import com.mojang.renderpearl.api.pipeline.RenderPipeline
import me.owdding.lib.helper.TextShaderHolder
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.oit.OitPipelineSet
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.util.Util

data class TextShaderInfo(val pipeline: PipelineResult, val texture: Identifier, val displayMode: Font.DisplayMode, val grayScale: Boolean)

val TEXT_RENDER_TYPE_CACHE: (TextShaderInfo) -> RenderType = Util.memoize<TextShaderInfo, RenderType> {
    RenderType.create(
        "meowddinglib/font_shader",
        //~ if >= 26.3 'pipeline' -> 'pipeline.first'
        RenderSetup.builder(it.pipeline.first)
            //? >= 26.3
            .apply {
                setOitPipelines(it.pipeline.second ?: return@apply)
            }
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
    return TEXT_RENDER_TYPE_CACHE(TextShaderInfo(shader.getPipelines(mode, grayScale), location, mode, grayScale))
}

//? < 26.3 {
/*fun createTextRenderType(
    pipeline: RenderPipeline,
    location: Identifier,
    mode: Font.DisplayMode,
    grayScale: Boolean,
): RenderType {
    return TEXT_RENDER_TYPE_CACHE(TextShaderInfo(pipeline, location, mode, grayScale))
}
*///? }

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

//? < 26.3
//typealias PipelineResult = RenderPipeline
//? >= 26.3
typealias PipelineResult = Pair<RenderPipeline, OitPipelineSet?>


interface TextShader {

    val id: Identifier
    //? 26.1
    //val pipeline: RenderPipeline


    //? >= 26.2
    val pipeline: (Font.DisplayMode?, Boolean) -> PipelineResult

    val useWhite: Boolean get() = true
    val hasShadow: Boolean? get() = null

    //? < 26.3 {
    //fun getPipeline(mode: Font.DisplayMode?, grayScale: Boolean): RenderPipeline = getPipelines(mode, grayScale)
    //?}
    fun getPipelines(mode: Font.DisplayMode?, grayScale: Boolean): PipelineResult {
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
