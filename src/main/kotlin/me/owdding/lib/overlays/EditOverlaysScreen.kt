package me.owdding.lib.overlays

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.drawFilledBox
import tech.thatgravyboat.skyblockapi.utils.text.CommonText

class EditOverlaysScreen(val modId: String? = null, val parent: Screen? = null) : Screen(CommonText.EMPTY) {

    //~ if >= 26.1 'render' -> 'extract'
    override fun extractBackground(guiGraphics: GuiGraphicsExtractor, i: Int, j: Int, f: Float) {
        guiGraphics.drawFilledBox(0, 0, width, height, 0x40000000)
        //~ if >= 26.1 'drawCenteredString' -> 'centeredText'
        guiGraphics.centeredText(font, "Edit Overlays", width / 2, height / 2, 0xFFFFFF)
    }

    override fun onClose() {
        val parent = this.parent ?: return super.onClose()
        McClient.setScreen(parent)
    }
}
