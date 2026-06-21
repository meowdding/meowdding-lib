package me.owdding.lib.utils

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.resources.Identifier
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent

internal fun RenderWorldEvent.renderBeaconBeam(
    poseStack: PoseStack,
    position: Vec3,
    texture: Identifier,
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
        BeaconRenderer.submitBeaconBeam(
            poseStack,
            this@renderBeaconBeam.submitNodeCollector,
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
