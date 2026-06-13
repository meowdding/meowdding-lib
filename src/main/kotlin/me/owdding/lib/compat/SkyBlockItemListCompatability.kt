package me.owdding.lib.compat

import com.operationpotato.itemlist.api.ExclusionZoneManager
import com.operationpotato.itemlist.api.Plugin
import me.owdding.lib.events.ItemListRegisterExclusionZonesEvent
import me.owdding.lib.utils.KnownMods
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.Rect2i
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

object SBILCompatability : Plugin {

    override fun registerExclusionZones(exclusionZoneManager: ExclusionZoneManager) {
        exclusionZoneManager.addProvider(Screen::class.java) { screen ->
            val areas = mutableListOf<Rect2i>()
            val hide = ItemListRegisterExclusionZonesEvent(screen) { x, y, width, height ->
                areas.add(Rect2i(x, y, width, height))
            }.post(SkyBlockAPI.eventBus)
            if (hide) listOf(Rect2i(0, 0, screen.width, screen.height)) else areas
        }
    }
}

object SBILRuntimeCompatability {
    val installed get() = KnownMods.SKYBLOCK_ITEM_LIST.installed

    fun getHoveredItemStack(): ItemStack? {
        if (!installed) return null
        //? >= 26.1
        return null // TODO
        //? < 26.1
        //return null
    }
}
