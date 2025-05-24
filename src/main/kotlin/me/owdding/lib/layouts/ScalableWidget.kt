package me.owdding.lib.layouts

import me.owdding.lib.extensions.floor
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.ScreenDirection
import net.minecraft.client.gui.navigation.ScreenRectangle
import tech.thatgravyboat.skyblockapi.utils.extentions.scaled
import java.util.*
import kotlin.properties.Delegates.observable


class ScalableWidget(val original: AbstractWidget) :
    AbstractWidget(original.x, original.y, original.width, original.height, original.message), Scalable {

    fun <T> MutableList<T>.add(t: T) {
        add(t)
    }

    init {
        width = original.width
        height = original.height
    }

    private var scale by observable(1.0) { _, _, scale ->
        super.width = (original.width * scale).floor()
        super.height = (original.height * scale).floor()
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        guiGraphics.scaled(scale, scale, 1) {
            original.x = (this@ScalableWidget.x / scale).floor()
            original.y = (this@ScalableWidget.y / scale).floor()
            currentScale.addLast(scale)
            original.render(guiGraphics, (mouseX / scale).floor(), (mouseY / scale).floor(), partialTick)
            currentScale.removeLast()
        }
    }

    override fun scale(scale: Double) {
        require(scale > 0) { "Scale must be greater than 0." }
        this.scale = scale
    }

    fun <T> translated(originalMouseX: Double, originalMouseY: Double, consumer: (translatedMouseX: Double, translatedMouseY: Double) -> T): T {
        return consumer(originalMouseX / scale, originalMouseY / scale)
    }


    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) = translated(mouseX, mouseY) { x, y ->
        original.mouseClicked(x, y, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) = translated(mouseX, mouseY) { x, y ->
        original.mouseReleased(x, y, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double) = translated(mouseX, mouseY) { x, y ->
        original.mouseDragged(x, y, button, dragX / scale, dragY / scale)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double) = translated(mouseX, mouseY) { x, y ->
        original.mouseScrolled(x, y, scrollX * scale, scrollY * scale)
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double) = translated(mouseX, mouseY) { x, y ->
        original.isMouseOver(x, y)
    }

    override fun getRectangle(): ScreenRectangle? {
        val original = super.getRectangle() ?: return null
        return ScreenRectangle(original.left(), original.top(), (original.width() * scale).floor(), (original.height() * scale).floor())
    }

    override fun getBorderForArrowNavigation(direction: ScreenDirection): ScreenRectangle? {
        val original = super.getBorderForArrowNavigation(direction) ?: return null
        return ScreenRectangle(original.left(), original.top(), (original.width() * scale).floor(), (original.height() * scale).floor())
    }

    override fun narrationPriority(): NarratableEntry.NarrationPriority? = original.narrationPriority()

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        original.updateNarration(narrationElementOutput)
    }

    companion object {
        private val currentScale: Deque<Double> = LinkedList<Double>()
        fun getCurrentScale(): Double = if (currentScale.isEmpty()) 1.0 else currentScale.reduce { first, second -> first * second }
    }
}
