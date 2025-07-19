package me.owdding.lib.dev

import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.displays.Displays
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.resources.ResourceLocation

object DisplayTest : Screen(CommonComponents.EMPTY) {

    fun minecraft(path: String): ResourceLocation = ResourceLocation.withDefaultNamespace(path)

    override fun init() {
        super.init()

        LayoutFactory.vertical {
            display(Displays.circleTexture(90, 90, ResourceLocation.withDefaultNamespace("gamemode_switcher/slot")))
        }.also { it.setPosition(30, 100) }.visitWidgets { addRenderableWidget(it) }
    }

}
