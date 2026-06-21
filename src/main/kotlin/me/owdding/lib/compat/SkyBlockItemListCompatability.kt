package me.owdding.lib.compat

import com.operationpotato.itemlist.api.ExcludedScreensManager
import com.operationpotato.itemlist.api.ExclusionZoneManager
import com.operationpotato.itemlist.api.HoveredItemManager
import com.operationpotato.itemlist.api.Plugin
import com.operationpotato.itemlist.api.RecipeButtonManager
import me.owdding.lib.events.ItemListEvent
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
            ItemListEvent.RegisterExclusionZones(screen) { x, y, width, height ->
                areas.add(Rect2i(x, y, width, height))
            }.post(SkyBlockAPI.eventBus)
            areas
        }
    }

    override fun registerHoveredItems(hoveredItemManager: HoveredItemManager) {
        hoveredItemManager.addConsumer { screen, stack, event ->
            ItemListEvent.HoveredItemKeyPress(screen, stack, event).post(SkyBlockAPI.eventBus)
        }
    }

    override fun registerRecipeButtons(manager: RecipeButtonManager) {
        manager.addMultiProvider { recipeObj, stack ->
            val recipe = recipeObj as? Recipe<*> ?: return@addMultiProvider emptyList()
            val buttons = mutableListOf<AbstractWidget>()
            ItemListEvent.RecipeButtonAdd(recipe, stack) { button: AbstractWidget ->
                buttons.add(button)
            }.post(SkyBlockAPI.eventBus)
            buttons
        }
    }

    override fun registerExcludedScreens(excludedScreensManager: ExcludedScreensManager) {
        excludedScreensManager.addProvider(Screen::class.java) { screen ->
            val event = ItemListEvent.RegisterExcludedScreen(screen).apply {
                post(SkyBlockAPI.eventBus)
            }
            Optional.ofNullable(event.reason)
        }
    }
}
