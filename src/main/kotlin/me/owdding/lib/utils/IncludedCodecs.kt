package me.owdding.lib.utils

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.core.ClientAsset
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.resources.ResourceLocation

internal object IncludedCodecs {

    @IncludedCodec
    val RESOURCE_LOCATION: Codec<ResourceLocation> = ResourceLocation.CODEC

    @IncludedCodec
    val COMPONENT_CODEC: Codec<Component> = ComponentSerialization.CODEC


    @IncludedCodec
    val TEXT_COLOR: Codec<net.minecraft.network.chat.TextColor> = net.minecraft.network.chat.TextColor.CODEC

    @IncludedCodec
    val CLIENT_ASSET: Codec<ClientAsset> = ClientAsset.CODEC

}
