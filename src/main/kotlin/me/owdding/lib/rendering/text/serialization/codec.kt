package me.owdding.lib.rendering.text.serialization

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.lib.helper.TextShaderHolder
import me.owdding.lib.rendering.text.TextShaders
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.literal
import net.minecraft.network.chat.ComponentContents
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.contents.KeybindContents
import net.minecraft.network.chat.contents.NbtContents
import net.minecraft.network.chat.contents.ObjectContents
import net.minecraft.network.chat.contents.PlainTextContents
import net.minecraft.network.chat.contents.ScoreContents
import net.minecraft.network.chat.contents.SelectorContents
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.util.ExtraCodecs
import tech.thatgravyboat.skyblockapi.utils.extentions.forNullGetter
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.join
import java.util.function.Function
import kotlin.jvm.optionals.getOrNull

object TextCodecs {

    internal fun createContentCodec(): MapCodec<ComponentContents> {
        val idMapper = ExtraCodecs.LateBoundIdMapper<String, MapCodec<out ComponentContents>>()
        idMapper.put("text", PlainTextContents.MAP_CODEC)
        idMapper.put("translatable", TranslatableContents.MAP_CODEC)
        idMapper.put("keybind", KeybindContents.MAP_CODEC)
        idMapper.put("score", ScoreContents.MAP_CODEC)
        idMapper.put("selector", SelectorContents.MAP_CODEC)
        idMapper.put("nbt", NbtContents.MAP_CODEC)
        idMapper.put("object", ObjectContents.MAP_CODEC)

        return ComponentSerialization.createLegacyComponentMatcher(idMapper, ComponentContents::codec, "type")
    }

    val STYLE_WITH_SHADER_CODEC: MapCodec<Style> = RecordCodecBuilder.mapCodec {
        it.group(
            Style.Serializer.MAP_CODEC.forGetter(Function.identity()),
            TextShaders.CODEC.optionalFieldOf("text_shader").forNullGetter { style -> (style as? TextShaderHolder)?.`meowddinglib$getTextShader`() },
        ).apply(it) { style, shader ->
            (style as? TextShaderHolder)?.`meowddinglib$withTextShader`(shader.getOrNull())
        }
    }


    @IncludedCodec(named = "customComponentCodec")
    val CUSTOM_COMPONENT_CODEC: Codec<Component> = Codec.recursive("SkyOceanComponentCodec") { self ->
        val componentMatcher = createContentCodec()

        val codec: Codec<Component> = RecordCodecBuilder.create {
            it.group(
                componentMatcher.forGetter { it.contents },
                ExtraCodecs.nonEmptyList(self.listOf()).optionalFieldOf("extra", mutableListOf<Component>()).forGetter { it.siblings },
                STYLE_WITH_SHADER_CODEC.forGetter { it.style },
            ).apply(
                it,
            ) { contents: ComponentContents, siblings: List<Component>, style: Style ->
                MutableComponent(
                    contents,
                    siblings,
                    style,
                )
            }
        }

        return@recursive Codec.either(
            Codec.either(
                Codec.STRING,
                ExtraCodecs.nonEmptyList(self.listOf()),
            ),
            codec,
        ).xmap(
            {
                it.map(
                    { either ->
                        either.map(Component::literal, Text::join)
                    },
                    Function.identity(),
                )
            },
            {
                val collapsed = it.tryCollapseToString()
                if (collapsed != null) Either.left(Either.left(collapsed)) else Either.right(it)
            },
        )
    }

}
