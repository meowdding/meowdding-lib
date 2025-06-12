package me.owdding.lib.layouts

import earth.terrarium.olympus.client.components.base.BaseParentWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation

class BackgroundWidget(val background: ResourceLocation, widget: LayoutElement, val padding: Int = 0) : BaseParentWidget(), ContainerBypass {
    val body = widget.asWidget()

    init {
        this.addRenderableWidget(this.body)
    }

    override fun getWidth() = body.width + padding * 2
    override fun getHeight() = body.height + padding * 2

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        graphics.blitSprite(RenderType::guiTextured, background, this.x, this.y, body.width + padding * 2, body.height + padding * 2)

        body.setPosition(this.x + padding, this.y + padding)

        super.renderWidget(graphics, mouseX, mouseY, partialTicks)
    }
}
