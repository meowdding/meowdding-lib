package me.owdding.lib.builder

import me.owdding.lib.extensions.withTooltip
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import tech.thatgravyboat.skyblockapi.utils.builders.TooltipBuilder

public class InventoryBuilder(internal val maxSize: Int = 54) {

    private val items: MutableMap<Int, ItemStack> = mutableMapOf()

    public fun add(slot: Int, item: ItemStack, init: (TooltipBuilder.() -> Unit)? = null) {
        check(slot in 0 until maxSize) { "Inventory Index out of bounds" }
        items[slot] = init?.let { item.withTooltip(it) } ?: item
    }

    public fun add(slot: Int, item: ItemLike, init: (TooltipBuilder.() -> Unit)? = null): Unit = add(slot, item.asItem().defaultInstance, init)

    public fun add(x: Int, y: Int, item: ItemStack, init: (TooltipBuilder.() -> Unit)? = null) {
        add(x + y * 9, item, init)
    }

    public fun add(x: Int, y: Int, item: ItemLike, init: (TooltipBuilder.() -> Unit)? = null): Unit = add(x, y, item.asItem().defaultInstance, init)

    public fun fill(item: ItemStack, init: (TooltipBuilder.() -> Unit)? = null) {
        val item = init?.let { item.withTooltip(it) } ?: item
        for (i in 0..<maxSize) {
            if (!items.containsKey(i)) {
                items[i] = item
            }
        }
    }

    public fun fill(item: ItemLike, init: (TooltipBuilder.() -> Unit)? = null): Unit = fill(item.asItem().defaultInstance, init)

    public fun build(): List<ItemStack> = items.entries.sortedBy { it.key }.map { it.value }

}
