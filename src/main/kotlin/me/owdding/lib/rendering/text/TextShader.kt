@file:Suppress("unused")

package me.owdding.lib.rendering.text

import com.mojang.blaze3d.pipeline.RenderPipeline
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.msrandom.stub.Stub

@Stub
expect fun createTextRenderType(
    shader: TextShader,
    location: ResourceLocation,
): RenderType

@Stub
expect fun Style.textShader(): TextShader?

@Stub
expect fun Style.withTextShader(shader: TextShader?): Style

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
