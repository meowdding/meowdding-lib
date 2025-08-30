package me.owdding.lib.events

import com.mojang.serialization.MapCodec
import me.owdding.lib.rendering.text.TextShader
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class RegisterTextShaderEvent(private val registry: MutableMap<ResourceLocation, MapCodec<out TextShader>>) : SkyBlockEvent() {
    fun register(id: ResourceLocation, codec: MapCodec<out TextShader>) {
        val previous = registry.put(id, codec)
        if (previous != null) {
            throw UnsupportedOperationException("Can't register text shader with id $id as it's already present!")
        }
    }
}
