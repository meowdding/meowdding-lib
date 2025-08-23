package me.owdding.lib.overlays

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.lib.mixins.OverlayAccessor
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.text.Text

interface Overlay {

    val modName: Component
    val name: Component

    val properties: Collection<EditableProperty> get() = EditableProperty.entries
    val enabled: Boolean get() = true

    val position: Position
    val bounds: Pair<Int, Int>
    val editBounds: Rect
        get() {
            val (x, y) = position
            return Rect(x, y, bounds.first, bounds.second)
        }

    fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {}
    fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) = render(graphics, mouseX, mouseY)

    fun onRightClick() = ContextMenu.open {
        it.dangerButton(Text.of("Reset Position")) {
            position.reset()
        }
    }

    fun setX(x: Int) {
        val width = McClient.window.guiScaledWidth
        if (bounds.first == 0 || bounds.first * position.scale >= width) return
        position.x = if (x < width / 2) x.coerceAtLeast(0) else (x - width).coerceAtMost((-bounds.first * position.scale).toInt())
    }

    fun setY(y: Int) {
        val height = McClient.window.guiScaledHeight
        if (bounds.second == 0 || bounds.second * position.scale >= height) return
        position.y = if (y < height / 2) y.coerceAtLeast(0) else (y - height).coerceAtMost((-bounds.second * position.scale).toInt())
    }

    fun setScale(scale: Float) {
        position.scale = (scale * 10f).toInt() / 10f
    }

    companion object {

        fun isEditing(): Boolean {
            var effectiveScreen = McScreen.self
            if (effectiveScreen is OverlayAccessor) {
                effectiveScreen = effectiveScreen.`mlib$getBackgroundScreen`()
            }
            return effectiveScreen is EditOverlaysScreen || effectiveScreen is OverlayScreen
        }
    }
}

enum class EditableProperty {
    X,
    Y,
    SCALE,
    MISC;
}
