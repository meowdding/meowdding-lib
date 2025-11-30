package me.owdding.lib.utils

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient

internal fun RenderWorldEvent.renderBeaconBeam(
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
    atCamera {
        translate(position)
        //? if > 1.21.8 {
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
        //?} else {
        /*BeaconRenderer.renderBeaconBeam(
            poseStack, bufferSource, texture, partialTicks, textureScale, gameTime, yOffset, height, color,
            beamRadius, glowRadius,
        )
        *///?}
    }
}
