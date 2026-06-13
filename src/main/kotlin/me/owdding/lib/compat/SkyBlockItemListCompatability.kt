package me.owdding.lib.compat

import com.operationpotato.itemlist.api.ExclusionZoneManager
import com.operationpotato.itemlist.api.HoveredItemManager
import com.operationpotato.itemlist.api.Plugin
import com.operationpotato.itemlist.api.impl.PluginManager
import me.owdding.lib.events.ItemListHoveredItemKeyPressEvent
import me.owdding.lib.events.ItemListRegisterExclusionZonesEvent
import me.owdding.lib.utils.KnownMods
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.Rect2i
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen

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

    override fun registerHoveredItems(hoveredItemManager: HoveredItemManager) {
        hoveredItemManager.addConsumer { screen, stack, event ->
            ItemListHoveredItemKeyPressEvent(screen, stack, event).post(SkyBlockAPI.eventBus)
        }
    }
}

object SBILRuntimeCompatability {
    val installed get() = KnownMods.SKYBLOCK_ITEM_LIST.installed
}
