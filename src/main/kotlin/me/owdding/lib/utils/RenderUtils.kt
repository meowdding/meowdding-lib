package me.owdding.lib.utils

//? < 1.21.11
/*import com.mojang.blaze3d.systems.RenderSystem*/

import me.owdding.lib.rendering.world.RenderTypes.BLOCK_FILL_TRIANGLE_THROUGH_WALLS
import me.owdding.lib.rendering.world.RenderTypes.NO_DEPTH_LINES
import net.minecraft.client.CameraType
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.ShapeRenderer
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.Shapes
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.math.max

@Suppress("unused")
enum class OutlineType(val outline: Boolean = false, val fill: Boolean = false) {
    OUTLINE(outline = true),
    FILL(fill = true),
    FILL_OUTLINE(outline = true, fill = true),
}

object RenderUtils {

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
        val x = cameraPosition.x
        val y = cameraPosition.y
        val z = cameraPosition.z

        val scale = max((cameraPosition.distanceTo(position).toFloat() / 10).toDouble(), 1.0).toFloat() * 0.033f

        poseStack.pushPop {
            poseStack.translate(position.x - x + 0.5, position.y - y + 1.07f, position.z - z + 0.5)
            poseStack.translate(0f, yOffset * -scale, 0f)
            poseStack.mulPose(cameraRotation)
            poseStack.scale(scale, -scale, scale)
            val xOffset = if (center) -McFont.width(text) / 2.0f else 0.0f

            drawString(
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

    internal fun RenderWorldEvent.renderBox(aabb: AABB, color: Int, fillMaxAlpha: Int) {
        val fillColor = ARGB.color(ARGB.alpha(color).coerceAtMost(fillMaxAlpha), color)

        val last = poseStack.last()
        val consumer = buffer.getBuffer(me.owdding.lib.rendering.world.RenderTypes.BLOCK_FILL_QUAD)
        val minX = aabb.minX.toFloat();
        val minY = aabb.minY.toFloat();
        val minZ = aabb.minZ.toFloat();
        val maxX = aabb.maxX.toFloat();
        val maxY = aabb.maxY.toFloat();
        val maxZ = aabb.maxZ.toFloat();

        fun Int.alpha() = ARGB.color(255, this)

        consumer.addVertex(last, minX, minY, minZ).setColor(0xFF0000.alpha())
        consumer.addVertex(last, maxX, minY, minZ).setColor(0xFF0000.alpha())
        consumer.addVertex(last, maxX, minY, maxZ).setColor(0xFF0000.alpha())
        consumer.addVertex(last, minX, minY, maxZ).setColor(0xFF0000.alpha())

        consumer.addVertex(last, minX, maxY, minZ).setColor(0x00FF00.alpha())
        consumer.addVertex(last, maxX, maxY, minZ).setColor(0x00FF00.alpha())
        consumer.addVertex(last, maxX, minY, minZ).setColor(0x00FF00.alpha())
        consumer.addVertex(last, minX, minY, minZ).setColor(0x00FF00.alpha())

        consumer.addVertex(last, minX, minY, minZ).setColor(0x0000FF.alpha())
        consumer.addVertex(last, minX, minY, maxZ).setColor(0x0000FF.alpha())
        consumer.addVertex(last, minX, maxY, maxZ).setColor(0x0000FF.alpha())
        consumer.addVertex(last, minX, maxY, minZ).setColor(0x0000FF.alpha())


        consumer.addVertex(last, maxX, maxY, minZ).setColor(0x00FFFF.alpha())
        consumer.addVertex(last, maxX, maxY, maxZ).setColor(0x00FFFF.alpha())
        consumer.addVertex(last, maxX, minY, maxZ).setColor(0x00FFFF.alpha())
        consumer.addVertex(last, maxX, minY, minZ).setColor(0x00FFFF.alpha())

        consumer.addVertex(last, minX, minY, maxZ).setColor(0xFFFF00.alpha())
        consumer.addVertex(last, maxX, minY, maxZ).setColor(0xFFFF00.alpha())
        consumer.addVertex(last, maxX, maxY, maxZ).setColor(0xFFFF00.alpha())
        consumer.addVertex(last, minX, maxY, maxZ).setColor(0xFFFF00.alpha())

        consumer.addVertex(last, maxX, maxY, maxZ).setColor(0xFF00FF.alpha())
        consumer.addVertex(last, maxX, maxY, minZ).setColor(0xFF00FF.alpha())
        consumer.addVertex(last, minX, maxY, minZ).setColor(0xFF00FF.alpha())
        consumer.addVertex(last, minX, maxY, maxZ).setColor(0xFF00FF.alpha())
    }

    fun RenderWorldEvent.renderBox(
        aabb: AABB,
        outlineType: OutlineType,
        color: Int,
        lineWidth: Float = 1f,
        fillMaxAlpha: Int = 100,
    ) {
        atCamera {
            val shape = Shapes.create(
                aabb.minX - 0.005, aabb.minY - 0.005, aabb.minZ - 0.005,
                aabb.maxX + 0.005, aabb.maxY + 0.005, aabb.maxZ + 0.005,
            )
            if (outlineType.fill) {
                renderBox(aabb, color, fillMaxAlpha)
            }
            if (outlineType.outline) {
                ShapeRenderer.renderShape(
                    poseStack,
                    buffer.getBuffer(NO_DEPTH_LINES),
                    shape,
                    0.0, 0.0, 0.0, color, /*? if > 1.21.10 {*/ lineWidth, /*?}*/
                )
            }
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
            //? > 1.21.10 {
            ShapeRenderer.renderShape(
                poseStack,
                buffer.getBuffer(BLOCK_FILL_TRIANGLE_THROUGH_WALLS),
                Shapes.create(
                    position.minX - 0.005, position.minY - 0.005, position.minZ - 0.005,
                    position.maxX + 0.005, position.maxY + 0.005, position.maxZ + 0.005,
                ),
                0.0, 0.0, 0.0, color, 1f,
            )
            //?} else {
            /*ShapeRenderer.addChainedFilledBoxVertices(
                poseStack,
                buffer.getBuffer(BLOCK_FILL_TRIANGLE_THROUGH_WALLS),
                position.minX - 0.005, position.minY - 0.005, position.minZ - 0.005,
                position.maxX + 0.005, position.maxY + 0.005, position.maxZ + 0.005,
                ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color).coerceAtMost(0.6f),
            )
            *///?}
        }
    }

    fun RenderWorldEvent.renderLineFromCursor(pos: Vec3, color: Int, width: Float = 5f) {
        val cameraEntity = McClient.self.cameraEntity ?: return
        val vec = Vec3.directionFromRotation(cameraEntity.xRot, cameraEntity.yRot).let {
            if (McClient.options.cameraType == CameraType.THIRD_PERSON_FRONT) it.reverse() else it
        }
        render3dLine(cameraPosition.add(vec), pos, color, width)
    }

    fun RenderWorldEvent.render3dLine(start: Vec3, end: Vec3, color: Int, width: Float = 5f) {
        atCamera {
            //? < 1.21.11
            /*RenderSystem.lineWidth(width)*/

            val entry = poseStack.last()
            val buffer = buffer.getBuffer(RenderTypes.lines())
            val normal = end.toVector3f().sub(start.toVector3f()).normalize()
            buffer.addVertex(entry, start.toVector3f()).setColor(color).setNormal(entry, normal)
            buffer.addVertex(entry, end.toVector3f()).setColor(color).setNormal(entry, normal)

            //? < 1.21.11
            /*RenderSystem.lineWidth(1f)*/
        }
    }

    fun RenderWorldEvent.renderBeaconBeam(position: Vec3, color: Int) {
        renderBeaconBeam(
            poseStack, position, buffer, BeaconRenderer.BEAM_LOCATION,
            0f, Mth.PI, McLevel.self.gameTime, 0, McLevel.self.maxY * 2,
            ARGB.opaque(color), 0.2f, 0.25f,
        )
    }

}
