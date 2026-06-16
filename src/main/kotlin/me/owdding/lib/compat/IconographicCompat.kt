package me.owdding.lib.compat

import me.owdding.lib.MeowddingLib
import me.owdding.lib.utils.MeowddingLogger
import me.owdding.lib.utils.MeowddingLogger.Companion.featureLogger
import net.minecraft.world.item.ItemStack
import java.util.function.BiConsumer


data object IconographicCompat : MeowddingLogger by MeowddingLib.featureLogger() {

    var withItemCallback: ((item: ItemStack, runnable: () -> Unit) -> Unit) = { _, runnable -> runnable() }

    fun withItem(item: ItemStack, runnable: () -> Unit) {
        withItemCallback(item, runnable)
    }

    fun setupItemCompat(consumer: BiConsumer<ItemStack, Runnable>) {
        info("Setting up iconographic compat!")
        this.withItemCallback = { item, runnable -> consumer.accept(item) { runnable() } }
    }
}
