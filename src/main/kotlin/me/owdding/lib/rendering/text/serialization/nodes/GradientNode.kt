package me.owdding.lib.rendering.text.serialization.nodes

import eu.pb4.placeholders.api.ParserContext
import eu.pb4.placeholders.api.node.TextNode
import eu.pb4.placeholders.api.node.parent.ParentNode
import me.owdding.lib.rendering.text.builtin.GradientProvider as OldGradientProvider
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import me.owdding.lib.rendering.text.builtin.GradientTextShader.Direction as OldGradientDirection
import me.owdding.lib.rendering.text.withTextShader
import me.owdding.lib.utils.GradientDirection
import me.owdding.lib.utils.GradientProvider
import net.minecraft.network.chat.Style

public class GradientNode(nodes: Array<TextNode>, private val direction: GradientDirection, private val speed: Float, private val provider: GradientProvider) : ParentNode(*nodes) {
    //? if < 26.4
    public constructor(nodes: Array<TextNode>, direction: OldGradientDirection, speed: Float, provider: OldGradientProvider): this(nodes, direction.asNewDirection(), speed, provider.asNew())

    public fun getDirection(): OldGradientDirection = direction.toOld() ?: OldGradientDirection.RIGHT
    public fun getProvider(): OldGradientProvider = { provider.getColors() }

    override fun applyFormatting(style: Style, context: ParserContext): Style {
        return style.withTextShader(GradientTextShader(provider, direction, speed))
    }
}
