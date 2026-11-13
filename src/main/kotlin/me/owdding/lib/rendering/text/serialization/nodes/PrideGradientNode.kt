package me.owdding.lib.rendering.text.serialization.nodes

import eu.pb4.placeholders.api.ParserContext
import eu.pb4.placeholders.api.node.TextNode
import eu.pb4.placeholders.api.node.parent.ParentNode
import me.owdding.lib.rendering.text.PrideShader
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import me.owdding.lib.rendering.text.builtin.GradientTextShader.Direction as OldGradientDirection
import me.owdding.lib.rendering.text.withTextShader
import me.owdding.lib.utils.GradientDirection
import net.minecraft.network.chat.Style

class PrideGradientNode(children: Array<out TextNode>, val gradient: PrideShader, private val direction: GradientDirection, val speed: Float) : ParentNode(*children) {
    //? if < 26.4
    constructor(children: Array<out TextNode>, gradient: PrideShader, direction: OldGradientDirection, speed: Float) : this(children, gradient, direction.asNewDirection(), speed)
    fun getDirection() = direction.toOld() ?: OldGradientDirection.RIGHT

    override fun applyFormatting(style: Style, context: ParserContext): Style {
        if (direction == GradientDirection.RIGHT && speed == 1f) {
            return style.withTextShader(gradient)
        }

        return style.withTextShader(GradientTextShader(gradient.getColors(), direction, speed))
    }
}
