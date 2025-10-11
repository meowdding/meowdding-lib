package me.owdding.lib.dev

import me.owdding.lib.displays.Displays
import me.owdding.lib.rendering.text.TextShaders.withTextShader
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import me.owdding.lib.rendering.text.textShader
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.RemotePlayer
import net.minecraft.network.chat.CommonComponents
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.strikethrough

object DisplayTest : Screen(CommonComponents.EMPTY) {

    val shader = GradientTextShader(
        listOf(
            0xFF55CDFC.toInt(),
            0xFFF7A8B8.toInt(),
            0xFFFFFFFF.toInt(),
            0xFFF7A8B8.toInt(),
            0xFF55CDFC.toInt()
        ),
    )

    val entity = Displays.entity(RemotePlayer(McClient.self.level!!, McPlayer.self!!.gameProfile), 50, 100, 100 / 3)

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(graphics, mouseX, mouseY, partialTicks)

        entity.render(graphics, 100, 100)

        graphics.withTextShader(shader) {
            graphics.drawString("Hello, World!", 10, 10)
        }
        graphics.drawString(
            Text.of("Hello, ") {
                append("World") {
                    this.textShader = shader
                    this.italic = true
                }
                append(" and other World") {
                    this.textShader = shader
                    this.strikethrough = true
                }
                append("!")
            },
            10, 20,
        )
    }

    override fun isPauseScreen(): Boolean {
        return false
    }
}
