@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.utils

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import kotlin.math.max

internal actual fun RenderWorldEvent.renderBeaconBeam(
    poseStack: PoseStack,
    position: Vec3,
    bufferSource: MultiBufferSource,
    texture: ResourceLocation,
    partialTicks: Float,
    textureScale: Float,
    gameTime: Long,
    yOffset: Int,
    height: Int,
    color: Int,
    beamRadius: Float,
    glowRadius: Float,
) {
    val player = McPlayer.self
    val cameraRenderState = McClient.self.levelRenderer.levelRenderState.cameraRenderState
    val distance = cameraRenderState.pos.subtract(position).horizontalDistance().toFloat()
    val scale = if (player == null || player.isScoping) 1.0f else max(1.0f, distance / 96.0f)

    val x = cameraRenderState.pos.x
    val y = cameraRenderState.pos.y
    val z = cameraRenderState.pos.z


    atCamera {
        translate(position)
        BeaconRenderer.submitBeaconBeam(
            poseStack,
            McClient.self.gameRenderer.featureRenderDispatcher.submitNodeStorage,
            texture,
            textureScale,
            gameTime % 40 + partialTicks,
            yOffset,
            height,
            color,
            beamRadius,
            glowRadius,
        )
    }
}
