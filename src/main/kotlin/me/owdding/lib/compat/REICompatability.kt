package me.owdding.lib.compat

import me.owdding.lib.utils.KnownMods
import me.shedaniel.math.Rectangle
import me.shedaniel.math.impl.PointHelper
import me.shedaniel.rei.api.client.REIRuntime
import me.shedaniel.rei.api.client.gui.widgets.Slot
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import net.minecraft.client.gui.components.events.ContainerEventHandler
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient

class REIRenderOverlayEvent(val screen: Screen, private val registrar: (Int, Int, Int, Int) -> Unit) : CancellableSkyBlockEvent() {

    fun register(x: Int, y: Int, width: Int, height: Int) {
        registrar(x, y, width, height)
    }

    fun register(layout: LayoutElement) = register(layout.x, layout.y, layout.width, layout.height)
}

object REICompatability : REIClientPlugin {

    override fun registerExclusionZones(zones: ExclusionZones) {
        zones.register(Screen::class.java) { screen ->
            val areas = mutableListOf<Rectangle>()
            val hide = REIRenderOverlayEvent(screen) { x, y, width, height ->
                areas.add(Rectangle(x, y, width, height))
            }.post(SkyBlockAPI.eventBus)

            if (hide) listOf(Rectangle(0, 0, screen.width, screen.height)) else areas
        }
    }
}

object REIRuntimeCompatability {
    fun getOverlay() = runCatching { REIRuntime.getInstance().overlay }.getOrNull()

    fun getOverlayStuff() = getOverlay()?.get()?.entryList?.focusedStack?.cheatsAs()?.value

    fun getReiHoveredItemStack(): ItemStack? {
        runCatching { REIRuntime.getInstance() }.getOrNull() ?: return null
        return getItemStackFromItemList() ?: getItemStackFromRecipe()
    }

    private fun getItemStackFromRecipe(): ItemStack? {
        val screen = McClient.self.screen.takeIf { it is AbstractContainerScreen<*> } ?: return null
        val entryStack = ScreenRegistry.getInstance().getFocusedStack(screen, PointHelper.ofMouse()) ?: return null
        return entryStack.value as? ItemStack ?: entryStack.cheatsAs().value
    }

    private fun getItemStackFromItemList(): ItemStack? {
        var listener: GuiEventListener? = REIRuntime.getInstance().overlay.orElse(null) ?: return null
        val mx = PointHelper.getMouseFloatingX().toInt()
        val my = PointHelper.getMouseFloatingY().toInt()

        while (true) {
            when (listener) {
                is Slot -> return listener.currentEntry.cheatsAs().value
                !is ContainerEventHandler -> return null
                else -> listener = listener.children()
                    .firstOrNull { it is LayoutElement && mx in it.x until (it.x + it.width) && my in it.y until (it.y + it.height) }
                    ?: return null
            }
        }
    }
}
