package me.owdding.lib.layouts

import earth.terrarium.olympus.client.components.base.BaseParentWidget
import earth.terrarium.olympus.client.ui.UIIcons
import me.owdding.lib.MeowddingLib
import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.builder.MIDDLE
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.Displays.background
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.LayoutElement
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import kotlin.math.max

class ClickToExpandWidget(title: LayoutElement, body: LayoutElement, val onClick: () -> Unit, val bodyOffset: Int = 7) : BaseParentWidget() {
    var allowBodyClick = true

    val title = LayoutFactory.horizontal(alignment = MIDDLE) {
        display(
            Displays.supplied {
                val chevron = if (expanded) UIIcons.CHEVRON_DOWN else MeowddingLib.id("chevron_right")
                background(chevron, Displays.empty(10, 10), TextColor.DARK_GRAY)
            },
        )
        spacer(2)
        widget(title)
    }.asWidget()
    val body = body.asWidget()
    var expanded = false

    init {
        this.addRenderableWidget(this.title)
        this.addRenderableWidget(this.body)
    }

    override fun getWidth() = if (expanded) max(body.width + bodyOffset, title.width) else title.width
    override fun getHeight() = title.height + if (expanded) body.height else 0

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if ((title.isMouseOver(mouseX, mouseY) || (allowBodyClick && body.isMouseOver(mouseX, mouseY))) && button == 0) {
            expanded = !expanded
            title.isFocused = expanded
            body.visible = expanded
            body.isFocused = expanded
            onClick()
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        title.setPosition(this.x, this.y)
        title.render(graphics, mouseX, mouseY, partialTicks)
        if (expanded) {
            body.setPosition(this.x + bodyOffset, this.y + title.height)
            body.render(graphics, mouseX, mouseY, partialTicks)
        }

        super.renderWidget(graphics, mouseX, mouseY, partialTicks)
    }
}
