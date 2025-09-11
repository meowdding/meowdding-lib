package me.owdding.lib.rendering.text

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.ktmodules.Module
import me.owdding.lib.events.RegisterTextShaderEvent
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription

@Module
object TextShaders {

    var activeShader: TextShader? = null
        @JvmStatic get
        @JvmStatic @ApiStatus.Internal set

    fun GuiGraphics.withTextShader(shader: TextShader?, action: () -> Unit) = pushPop(shader, action)

    fun pushPop(shader: TextShader?, action: () -> Unit) {
        activeShader = shader
        action()
        activeShader = null
    }

    private val codecRegistry by lazy {
        val registry = mutableMapOf<ResourceLocation, MapCodec<out TextShader>>()
        RegisterTextShaderEvent(registry).post(SkyBlockAPI.eventBus)
        registry
    }

    val CODEC: Codec<TextShader> = ResourceLocation.CODEC.dispatch({ it.id }, { codecRegistry[it]!! })

    @Subscription
    fun register(event: RegisterTextShaderEvent) {
        event.register(GradientTextShader.ID, GradientTextShader.CODEC)
    }
}
