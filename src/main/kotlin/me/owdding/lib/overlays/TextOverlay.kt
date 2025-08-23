package me.owdding.lib.overlays

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width

open class TextOverlay(
    override val modName: Component,
    override val name: Component,
    override val position: Position,
    private val isEnabled: () -> Boolean,
    private val text: () -> Component,
) : Overlay {

    override val bounds: Pair<Int, Int> get() = text().width to 10
    override val enabled: Boolean get() = this.isEnabled()

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        graphics.drawString(text(), 0, 1, shadow = true)
    }
}
