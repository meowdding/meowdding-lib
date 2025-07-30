package me.owdding.lib.dev

import me.owdding.lib.rendering.text.TextShaders.withTextShader
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import tech.thatgravyboat.skyblockapi.platform.drawString

object DisplayTest : Screen(CommonComponents.EMPTY) {

    val shader = GradientTextShader(listOf(
        0xFF55CDFC.toInt(),
        0xFFF7A8B8.toInt(),
        0xFFFFFFFF.toInt(),
        0xFFF7A8B8.toInt(),
        0xFF55CDFC.toInt()
    ))

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(graphics, mouseX, mouseY, partialTicks)

        graphics.withTextShader(shader) {
            graphics.drawString("Hello, World!", 10, 10)
        }
    }

    override fun isPauseScreen(): Boolean {
        return false
    }
}
