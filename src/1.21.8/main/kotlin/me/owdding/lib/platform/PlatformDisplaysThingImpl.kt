package me.owdding.lib.platform

import com.mojang.blaze3d.vertex.PoseStack
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays.isMouseOver
import me.owdding.lib.displays.entity.EntityStateRenderer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.joml.Quaternionf
import org.joml.Vector3f
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.*
import tech.thatgravyboat.skyblockapi.utils.extentions.scaled
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.math.atan

actual object PlatformDisplays {

    actual fun entity(
        entity: LivingEntity,
        width: Int,
        height: Int,
        scale: Int,
        mouseX: Float,
        mouseY: Float,
        spinning: Boolean,
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
                val positionOffset = Vector3f(0.0f, (-centerY / scaledSize) + entity.boundingBox.ysize.toFloat() / 2f, 0.0f)

                EntityStateRenderer.draw(graphics, entity, width, height, scaledSize, positionOffset, baseRotation, tiltRotation)

                entity.yBodyRot = originalBodyRotation
                entity.yRot = originalYRotation
                entity.xRot = originalXRotation
                entity.yHeadRotO = originalHeadRotationPrev
                entity.yHeadRot = originalHeadRotation
            }
        }
    }

    actual fun item(
        item: ItemStack,
        width: Int,
        height: Int,
        showTooltip: Boolean,
        showStackSize: Boolean,
        customStackText: Any?,
    ): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height

            override fun render(graphics: GuiGraphics) {
                if (showTooltip && !item.isEmpty) {
                    val player = McPlayer.self
                    if (isMouseOver(this, graphics) && player != null) {
                        graphics.showTooltip(
                            Text.multiline(
                                item.getTooltipLines(
                                    Item.TooltipContext.of(McLevel.self),
                                    player,
                                    TooltipFlag.NORMAL,
                                ),
                            ),
                            1000,
                        )
                    }
                }

                graphics.pushPop {
                    graphics.scale(width / 16f, height / 16f)
                    graphics.renderItem(item, 0, 0)

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

    actual fun pushPop(display: Display, operations: PoseStack.() -> Unit): Display {
        return object : Display {
            // Does not account for scaling
            override fun getWidth() = display.getWidth()
            override fun getHeight() = display.getHeight()
            override fun render(graphics: GuiGraphics) {
                graphics.pushPop {
                    display.render(graphics)
                }
            }
        }
    }
}
