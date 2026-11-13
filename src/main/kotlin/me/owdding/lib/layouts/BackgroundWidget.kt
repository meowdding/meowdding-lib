package me.owdding.lib.layouts

import earth.terrarium.olympus.client.components.base.BaseParentWidget
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asLayer
import me.owdding.lib.displays.asWidget
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.resources.Identifier

public class BackgroundWidget(backgroundWidget: LayoutElement, widget: LayoutElement, internal val padding: Int = 0) : BaseParentWidget(), ContainerBypass {
    internal val body = widget.asWidget()
    internal val background = backgroundWidget.asWidget()

    public constructor(background: Display, widget: LayoutElement, padding: Int = 0) : this(background.asWidget(), widget, padding)
    public constructor(resourceLocation: Identifier, widget: LayoutElement, padding: Int = 0) : this(
        Displays.sprite(
            resourceLocation,
            widget.width + padding * 2,
            widget.height + padding * 2,
        ),
        widget, padding,
    )

    public constructor(vararg resourceLocation: Identifier, widget: LayoutElement, padding: Int = 0) : this(
        resourceLocation.map {
            Displays.sprite(
                it,
                widget.width + padding * 2,
                widget.height + padding * 2,
            )
        }.asLayer(),
        widget,
        padding,
    )

    init {
        this.renderables.add(backgroundWidget.asWidget())
        this.addRenderableWidget(this.body)
    }

    override fun getWidth(): Int = body.width + padding * 2
    override fun getHeight(): Int = body.height + padding * 2

    override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float) {
        background.setPosition(this.x, this.y)
        body.setPosition(this.x + padding, this.y + padding)

        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTicks)
    }
}
