//? >= 26.1 {
package me.owdding.lib.compat

import com.operationpotato.itemlist.api.ExclusionZoneManager
import com.operationpotato.itemlist.api.HoveredItemManager
import com.operationpotato.itemlist.api.Plugin
import com.operationpotato.itemlist.api.RecipeButtonManager
import me.owdding.lib.events.ItemListEvent
import me.owdding.lib.utils.KnownMods
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.Rect2i
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import java.util.Optional

object SBILCompatability : Plugin {

    override fun registerExclusionZones(exclusionZoneManager: ExclusionZoneManager) {
        exclusionZoneManager.addProvider(Screen::class.java) { screen ->
            val areas = mutableListOf<Rect2i>()
            val hide = ItemListEvent.RegisterExclusionZones(screen) { x, y, width, height ->
                areas.add(Rect2i(x, y, width, height))
            }.post(SkyBlockAPI.eventBus)
            if (hide) listOf(Rect2i(0, 0, screen.width, screen.height)) else areas
        }
    }

    override fun registerHoveredItems(hoveredItemManager: HoveredItemManager) {
        hoveredItemManager.addConsumer { screen, stack, event ->
            ItemListEvent.HoveredItemKeyPress(screen, stack, event).post(SkyBlockAPI.eventBus)
        }
    }

    override fun registerRecipeButtons(manager: RecipeButtonManager) {
        manager.addProvider { recipeObj, stack ->
            val recipe = recipeObj as? Recipe<*> ?: return@addProvider Optional.empty()
            val buttons = mutableListOf<AbstractWidget>()
            ItemListEvent.RecipeButtonAdd(recipe, stack) { button: AbstractWidget ->
                buttons.add(button)
            }
            Optional.ofNullable(buttons.firstOrNull()) // TODO: change to provideMultiple once that is released
        }
    }
}

object SBILRuntimeCompatability {
    val installed get() = KnownMods.SKYBLOCK_ITEM_LIST.installed
}
//?}
