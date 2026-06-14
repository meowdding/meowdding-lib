package me.owdding.lib.events

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

sealed interface ItemListEvent {
    data class HoveredItemKeyPressEvent(val screen: Screen, val stack: ItemStack?, val event: KeyEvent) : SkyBlockEvent()

    data class RecipeButtonAddEvent(val recipe: Recipe<*>, val itemStack: ItemStack, private val button: (AbstractWidget) -> Unit) : SkyBlockEvent()

    data class RegisterExclusionZonesEvent(val screen: Screen, private val registrar: (Int, Int, Int, Int) -> Unit) : CancellableSkyBlockEvent() {
        fun register(x: Int, y: Int, width: Int, height: Int) {
            registrar(x, y, width, height)
        }

        fun register(layout: LayoutElement) = register(layout.x, layout.y, layout.width, layout.height)
    }
}
