package me.owdding.lib.displays

import com.teamresourceful.resourcefullib.client.screens.CursorScreen
import earth.terrarium.olympus.client.components.base.BaseWidget
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import tech.thatgravyboat.skyblockapi.platform.scale
import tech.thatgravyboat.skyblockapi.utils.extentions.translated

class DisplayWidget(private val display: Display): BaseWidget() {
    private var renderer: WidgetRenderer<DisplayWidget?> = WidgetRenderer.empty()

    init {
        active = false
        width = display.getWidth()
        height = display.getHeight()
    }

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        display.render(graphics, x, y)
        renderer.render(graphics, WidgetRendererContext<DisplayWidget?>(this, mouseX, mouseY), partialTicks)
    }

    fun withRenderer(renderer: WidgetRenderer<DisplayWidget?>): DisplayWidget {
        this.renderer = renderer
        return this
    }

    override fun withSize(width: Int, height: Int): DisplayWidget = super.withSize(width, height) as DisplayWidget

    override fun getCursor(): CursorScreen.Cursor {
        return CursorScreen.Cursor.DEFAULT
    }

    companion object {
        fun <T : AbstractWidget> displayRenderer(display: Display) = WidgetRenderer<T> { graphics, context, _ ->
            graphics.translated(context.x, context.y) {
                graphics.scale(context.width.toFloat() / display.getWidth().toFloat(), context.height.toFloat() / display.getHeight().toFloat())
                display.render(graphics, 0, 0)
            }
        }
    }
}
