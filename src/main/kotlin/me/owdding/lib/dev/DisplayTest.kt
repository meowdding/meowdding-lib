package me.owdding.lib.dev

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.resources.ResourceLocation

object DisplayTest : Screen(CommonComponents.EMPTY) {

    fun minecraft(path: String): ResourceLocation = ResourceLocation.withDefaultNamespace(path)

    override fun init() {
        super.init()

    }

}
