package me.owdding.lib.rendering.text.serialization.nodes

import eu.pb4.placeholders.api.ParserContext
import eu.pb4.placeholders.api.node.TextNode
import eu.pb4.placeholders.api.node.parent.ParentNode
import me.owdding.lib.rendering.text.PrideShader
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import me.owdding.lib.rendering.text.withTextShader
import net.minecraft.network.chat.Style

class PrideGradientNode(children: Array<out TextNode>, val gradient: PrideShader, val direction: GradientTextShader.Direction, val speed: Float) : ParentNode(*children) {

    override fun applyFormatting(style: Style, context: ParserContext): Style {
        if (direction == GradientTextShader.Direction.RIGHT && speed == 1f) {
            return style.withTextShader(gradient)
        }

        return style.withTextShader(GradientTextShader(gradient.colors, direction, speed))
    }
}
