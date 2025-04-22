package me.owdding.lib.extensions

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import it.unimi.dsi.fastutil.objects.ObjectSortedSets
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.item.component.TooltipDisplay
import tech.thatgravyboat.skyblockapi.utils.builders.TooltipBuilder
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import java.util.*

fun ItemStack.withoutTooltip() = withTooltip()

fun ItemStack.withTooltip(init: TooltipBuilder.() -> Unit = {}): ItemStack {
    val builder = TooltipBuilder().apply(init).lines()
    when {
        builder.isEmpty() -> this.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay(true, ObjectSortedSets.emptySet()))
        builder.size == 1 -> this.set(
            DataComponents.CUSTOM_NAME,
            builder.first().copy().apply {
                this.italic = false
            },
        )

        else -> {
            this.set(
                DataComponents.CUSTOM_NAME,
                builder.first().copy().apply {
                    this.italic = false
                },
            )
            val lore = builder.drop(1)
            this.set(DataComponents.LORE, ItemLore(lore, lore))
        }
    }
    return this
}

object ItemUtils {

    fun createSkull(textureBase64: String): ItemStack {
        val profile = GameProfile(UUID.randomUUID(), "a")
        profile.properties.put("textures", Property("textures", textureBase64))
        return createSkull(profile)
    }

    fun createSkull(profile: GameProfile): ItemStack {
        val stack = ItemStack(Items.PLAYER_HEAD)
        stack.set(DataComponents.PROFILE, ResolvableProfile(profile))
        return stack
    }
}

