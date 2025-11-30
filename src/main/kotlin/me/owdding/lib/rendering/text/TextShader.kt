@file:Suppress("unused")

package me.owdding.lib.rendering.text

import com.mojang.blaze3d.pipeline.RenderPipeline
import me.owdding.lib.helper.TextShaderHolder
import net.minecraft.Util
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.CompositeState
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.TriState
import java.util.function.BiFunction

val TEXT_RENDER_TYPE_CACHE: BiFunction<TextShader, ResourceLocation, RenderType> =
    Util.memoize<TextShader, ResourceLocation, RenderType> { shader, location ->
        RenderType.create(
            "meowddinglib/font_shader",
            786432,
            false,
            false,
            shader.pipeline,
            CompositeState.builder()
                .setTextureState(TextureStateShard(location, /*? if 1.21.5 >>*/ /*TriState.FALSE,*/ false))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .createCompositeState(false)
        )
    }

fun createTextRenderType(
    shader: TextShader,
    location: ResourceLocation,
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

    val id: ResourceLocation
    val pipeline: RenderPipeline

    val useWhite: Boolean get() = true
    val hasShadow: Boolean? get() = null

    fun getRenderType(location: ResourceLocation): RenderType {
        return createTextRenderType(this, location)
    }
}
