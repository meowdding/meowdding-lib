package me.owdding.lib.layouts

import earth.terrarium.olympus.client.components.base.BaseParentWidget
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.LayoutElement

public class PaddedWidget(
    widget: LayoutElement,
    internal val paddingTop: Int,
    internal val paddingRight: Int,
    internal val paddingBottom: Int,
    internal val paddingLeft: Int,
) : BaseParentWidget() {
    public constructor(widget: LayoutElement, padding: Int) : this(widget, padding, padding, padding, padding)

    internal val widget: AbstractWidget by lazy { widget.asWidget() }
    internal val actualWidget = widget

    init {
        actualWidget.visitWidgets {
            this.addRenderableWidget(it)
        }
    }

    override fun getWidth(): Int = widget.width + paddingLeft + paddingRight
    override fun getHeight(): Int = widget.height + paddingTop + paddingBottom

    override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float) {
        widget.setPosition(this.x + paddingLeft, this.y + paddingTop)
        actualWidget.visitWidgets {
            it.extractRenderState(graphics, mouseX, mouseY, partialTicks)
        }
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTicks)
    }
}
