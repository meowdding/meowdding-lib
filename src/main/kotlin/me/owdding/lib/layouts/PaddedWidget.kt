package me.owdding.lib.layouts

import earth.terrarium.olympus.client.components.base.BaseParentWidget
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.layouts.LayoutElement

class PaddedWidget(widget: LayoutElement, val paddingTop: Int, val paddingRight: Int, val paddingBottom: Int, val paddingLeft: Int) : BaseParentWidget() {
    constructor(widget: LayoutElement, padding: Int) : this(widget, padding, padding, padding, padding)

    val widget = widget.asWidget()

    init {
        this.addRenderableWidget(this.widget)
    }

    override fun getWidth() = widget.width + paddingLeft + paddingRight
    override fun getHeight() = widget.height + paddingTop + paddingBottom

    //~ if >= 26.1 'renderWidget' -> 'extractWidgetRenderState'
    override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float) {
        widget.setPosition(this.x + paddingLeft, this.y + paddingTop)
        //~ if >= 26.1 'render' -> 'extractRenderState'
        widget.extractRenderState(graphics, mouseX, mouseY, partialTicks)

        //~ if >= 26.1 'renderWidget' -> 'extractWidgetRenderState'
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTicks)
    }
}
