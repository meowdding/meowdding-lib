package me.owdding.lib.utils

import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import tech.thatgravyboat.skyblockapi.utils.text.Text´
import kotlin.math.max

object RenderUtils {
    fun RenderWorldEvent.renderTextInWorld(
        position: Vec3,
        text: String,
        color: Int,
        center: Boolean = true,
    ) {
        renderTextInWorld(position, Text.of(text), color, center)
    }

    fun RenderWorldEvent.renderTextInWorld(
        position: Vec3,
        text: Component,
        color: Int,
        center: Boolean = true,
    ) {
        val x = camera.position.x
        val y = camera.position.y
        val z = camera.position.z

        val scale = max((camera.position.distanceTo(position).toFloat() / 10).toDouble(), 1.0).toFloat() * 0.025f

        poseStack.pushPop {
            poseStack.translate(position.x - x + 0.5, position.y - y + 1.07f, position.z - z + 0.5)
            poseStack.mulPose(camera.rotation())
            poseStack.scale(scale, -scale, scale)
            val xOffset = if (center) -McFont.width(text) / 2.0f else 0.0f

            ctx.drawString(
                text = text,
                x = xOffset,
                y = 0.0f,
                color = color.toUInt(),
                dropShadow = false,
                displayMode = Font.DisplayMode.SEE_THROUGH,
                backgroundColor = 0u,
                light = LightTexture.FULL_BRIGHT,
            )
        }
    }

}
