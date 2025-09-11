package me.owdding.lib.compat

import me.owdding.ktmodules.Module
import me.owdding.lib.utils.KnownMods
import me.shedaniel.math.Rectangle
import me.shedaniel.math.impl.PointHelper
import me.shedaniel.rei.api.client.REIRuntime
import me.shedaniel.rei.api.client.gui.screen.DisplayScreen
import me.shedaniel.rei.api.client.gui.widgets.Slot
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import net.minecraft.client.gui.components.events.ContainerEventHandler
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.item.ItemStack
import org.lwjgl.glfw.GLFW
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text.send

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

@Module
object REIRuntimeCompatability {
    @Subscription
    fun onScreen(event: ScreenKeyPressedEvent) {
        if (event.key == GLFW.GLFW_KEY_L) getReiHoveredItemStack()?.hoverName?.send()
    }

    fun getReiHoveredItemStack(): ItemStack? {
        if (!KnownMods.REI.installed) return null
        runCatching { REIRuntime.getInstance() }.getOrNull() ?: return null
        return getItemList() ?: getRecipe() ?: getRecipeFallback()
    }

    private fun getItemList(): ItemStack? {
        fun getStack(listener: GuiEventListener): ItemStack? = when (listener) {
            is Slot -> listener.currentEntry.cheatsAs().value
            !is ContainerEventHandler -> null
            else -> listener.getChildAt(PointHelper.getMouseFloatingX(), PointHelper.getMouseFloatingY()).orElse(null)?.let(::getStack)
        }

        val listener = REIRuntime.getInstance().overlay.orElse(null) ?: return null
        return getStack(listener)
    }

    private fun getRecipe(): ItemStack? {
        val entryStack = ScreenRegistry.getInstance().getFocusedStack(McClient.self.screen, PointHelper.ofMouse()) ?: return null
        return entryStack.value as? ItemStack ?: entryStack.cheatsAs().value
    }

    private fun getRecipeFallback(): ItemStack? {
        val screen = McClient.self.screen as? DisplayScreen ?: return null
        val result = screen.resultsToNotice.firstOrNull() ?: return null
        return result.value as? ItemStack ?: result.cheatsAs().value
    }
}
