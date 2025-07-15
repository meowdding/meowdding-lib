package me.owdding.lib.platform

import com.mojang.blaze3d.vertex.PoseStack
import me.owdding.lib.displays.Display
import net.minecraft.world.entity.LivingEntity

interface PlatformDisplaysThing {
    fun entity(entity: LivingEntity, width: Int, height: Int, scale: Int, mouseX: Float = Float.NaN, mouseY: Float = Float.NaN, spinning: Boolean = false): Display
    fun pushPop(display: Display, operations: PoseStack.() -> Unit): Display
}

object PlatformDisplays {

    // TODO this is disgusting, rn cloches stubs fail to generate because ksp bricked itself. Remove once fixed.
    private val instance = Class.forName("me.owdding.lib.platform.PlatformDisplaysThingImpl")
        .getConstructor()
        .newInstance()
        as PlatformDisplaysThing

    fun entity(entity: LivingEntity, width: Int, height: Int, scale: Int, mouseX: Float = Float.NaN, mouseY: Float = Float.NaN, spinning: Boolean = false): Display {
        return instance.entity(entity, width, height, scale, mouseX, mouseY, spinning)
    }

    fun pushPop(display: Display, operations: PoseStack.() -> Unit): Display {
        return instance.pushPop(display, operations)
    }
}
