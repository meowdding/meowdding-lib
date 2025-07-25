package me.owdding.lib.dev

import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asWidget
import me.owdding.lib.displays.withBackground
import me.owdding.lib.layouts.ScalableWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items

object DisplayTest : Screen(CommonComponents.EMPTY) {

    fun minecraft(path: String): ResourceLocation = ResourceLocation.withDefaultNamespace(path)

    override fun init() {
        super.init()

        val widget = ScalableWidget(Displays.item(Items.CHEST).withBackground(0xFFFF0000u).asWidget())
        widget.scale(5.0)

        this.addRenderableWidget(widget)

        val widget1 = ScalableWidget(Displays.item(Items.CHEST).withBackground(0xFFFF0000u).asWidget())
        widget1.setPosition(100, 0)
        widget1.scale(2.0)

        this.addRenderableWidget(widget1)

        val widget2 = ScalableWidget(Displays.item(Items.CHEST).withBackground(0xFFFF0000u).asWidget())
        widget2.setPosition(200, 0)
        widget2.scale(4.0)
        this.addRenderableWidget(widget2)
    }

}
