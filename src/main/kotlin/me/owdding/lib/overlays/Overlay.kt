package me.owdding.lib.overlays

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.lib.mixins.OverlayAccessor
import me.owdding.lib.utils.next
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.text.Text

interface Overlay {

    val ignoreChatScreenEdits: Boolean get() = false

    val modId: String
    val name: Component

    val properties: Collection<EditableProperty> get() = EditableProperty.entries
    val enabled: Boolean get() = true

    val position: Position
    val bounds: Pair<Int, Int>

    val alignedX: Float
        get() {
            val (x, _) = position
            val width = bounds.first * position.scale
            return when (position.alignment) {
                OverlayAlignment.START -> x.toFloat()
                OverlayAlignment.CENTER -> x - width / 2f
                OverlayAlignment.END -> x - width
            }
        }

    val editBounds: Rect
        get() {
            val (_, y) = position
            return Rect(alignedX.toInt(), y, bounds.first, bounds.second)
        }

    fun extract(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int) {}

    fun extract(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float) = extract(graphics, mouseX, mouseY)

    fun onRightClick() = ContextMenu.open {
        it.dangerButton(Text.translatable("mlib.overlay.edit.reset")) {
            position.resetPosition()
        }
    }

    fun setX(x: Int) {
        val screenWidth = McClient.window.guiScaledWidth
        val scaledWidth = bounds.first * position.scale
        if (bounds.first == 0 || scaledWidth >= screenWidth) return

        var anchorX = when (position.alignment) {
            OverlayAlignment.START -> x.toFloat()
            OverlayAlignment.CENTER -> x + scaledWidth / 2f
            OverlayAlignment.END -> x + scaledWidth
        }

        val minAnchorX = when (position.alignment) {
            OverlayAlignment.START -> 0f
            OverlayAlignment.CENTER -> scaledWidth / 2f
            OverlayAlignment.END -> scaledWidth
        }
        val maxAnchorX = when (position.alignment) {
            OverlayAlignment.START -> screenWidth - scaledWidth
            OverlayAlignment.CENTER -> screenWidth - scaledWidth / 2f
            OverlayAlignment.END -> screenWidth.toFloat()
        }

        anchorX = anchorX.coerceIn(minAnchorX, maxAnchorX)

        position.x = if (anchorX < screenWidth / 2) {
            anchorX.toInt()
        } else {
            (anchorX - screenWidth).toInt().coerceAtMost(-1)
        }
    }

    fun setY(y: Int) {
        val height = McClient.window.guiScaledHeight
        if (bounds.second == 0 || bounds.second * position.scale >= height) return
        position.y = if (y < height / 2) y.coerceAtLeast(0) else (y - height).coerceAtMost((-bounds.second * position.scale).toInt())
    }

    fun setScale(scale: Float) {
        position.scale = ((scale * 10f).toInt() / 10f).coerceAtLeast(0.1f)
    }

    fun nextAlignment() {
        position.alignment = position.alignment.next()
    }

    fun isEditing(): Boolean {
        var effectiveScreen = McScreen.self
        if (effectiveScreen is OverlayAccessor) {
            effectiveScreen = effectiveScreen.`mlib$getBackgroundScreen`()
        }
        return (effectiveScreen is EditOverlaysScreen && effectiveScreen.modId in arrayOf(null, modId)) || effectiveScreen is OverlayScreen
    }

    fun isEditingOverlay(): Boolean = (McScreen.self as? EditOverlaysScreen)?.modId !in arrayOf(null, modId)
}

enum class EditableProperty {
    X,
    Y,
    SCALE,
    ALIGNMENT,
    MISC;
}
