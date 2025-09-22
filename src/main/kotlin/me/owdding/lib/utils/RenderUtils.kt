package me.owdding.lib.utils

import com.mojang.blaze3d.systems.RenderSystem
import me.owdding.lib.rendering.world.RenderTypes.BLOCK_FILL_TRIANGLE_THROUGH_WALLS
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShapeRenderer
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.math.max


object RenderUtils {

    val RenderWorldEvent.partialTicks: Float get() = this.ctx.tickCounter().getGameTimeDeltaPartialTick(false)

    fun RenderWorldEvent.renderTextInWorld(
        position: Vec3,
        text: String,
        color: Int = 0xFFFFFFFF.toInt(),
        center: Boolean = true,
    ) {
        renderTextInWorld(position, Text.of(text), color, center)
    }

    fun RenderWorldEvent.renderTextInWorld(
        position: Vec3,
        text: Component,
        color: Int = 0xFFFFFFFF.toInt(),
        center: Boolean = true,
        yOffset: Float = 0f,
    ) {
        val x = camera.position.x
        val y = camera.position.y
        val z = camera.position.z

        val scale = max((camera.position.distanceTo(position).toFloat() / 10).toDouble(), 1.0).toFloat() * 0.033f

        poseStack.pushPop {
            poseStack.translate(position.x - x + 0.5, position.y - y + 1.07f, position.z - z + 0.5)
            poseStack.translate(0f, yOffset * -scale, 0f)
            poseStack.mulPose(camera.rotation())
            poseStack.scale(scale, -scale, scale)
            val xOffset = if (center) -McFont.width(text) / 2.0f else 0.0f

            ctx.drawString(
                text = text,
                x = xOffset,
                y = 0.0f,
                color = color.toUInt(),
                dropShadow = true,
                displayMode = Font.DisplayMode.SEE_THROUGH,
                backgroundColor = 0x70000000.toUInt(),
                light = LightTexture.FULL_BRIGHT,
            )
        }
    }

    fun RenderWorldEvent.renderBox(
        position: Vec3,
        color: Int,
    ) {
        renderBox(AABB.unitCubeFromLowerCorner(position), color)
    }

    fun RenderWorldEvent.renderBox(
        position: BlockPos,
        color: Int,
    ) {
        renderBox(AABB(position), color)
    }

    fun RenderWorldEvent.renderBox(
        position: AABB,
        color: Int,
    ) {
        atCamera {
            ShapeRenderer.addChainedFilledBoxVertices(
                poseStack,
                buffer.getBuffer(BLOCK_FILL_TRIANGLE_THROUGH_WALLS),
                position.minX - 0.005, position.minY - 0.005, position.minZ - 0.005,
                position.maxX + 0.005, position.maxY + 0.005, position.maxZ + 0.005,
                ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color).coerceAtMost(0.6f),
            )
        }
    }

    fun RenderWorldEvent.renderLineFromCursor(pos: Vec3, color: Int, width: Float = 5f) {
        render3dLine(camera.position.add(Vec3.directionFromRotation(camera.xRot, camera.yRot)), pos, color, width)
    }

    fun RenderWorldEvent.render3dLine(start: Vec3, end: Vec3, color: Int, width: Float = 5f) {
        atCamera {
            RenderSystem.lineWidth(width)

            val entry = poseStack.last()
            val buffer = buffer.getBuffer(RenderType.lineStrip())
            val normal = end.toVector3f().sub(start.toVector3f()).normalize()
            buffer.addVertex(entry, start.toVector3f()).setColor(color).setNormal(entry, normal)
            buffer.addVertex(entry, end.toVector3f()).setColor(color).setNormal(entry, normal)

            RenderSystem.lineWidth(1f)
        }
    }

    fun RenderWorldEvent.renderBeaconBeam(position: Vec3, color: Int) {
        atCamera {
            translate(position)
            BeaconRenderer.renderBeaconBeam(
                poseStack, buffer, BeaconRenderer.BEAM_LOCATION,
                0f, Mth.PI, McLevel.self.gameTime, 0, McLevel.self.maxY * 2,
                ARGB.opaque(color), 0.2f, 0.25f,
            )
        }
    }

}
