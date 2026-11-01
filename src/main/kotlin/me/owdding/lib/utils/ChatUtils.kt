package me.owdding.lib.utils

import me.owdding.lib.rendering.text.PrideShader
import me.owdding.lib.rendering.text.textShader
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

internal val chatPrefix = Text.of {
    append("[")
    append("Meowdding") {
        textShader = PrideShader.TRANS
    }
    append("]")
    color = TextColor.GRAY
}

internal fun Component.withPrefix(): Component = Text.join(chatPrefix, CommonText.SPACE, this)
internal fun Component.sendWithPrefix() = withPrefix().send()
