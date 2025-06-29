package me.owdding.lib.layouts

import earth.terrarium.olympus.client.components.base.BaseParentWidget
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asLayer
import me.owdding.lib.displays.asWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.resources.ResourceLocation

class BackgroundWidget(backgroundWidget: LayoutElement, widget: LayoutElement, val padding: Int = 0) : BaseParentWidget(), ContainerBypass {
    val body = widget.asWidget()
    val background = backgroundWidget.asWidget()

    constructor(background: Display, widget: LayoutElement, padding: Int = 0) : this(background.asWidget(), widget, padding)
    constructor(resourceLocation: ResourceLocation, widget: LayoutElement, padding: Int = 0) : this(
        Displays.sprite(
            resourceLocation,
            widget.width + padding * 2,
            widget.height + padding * 2,
        ),
        widget, padding,
    )

    constructor(vararg resourceLocation: ResourceLocation, widget: LayoutElement, padding: Int = 0) : this(
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

    override fun getWidth() = body.width + padding * 2
    override fun getHeight() = body.height + padding * 2

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        background.setPosition(this.x, this.y)
        body.setPosition(this.x + padding, this.y + padding)

        super.renderWidget(graphics, mouseX, mouseY, partialTicks)
    }
}
