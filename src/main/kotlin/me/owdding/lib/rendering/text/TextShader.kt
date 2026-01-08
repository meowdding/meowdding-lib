package me.owdding.lib.rendering.text

import com.mojang.blaze3d.pipeline.RenderPipeline
import me.owdding.lib.helper.TextShaderHolder
//? > 1.21.10
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.util.Util
import java.util.function.BiFunction

//? < 1.21.11 {
/*import net.minecraft.client.renderer.rendertype.RenderType.CompositeState
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard
import net.minecraft.util.TriState
*///?}

val TEXT_RENDER_TYPE_CACHE: BiFunction<TextShader, Identifier, RenderType> =
    Util.memoize<TextShader, Identifier, RenderType> { shader, location ->
        //? if 1.21.5 {
        /*val shard = TextureStateShard(location, TriState.FALSE, false)
        *///?} else if < 1.21.11 {
        /*val shard = TextureStateShard(location, false)
        *///? }

        //? if > 1.21.10 {
        RenderType.create(
            "meowddinglib/fon_shader",
            RenderSetup.builder(shader.pipeline)
                .bufferSize(786432)
                .useLightmap()
                .withTexture("Sampler0", location)
                .createRenderSetup(),
        )
        //?} else {
        /*RenderType.create(
            "meowddinglib/font_shader",
            786432,
            false,
            false,
            shader.pipeline,
            CompositeState.builder()
                .setTextureState(shard)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .createCompositeState(false),
        )
        *///?}
    }

fun createTextRenderType(
    shader: TextShader,
    location: Identifier,
): RenderType {
    return TEXT_RENDER_TYPE_CACHE.apply(shader, location)
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
