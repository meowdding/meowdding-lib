package me.owdding.lib.overlays

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.lib.events.overlay.FinishOverlayEditingEvent
import me.owdding.lib.platform.screens.KeyEvent
import me.owdding.lib.platform.screens.MeowddingScreen
import me.owdding.lib.platform.screens.MouseButtonEvent
import me.owdding.lib.utils.keys
import me.owdding.lib.utils.keysOf
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import org.lwjgl.glfw.GLFW
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.platform.*
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text

private val ADD_KEY = keys {
    withSymbol("+")
    withKey(InputConstants.KEY_EQUALS)
    withKey(InputConstants.KEY_ADD)
}

private val MINUS_KEY = keys {
    withSymbol("-")
    withKey(InputConstants.KEY_MINUS)
    withKey(GLFW.GLFW_KEY_KP_SUBTRACT)
}

private val UP_KEY = keysOf(InputConstants.KEY_UP)
private val DOWN_KEY = keysOf(InputConstants.KEY_DOWN)
private val LEFT_KEY = keysOf(InputConstants.KEY_LEFT)
private val RIGHT_KEY = keysOf(InputConstants.KEY_RIGHT)

class OverlayScreen(private val overlay: Overlay, private val parent: Screen?) : MeowddingScreen(CommonText.EMPTY) {

    private var dragging = false
    private var relativeX = 0
    private var relativeY = 0

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(graphics, mouseX, mouseY, partialTicks)
        val (x, y) = overlay.position
        val (width, height) = overlay.bounds * overlay.position.scale

        val hovered = mouseX - x in 0..width && mouseY - y in 0..height
        graphics.pushPop {
            graphics.translate(x.toFloat(), y.toFloat())
            graphics.scale(overlay.position.scale, overlay.position.scale)
            overlay.render(graphics, mouseX, mouseY, partialTicks)
        }
        if (hovered) {
            graphics.drawFilledBox(x, y, width, height, 0x50000000)
            graphics.drawOutline(x - 1, y - 1, width + 2, height + 2, 0xFFFFFFFF.toInt())
            graphics.showTooltip(
                Text.multiline(
                    overlay.name,
                    CommonText.EMPTY,
                    Text.translatable("mlib.overlay.edit.options"),
                    Text.translatable("mlib.overlay.mod.${overlay.modId}"),
                ),
            )
        }

        val center = (this.width / 2f).toInt()
        graphics.drawCenteredString(font, "X: ${overlay.position.x}, Y: ${overlay.position.y}", center, this.height - 40, -1)
        graphics.drawCenteredString(font, "Scale: ${overlay.position.scale}", center, this.height - 30, -1)
        graphics.drawCenteredString(font, "Use +/- to scale, arrow keys to move around.", center, this.height - 20, -1)
    }

    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderMenuBackground(guiGraphics)
    }

    override fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        if (dragging) {
            if (EditableProperty.X in overlay.properties) overlay.setX(mouseEvent.x.toInt() - relativeX)
            if (EditableProperty.Y in overlay.properties) overlay.setY(mouseEvent.y.toInt() - relativeY)
        }
        return true
    }

    override fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean {
        dragging = false
        return true
    }

    override fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean): Boolean {
        val (mouseX, mouseY) = mouseEvent
        val (button) = mouseEvent.buttonInfo
        val (x, y) = overlay.position
        val (width, height) = overlay.bounds * overlay.position.scale

        if ((mouseX - x).toInt() in 0..width && (mouseY - y).toInt() in 0..height) {
            when (button) {
                InputConstants.MOUSE_BUTTON_LEFT -> {
                    relativeX = (mouseX - x).toInt()
                    relativeY = (mouseY - y).toInt()
                    dragging = true
                }

                InputConstants.MOUSE_BUTTON_RIGHT -> {
                    overlay.onRightClick()
                }
            }
        } else if (this.parent != null) {
            save()
            McClient.setScreen(this.parent)
        }
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (isMouseOverOverlay(mouseX, mouseY) && EditableProperty.SCALE in overlay.properties) {
            val scale = overlay.position.scale + scrollY * 0.1f
            overlay.setScale(scale.toFloat())
            return true
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        val (key, scan) = keyEvent
        val multiplier = if (McScreen.isShiftDown) 10 else 1
        val (x, y) = overlay.position
        val scale = overlay.position.scale

        when {
            UP_KEY.isDown(key, scan) && EditableProperty.Y in overlay.properties -> overlay.setY(y - multiplier)
            DOWN_KEY.isDown(key, scan) && EditableProperty.Y in overlay.properties -> overlay.setY(y + multiplier)
            LEFT_KEY.isDown(key, scan) && EditableProperty.X in overlay.properties -> overlay.setX(x - multiplier)
            RIGHT_KEY.isDown(key, scan) && EditableProperty.X in overlay.properties -> overlay.setX(x + multiplier)
            ADD_KEY.isDown(key, scan) && EditableProperty.SCALE in overlay.properties -> overlay.setScale(scale + 0.1f)
            MINUS_KEY.isDown(key, scan) && EditableProperty.SCALE in overlay.properties -> overlay.setScale(scale - 0.1f)
            else -> return super.keyPressed(keyEvent)
        }
        return true
    }

    override fun onClose() {
        save()
        if (parent != null && parent !is ChatScreen) {
            McClient.setScreen(parent)
        } else {
            super.onClose()
        }
    }

    fun save() = FinishOverlayEditingEvent(overlay.modId).post(SkyBlockAPI.eventBus)

    fun isMouseOverOverlay(mouseX: Double, mouseY: Double): Boolean {
        val (x, y) = overlay.position
        val (width, height) = overlay.bounds * overlay.position.scale
        return ((mouseX - x).toInt() in 0..width && (mouseY - y).toInt() in 0..height)
    }
}

private operator fun Pair<Int, Int>.times(scale: Float): Pair<Int, Int> {
    return (first * scale).toInt() to (second * scale).toInt()
}
