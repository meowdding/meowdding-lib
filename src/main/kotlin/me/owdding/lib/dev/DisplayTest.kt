package me.owdding.lib.dev

import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.displays.*
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.utils.text.Text

object DisplayTest : Screen(CommonComponents.EMPTY) {

    override fun init() {
        super.init()
        addRenderableWidget(Displays.item(Items.STRING).asWidget())
        LayoutFactory.vertical {
            horizontal {
                listOf(
                    Displays.item(Items.STRING),
                    Displays.item(Items.DIAMOND),
                    Displays.item(Items.NETHERRACK).withTooltip(Text.of("meow")),
                ).toColumn().add()
                display(Displays.background(0xFFFFFFFFu, Displays.item(Items.GRAVEL)))
            }
            listOf(
                Displays.item(Items.STRING),
                Displays.item(Items.DIAMOND),
                Displays.item(Items.NETHERRACK).withTooltip(Text.of("meow")),
            ).toRow().add()
            Displays.item(Items.DIAMOND).add()
            Displays.text("asdafsafa").add()
        }.also {
            it.arrangeElements()
            FrameLayout.centerInRectangle(it, 40, 40, 100, 100)
        }.visitWidgets { addRenderableWidget(it) }
    }

}
