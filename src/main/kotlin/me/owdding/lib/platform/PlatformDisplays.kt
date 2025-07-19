package me.owdding.lib.platform

import com.mojang.blaze3d.vertex.PoseStack
import me.owdding.lib.displays.Display
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.msrandom.stub.Stub

@Stub
internal expect object PlatformDisplays {
    fun entity(
        entity: LivingEntity,
        width: Int,
        height: Int,
        scale: Int,
        mouseX: Float = Float.NaN,
        mouseY: Float = Float.NaN,
        spinning: Boolean = false,
    ): Display

    fun item(
        item: ItemStack,
        width: Int = 16,
        height: Int = 16,
        showTooltip: Boolean = false,
        showStackSize: Boolean = false,
        customStackText: Any? = null,
    ): Display

    fun pushPop(display: Display, operations: PoseStack.() -> Unit): Display
}
