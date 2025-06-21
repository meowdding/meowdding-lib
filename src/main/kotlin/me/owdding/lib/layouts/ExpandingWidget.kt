package me.owdding.lib.layouts

import earth.terrarium.olympus.client.components.base.BaseParentWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget

class ExpandingWidget(val widget: AbstractWidget, val margin: Int) : BaseParentWidget(widget.width + margin * 2, widget.height + margin * 2) {

    init {
        this.addRenderableWidget(widget)
    }

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (widget.isMouseOver(mouseX.toDouble(), mouseY.toDouble())) {
            widget.setPosition(this.x, this.y)
            widget.setSize(this.width, this.height)
        } else {
            widget.setPosition(this.x + margin, this.y + margin)
            widget.setSize(this.width - margin * 2, this.height - margin * 2)
        }
        super.renderWidget(graphics, mouseX, mouseY, partialTicks)
    }
}
