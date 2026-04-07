package me.owdding.lib.overlays

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.lib.displays.Display
import me.owdding.lib.mixins.OverlayAccessor
import net.minecraft.client.gui.components.ChatComponent.getWidth
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.translated
import tech.thatgravyboat.skyblockapi.utils.text.Text


private fun Overlay._extract(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float) {
    extract(graphics, mouseX, mouseY)
}


interface Overlay {

    val modId: String
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

    //? < 26.1 {
    /*@Deprecated(message = "Outdated naming", replaceWith = ReplaceWith("extract"))
    fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int) = extract(graphics, mouseX, mouseY)
    *///? }
    fun extract(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int): Unit
    //? < 26.1
    //= @Suppress("DEPRECATION_ERROR") render(graphics, mouseX, mouseY)

    //? < 26.1 {
    /*@Deprecated(message = "Outdated naming", replaceWith = ReplaceWith("extract"))
    fun render(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float) = _extract(graphics, mouseX, mouseY, partialTicks)
    *///? }
    //~ if >= 26.1 'render' -> '_extract'
    fun extract(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float) = _extract(graphics, mouseX, mouseY, partialTicks)

    fun onRightClick() = ContextMenu.open {
        it.dangerButton(Text.translatable("mlib.overlay.edit.reset")) {
            position.resetPosition()
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
    MISC;
}
