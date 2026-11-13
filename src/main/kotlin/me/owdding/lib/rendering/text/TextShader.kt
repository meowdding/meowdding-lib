package me.owdding.lib.rendering.text

import com.mojang.renderpearl.api.pipeline.RenderPipeline
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.util.Util

//? >= 26.3
import net.minecraft.client.renderer.oit.OitPipelineSet

public data class TextShaderInfo(val pipeline: PipelineResult, val texture: Identifier, val displayMode: Font.DisplayMode, val grayScale: Boolean)

internal val TEXT_RENDER_TYPE_CACHE: (TextShaderInfo) -> RenderType = Util.memoize<TextShaderInfo, RenderType> {
    RenderType.create(
        "meowddinglib/font_shader",
        //~ if >= 26.3 'pipeline' -> 'pipeline.first'
        RenderSetup.builder(it.pipeline.first)
            //? >= 26.3 {
            .apply {
                setOitPipelines(it.pipeline.second ?: return@apply)
            }
            //? }
            .useLightmap()
            .sortOnUpload()
            .withTexture("Sampler0", it.texture)
            .createRenderSetup(),
    )
}::apply

public fun createTextRenderType(
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

public fun Style.textShader(): TextShader? {
    return this.`meowddinglib$getTextShader`()
}

public fun Style.withTextShader(shader: TextShader?): Style {
    return this.`meowddinglib$withTextShader`(shader) ?: this
}

public var MutableComponent.textShader: TextShader?
    get() = this.style.textShader()
    set(value) {
        this.withStyle(style.withTextShader(value))
    }

//? < 26.3
//typealias PipelineResult = RenderPipeline
//? >= 26.3
public typealias PipelineResult = Pair<RenderPipeline, OitPipelineSet?>

public interface TextShader {

    public val id: Identifier

    public val pipeline: (Font.DisplayMode?, Boolean) -> PipelineResult

    public val useWhite: Boolean get() = true
    public val hasShadow: Boolean? get() = null

    //? < 26.3 {
    //public fun getPipeline(mode: Font.DisplayMode?, grayScale: Boolean): RenderPipeline = getPipelines(mode, grayScale)
    //?}
    public fun getPipelines(mode: Font.DisplayMode?, grayScale: Boolean): PipelineResult = pipeline(mode, grayScale)

    public fun getRenderType(
        location: Identifier,
    ): RenderType = getRenderType(location, Font.DisplayMode.NORMAL, false)

    public fun getRenderType(
        location: Identifier,
        mode: Font.DisplayMode,
        grayScale: Boolean,
    ): RenderType {
        return createTextRenderType(this, location, mode, grayScale)
    }
}
