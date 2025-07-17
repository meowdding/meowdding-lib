package me.owdding.lib.dev

import earth.terrarium.olympus.client.ui.UIConstants
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asButton
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import tech.thatgravyboat.skyblockapi.helpers.McPlayer

object DisplayTest : Screen(CommonComponents.EMPTY) {

    override fun init() {
        super.init()




        addRenderableWidget(Displays.entity(McPlayer.self!!, 30, 70, 35).asButton().withTexture(UIConstants.BUTTON))
    }

}
