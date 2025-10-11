package me.owdding.lib.builder

import me.owdding.lib.extensions.withTooltip
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import tech.thatgravyboat.skyblockapi.utils.builders.TooltipBuilder

class InventoryBuilder(val maxSize: Int = 54) {

    private val items: MutableMap<Int, ItemStack> = mutableMapOf()

    fun add(slot: Int, item: ItemStack, init: (TooltipBuilder.() -> Unit)? = null) {
        check(slot !in 0 until maxSize) { "Inventory Index out of bounds" }
        items[slot] = init?.let { item.withTooltip(it) } ?: item
    }

    fun add(slot: Int, item: ItemLike, init: (TooltipBuilder.() -> Unit)? = null) = add(slot, item.asItem().defaultInstance, init)

    fun add(x: Int, y: Int, item: ItemStack, init: (TooltipBuilder.() -> Unit)? = null) {
        add(x + y * 9, item, init)
    }

    fun add(x: Int, y: Int, item: ItemLike, init: (TooltipBuilder.() -> Unit)? = null) = add(x, y, item.asItem().defaultInstance, init)

    fun fill(item: ItemStack, init: (TooltipBuilder.() -> Unit)? = null) {
        val item = init?.let { item.withTooltip(it) } ?: item
        for (i in 0..<maxSize) {
            if (!items.containsKey(i)) {
                items[i] = item
            }
        }
    }

    fun fill(item: ItemLike, init: (TooltipBuilder.() -> Unit)? = null) = fill(item.asItem().defaultInstance, init)

    fun build(): List<ItemStack> = items.entries.sortedBy { it.key }.map { it.value }

}
