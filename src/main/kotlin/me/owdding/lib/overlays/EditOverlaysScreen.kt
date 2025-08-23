package me.owdding.lib.overlays

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.CommonText

class EditOverlaysScreen(val parent: Screen? = null) : Screen(CommonText.EMPTY) {

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        guiGraphics.fill(0, 0, width, height, 0x40000000)
        guiGraphics.drawCenteredString(font, "Edit Overlays", width / 2, height / 2, 0xFFFFFF)
    }

    override fun onClose() {
        val parent = this.parent ?: return super.onClose()
        McClient.setScreen(parent)
    }
}
