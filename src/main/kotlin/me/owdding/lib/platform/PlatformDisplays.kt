package me.owdding.lib.platform

import com.mojang.blaze3d.vertex.PoseStack
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays.isMouseOver
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import org.joml.RoundingMode
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector3f
import org.joml.component1
import org.joml.component2
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.*
import tech.thatgravyboat.skyblockapi.utils.extentions.scaled
import tech.thatgravyboat.skyblockapi.utils.text.Text
import java.lang.Math
import java.lang.System
import kotlin.math.atan

//? if > 1.21.5 {
import me.owdding.lib.displays.entity.EntityStateRenderer
import me.owdding.lib.displays.item.ItemStateRenderer

//?}
//?} else
/*import net.minecraft.client.gui.screens.inventory.InventoryScreen*/

internal object PlatformDisplays {
    fun entity(
        entity: LivingEntity,
        width: Int,
        height: Int,
        scale: Int,
        mouseX: Float = Float.NaN,
        mouseY: Float = Float.NaN,
        spinning: Boolean = false,
    ): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {
                val centerX = width / 2f
                val centerY = height / 2f
                val eyesX = mouseX.takeIf { !it.isNaN() } ?: centerX
                val eyesY = mouseY.takeIf { !it.isNaN() } ?: centerY

                val rotationX = atan((centerX - eyesX) / 40.0).toFloat()
                val rotationY = atan((centerY - eyesY) / 40.0).toFloat()
                val baseRotation = Quaternionf().rotateZ(Math.PI.toFloat())
                val tiltRotation = Quaternionf().rotateX(rotationY * 20.0f * (Math.PI.toFloat() / 180f))

                if (spinning) {
                    val currentTime = System.currentTimeMillis() % 3600
                    val spinAngle = (currentTime / 10.0) % 360.0
                    baseRotation.mul(Quaternionf().rotateY(Math.toRadians(spinAngle).toFloat()))
                }

                baseRotation.mul(tiltRotation)
                val originalBodyRotation = entity.yBodyRot
                val originalYRotation = entity.yRot
                val originalXRotation = entity.xRot
                val originalHeadRotationPrev = entity.yHeadRotO
                val originalHeadRotation = entity.yHeadRot
                entity.yBodyRot = 180.0f + rotationX * 20.0f
                entity.yRot = 180.0f + rotationX * 40.0f
                entity.xRot = -rotationY * 20.0f
                entity.yHeadRot = entity.yRot
                entity.yHeadRotO = entity.yRot
                val entityScale = entity.scale
                val scaledSize = scale / entityScale

                //? if > 1.21.5 {
                val positionOffset = Vector3f(0.0f, (-centerY / scaledSize) + entity.boundingBox.ysize.toFloat() / 2f, 0.0f)
                EntityStateRenderer.draw(graphics, entity, width, height, scaledSize, positionOffset, baseRotation, tiltRotation)
                //?} else {
                /*val positionOffset = Vector3f(0.0f, entity.bbHeight / 2.0f * entityScale, 0.0f)
                InventoryScreen.renderEntityInInventory(
                    graphics,
                    centerX,
                    centerY,
                    scaledSize,
                    positionOffset,
                    baseRotation,
                    tiltRotation,
                    entity,
                )
                *///?}

                entity.yBodyRot = originalBodyRotation
                entity.yRot = originalYRotation
                entity.xRot = originalXRotation
                entity.yHeadRotO = originalHeadRotationPrev
                entity.yHeadRot = originalHeadRotation
            }
        }
    }

    fun item(
        item: ItemStack,
        width: Int = 16,
        height: Int = 16,
        showTooltip: Boolean = false,
        showStackSize: Boolean = false,
        customStackText: Any? = null,
    ): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height

            override fun render(graphics: GuiGraphics) {
                //? if 1.21.5 {
                /*val x = graphics.pose().last().pose().m30().toInt()
                val y = graphics.pose().last().pose().m31().toInt()
                *///?} else
                val (x, y) = Vector2i(graphics.pose().transformPosition(Vector2f(0f, 0f)), RoundingMode.TRUNCATE)
                if (
                    !graphics.containsPointInScissor(x, y) && !graphics.containsPointInScissor(x + width, y) &&
                    !graphics.containsPointInScissor(x + width, y + height) && !graphics.containsPointInScissor(x, y + height)
                ) return

                if (showTooltip && !item.isEmpty) {
                    val player = McPlayer.self
                    if (isMouseOver(this, graphics) && player != null) {
                        //? > 1.21.5 {
                        graphics.setTooltipForNextFrame(McFont.self, item, McClient.mouse.first.toInt(), McClient.mouse.second.toInt())
                        //?} else
                        /*graphics.renderTooltip(McFont.self, item, McClient.mouse.first.toInt(), McClient.mouse.second.toInt())*/
                    }
                }

                graphics.pushPop {
                    graphics.scale(width / 16f, height / 16f)

                    //? if > 1.21.5 {
                    val scale = graphics.getScale()
                    if (scale.x > 1f || scale.y > 1f) {
                        ItemStateRenderer.draw(graphics, item, 0, 0)
                    } else {
                        graphics.renderItem(item, 0, 0)
                    }
                    //?} else
                    /*graphics.renderItem(item, 0, 0)*/

                    val stackSize = item.count
                    if ((showStackSize && stackSize > 1) || customStackText != null) {
                        val component = when (customStackText) {
                            null -> Text.of(stackSize.toString())
                            is Component -> customStackText
                            is String -> Text.of(customStackText)
                            else -> Text.of(customStackText.toString())
                        }

                        val scale = (width.toFloat() / McFont.width(component)).coerceAtMost(1f)

                        graphics.translate(1 + width - McFont.width(component) * scale, 2 + height - McFont.height * scale)
                        graphics.scaled(scale, scale) {
                            graphics.drawString(
                                component,
                                0,
                                0,
                                -1,
                                true,
                            )
                        }
                    }
                }
            }
        }
    }

    fun pushPop(display: Display, operations: PoseStack.() -> Unit): Display {
        return object : Display {
            // Does not account for scaling
            override fun getWidth() = display.getWidth()
            override fun getHeight() = display.getHeight()
            override fun render(graphics: GuiGraphics) {
                graphics.pushPop {
                    //? if 1.21.5
                    /*operations(graphics.pose())*/
                    display.render(graphics)
                }
            }
        }
    }
}
