package me.owdding.lib.utils

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.lib.generated.CodecUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import java.util.function.Function

object CodecHelpers {

    @IncludedCodec
    val JSON_OBJECT_CODEC: MapCodec<JsonObject> = MapCodec.assumeMapUnsafe(CodecUtils.JSON_ELEMENT_CODEC.xmap({ it.asJsonObject }, Function.identity()))

    @IncludedCodec
    val COMPONENT_CODEC: Codec<Component> = ComponentSerialization.CODEC

}
