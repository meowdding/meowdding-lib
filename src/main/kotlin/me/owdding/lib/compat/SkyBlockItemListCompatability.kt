package me.owdding.lib.compat

import com.operationpotato.itemlist.api.ExclusionZoneManager
import com.operationpotato.itemlist.api.Plugin
import me.owdding.lib.compat.REICompatability.provide
import me.owdding.lib.utils.KnownMods
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.world.item.ItemStack

object SBILCompatability : Plugin {

    override fun registerExclusionZones(exclusionZoneManager: ExclusionZoneManager) {
        //exclusionZoneManager.addProvider(InventoryScreen::class.java, ::provide)
        //exclusionZoneManager.addProvider(CreativeModeInventoryScreen::class.java, ::provide)
    }
}

object SBILRuntimeCompatability {
    val installed get() = KnownMods.SKYBLOCK_ITEM_LIST.installed

    fun getHoveredItemStack(): ItemStack? {
        if (!installed) return null
        //? >= 26.1
        return null
        //? < 26.1
        //return null // TODO
    }
}
