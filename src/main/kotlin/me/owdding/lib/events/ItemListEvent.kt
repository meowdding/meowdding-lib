package me.owdding.lib.events

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public sealed interface ItemListEvent {
    public data class HoveredItemKeyPress(val screen: Screen, val stack: ItemStack?, val event: KeyEvent) : SkyBlockEvent()

    public data class RecipeButtonAdd(val recipe: Recipe<*>, val itemStack: ItemStack, private val button: (AbstractWidget) -> Unit) : SkyBlockEvent(){
        public fun register(widget: AbstractWidget) {
            button(widget)
        }
    }

    public data class RegisterExclusionZones(val screen: Screen, private val registrar: (Int, Int, Int, Int) -> Unit) : SkyBlockEvent() {
        public fun register(x: Int, y: Int, width: Int, height: Int) {
            registrar(x, y, width, height)
        }

        public fun register(layout: LayoutElement): Unit = register(layout.x, layout.y, layout.width, layout.height)
    }

    public class RegisterExcludedScreen(public val screen: Screen) : SkyBlockEvent() {
        public var reason: String? = null
            private set

        public fun exclude(reason: String) {
            this.reason = reason
        }

        public val isExcluded: Boolean get() = reason != null
    }
}
