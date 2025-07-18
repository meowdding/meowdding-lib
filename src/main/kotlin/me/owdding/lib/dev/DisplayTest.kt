package me.owdding.lib.dev

import earth.terrarium.olympus.client.ui.UIConstants
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asButton
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.CommonComponents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.DyedItemColor
import tech.thatgravyboat.skyblockapi.helpers.McLevel

object DisplayTest : Screen(CommonComponents.EMPTY) {

    override fun init() {
        super.init()

        val zombie1 = Zombie(McLevel.self).also { zombie ->
            zombie.setItemSlot(EquipmentSlot.HEAD, Items.PLAYER_HEAD.defaultInstance)
            zombie.setItemSlot(EquipmentSlot.CHEST, Items.LEATHER_CHESTPLATE.defaultInstance.also { stack ->
                stack.set(DataComponents.DYED_COLOR, DyedItemColor(0xFFFF00))
            })
        }
        val zombie2 = Zombie(McLevel.self).also { zombie ->
            zombie.setItemSlot(EquipmentSlot.HEAD, Items.PLAYER_HEAD.defaultInstance)
            zombie.setItemSlot(EquipmentSlot.CHEST, Items.LEATHER_CHESTPLATE.defaultInstance.also { stack ->
                stack.set(DataComponents.DYED_COLOR, DyedItemColor(0xFF00FF))
            })
        }

        addRenderableWidget(Displays.entity(zombie1, 30, 70, 35).asButton().withTexture(UIConstants.BUTTON).withPosition(0, 0))
        addRenderableWidget(Displays.entity(zombie2, 30, 70, 35).asButton().withTexture(UIConstants.BUTTON).withPosition(30, 70))
    }

}
