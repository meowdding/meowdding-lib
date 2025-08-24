package me.owdding.lib.overlays

import me.owdding.ktmodules.Module
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseClickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.platform.pushPop
import tech.thatgravyboat.skyblockapi.platform.scale
import tech.thatgravyboat.skyblockapi.platform.showTooltip
import tech.thatgravyboat.skyblockapi.platform.translate
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text

@Module
object Overlays {

    private val overlays = mutableListOf<Overlay>()

    fun register(overlay: Overlay) {
        overlays.add(overlay)
    }

    private fun isOverlayScreen(screen: Screen?, mouseX: Int, mouseY: Int): Boolean {
        return (screen is ChatScreen && !isWithinChatBounds(mouseX, mouseY)) || screen is EditOverlaysScreen
    }

    private fun isWithinChatBounds(mouseX: Int, mouseY: Int): Boolean {
        val window = McClient.window
        val chat = McClient.chat

        val height = chat.height
        val width = chat.width
        val x = 0
        val y = window.guiScaledHeight - 40 - height

        return mouseX in x..width && mouseY in y..window.guiScaledHeight - 40
    }

    @Subscription
    fun onHudRender(event: RenderHudEvent) {
        if (McClient.options.hideGui) return

        val graphics = event.graphics
        val screen = McScreen.self
        val (mouseX, mouseY) = McClient.mouse
        overlays.forEach {
            if (!it.enabled) return@forEach
            val (x, y) = it.position
            graphics.pushPop {
                graphics.translate(x.toFloat(), y.toFloat())
                graphics.scale(it.position.scale, it.position.scale)
                it.render(graphics, mouseX.toInt(), mouseY.toInt(), event.partialTicks)
            }

            val rect = it.editBounds * it.position.scale

            if (isOverlayScreen(screen, mouseX.toInt(), mouseY.toInt()) && rect.contains(mouseX.toInt(), mouseY.toInt())) {
                if (it.isEditingOverlay()) return@forEach
                graphics.fill(rect.x, rect.y, rect.right, rect.bottom, 0x50000000)
                graphics.renderOutline(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2, 0xFFFFFFFF.toInt())
                if (it.properties.isNotEmpty()) {
                    graphics.showTooltip(
                        Text.multiline(
                            it.name,
                            CommonText.EMPTY,
                            Text.translatable("mlib.overlay.edit"),
                            Text.translatable("mlib.overlay.mod.${it.modId}"),
                        ),
                    )
                } else {
                    graphics.showTooltip(it.name)
                }
            }
        }
    }

    @Subscription
    fun onMouseClick(event: ScreenMouseClickEvent.Pre) {
        if (!isOverlayScreen(event.screen, event.x.toInt(), event.y.toInt())) return

        for (overlay in overlays.reversed()) {
            if (!overlay.enabled) continue
            if (overlay.properties.isEmpty()) continue
            if (overlay.isEditingOverlay()) continue
            val rect = overlay.editBounds * overlay.position.scale

            if (rect.contains(event.x, event.y)) {
                val screen = McScreen.self
                McClient.setScreen(OverlayScreen(overlay, screen))
                return
            }
        }
    }

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        event.register("meowdding") {
            then("overlays") {
                callback { McClient.setScreen(EditOverlaysScreen()) }
            }
            thenCallback("mliboverlays") {
                McClient.setScreen(EditOverlaysScreen("meowdding-lib"))
            }
        }
    }
}
