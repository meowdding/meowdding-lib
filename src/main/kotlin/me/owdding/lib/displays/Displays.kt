package me.owdding.lib.displays

import com.mojang.blaze3d.vertex.PoseStack
import earth.terrarium.olympus.client.images.BuiltinImageProviders
import me.owdding.lib.extensions.floor
import me.owdding.lib.layouts.ScalableWidget
import me.owdding.lib.platform.PlatformDisplays
import net.minecraft.Util
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceRenderer
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.msrandom.stub.Stub
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.platform.*
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.extentions.translated
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import java.net.URI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin


private const val NO_SPLIT = -1

@Stub
internal expect fun roundedTextureDisplay(width: Int, height: Int, texture: ResourceLocation): Display

object Displays {

    private var showTooltips = true

    fun empty(width: Int = 0, height: Int = 0): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {}
        }
    }

    fun supplied(display: () -> Display): Display {
        return object : Display {
            override fun getWidth() = display().getWidth()
            override fun getHeight() = display().getHeight()
            override fun render(graphics: GuiGraphics) {
                display().render(graphics)
            }
        }
    }

    fun fixed(width: Int, height: Int, display: Display): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {
                graphics.scissor(0, 0, width, height) {
                    display.render(graphics)
                }
            }
        }
    }

    fun background(color: UInt, display: Display): Display {
        return object : Display {
            override fun getWidth() = display.getWidth()
            override fun getHeight() = display.getHeight()
            override fun render(graphics: GuiGraphics) {
                graphics.fill(
                    0, 0,
                    getWidth(), getHeight(),
                    color.toInt(),
                )
                display.render(graphics)
            }
        }
    }

    fun background(sprite: ResourceLocation, display: Display, color: Int = -1): Display {
        return object : Display {
            override fun getWidth() = display.getWidth()
            override fun getHeight() = display.getHeight()
            override fun render(graphics: GuiGraphics) {
                graphics.drawSprite(
                    sprite,
                    0,
                    0,
                    display.getWidth(),
                    display.getHeight(),
                    color.and(0xFFFFFF).or(0xFF000000u.toInt()),
                )
                display.render(graphics)
            }
        }
    }

    fun image(uri: URI, display: Display): Display {
        return object : Display {
            override fun getWidth() = display.getWidth()
            override fun getHeight() = display.getHeight()
            override fun render(graphics: GuiGraphics) {
                graphics.drawTexture(BuiltinImageProviders.URL.get(uri), 0, 0, display.getWidth(), display.getHeight())
                display.render(graphics)
            }
        }
    }

    fun background(sprite: ResourceLocation, width: Int, height: Int): Display {
        return background(sprite, empty(width, height))
    }

    fun padding(padding: Int, display: Display): Display {
        return padding(padding, padding, display)
    }

    fun padding(padX: Int, padY: Int, display: Display): Display {
        return padding(padX, padX, padY, padY, display)
    }

    fun padding(left: Int, right: Int, top: Int, bottom: Int, display: Display): Display {
        return object : Display {
            override fun getWidth() = left + display.getWidth() + right
            override fun getHeight() = top + display.getHeight() + bottom
            override fun render(graphics: GuiGraphics) {
                display.render(graphics, left, top)
            }
        }
    }

    fun center(width: Int = -1, height: Int = -1, display: Display): Display {
        return object : Display {
            override fun getWidth() = if (width == -1) display.getWidth() else width
            override fun getHeight() = if (height == -1) display.getHeight() else height
            override fun render(graphics: GuiGraphics) {
                display.render(graphics, (getWidth() - display.getWidth()) / 2, (getHeight() - display.getHeight()) / 2)
            }
        }
    }

    fun outline(color: () -> UInt, display: Display): Display {
        return object : Display {
            override fun getWidth() = display.getWidth() + 2
            override fun getHeight() = display.getHeight() + 2
            override fun render(graphics: GuiGraphics) {
                display.render(graphics, 1, 1)
                graphics.renderOutline(0, 0, getWidth(), getHeight(), color().toInt())
            }
        }
    }

    fun face(texture: () -> ResourceLocation, size: Int = 8): Display {
        return object : Display {
            override fun getWidth(): Int = size
            override fun getHeight(): Int = size

            override fun render(graphics: GuiGraphics) {
                PlayerFaceRenderer.draw(graphics, texture(), 0, 0, 8, true, false, -1)
            }
        }
    }

    fun sprite(sprite: ResourceLocation, width: Int, height: Int): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {
                graphics.drawSprite(sprite, 0, 0, width, height, -1)
            }
        }
    }

    fun text(text: String, color: () -> UInt = { 0xFFFFFFFFu }, shadow: Boolean = true): Display {
        return text({ text }, color, shadow)
    }

    fun text(text: () -> String, color: () -> UInt = { 0xFFFFFFFFu }, shadow: Boolean = true): Display {
        return object : Display {

            val component: MutableComponent
                get() = Text.of(text())

            override fun getWidth() = component.width
            override fun getHeight() = McFont.height
            override fun render(graphics: GuiGraphics) {
                graphics.drawString(component, 0, 0, color().toInt(), shadow)
            }
        }
    }

    fun component(component: () -> Component, color: () -> UInt = { 0xFFFFFFFFu }, shadow: Boolean = true): Display {
        return object : Display {
            override fun getWidth() = component().width
            override fun getHeight() = McFont.height
            override fun render(graphics: GuiGraphics) {
                graphics.drawString(component(), 0, 0, color().toInt(), shadow)
            }
        }
    }

    fun component(
        component: Component,
        maxWidth: Int = NO_SPLIT,
        color: () -> UInt = { 0xFFFFFFFFu },
        shadow: Boolean = true,
    ): Display {
        val lines = if (maxWidth == NO_SPLIT) listOf(component.visualOrderText) else McFont.split(component, maxWidth)
        val width = lines.maxOfOrNull(McFont::width) ?: 0
        val height = lines.size * McFont.height

        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {
                lines.forEachIndexed { index, line ->
                    graphics.drawString(line, 0, index * McFont.height, color().toInt(), shadow)
                }
            }
        }
    }

    fun text(
        sequence: FormattedCharSequence,
        color: () -> UInt = { 0xFFFFFFFFu },
        shadow: Boolean = true,
    ): Display {
        return object : Display {
            override fun getWidth() = McFont.width(sequence)
            override fun getHeight() = McFont.height
            override fun render(graphics: GuiGraphics) {
                graphics.drawString(sequence, 0, 0, color().toInt(), shadow)
            }
        }
    }

    fun text(
        text: FormattedText,
        color: () -> UInt = { 0xFFFFFFFFu },
        shadow: Boolean = true,
    ) = text(Language.getInstance().getVisualOrder(text), color, shadow)

    fun wrappedText(
        text: FormattedText,
        maxWidth: Int,
        color: () -> UInt = { 0xFFFFFFFFu },
        shadow: Boolean = true,
    ): Display {
        val lines = McFont.split(text, maxWidth)
        return object : Display {
            override fun getWidth() = maxWidth
            override fun getHeight() = lines.size * McFont.height
            override fun render(graphics: GuiGraphics) {
                lines.forEachIndexed { index, line ->
                    graphics.drawString(line, 0, index * McFont.height, color().toInt(), shadow)
                }
            }
        }
    }

    fun row(
        vararg displays: Display,
        spacing: Int = 0,
        alignment: Alignment = Alignment.START,
    ): Display {
        return object : Display {
            override fun getWidth() = displays.sumOf { it.getWidth() } + spacing * (displays.size - 1)
            override fun getHeight() = displays.maxOfOrNull { it.getHeight() } ?: 0
            override fun render(graphics: GuiGraphics) {
                val maxHeight = getHeight()
                var currentX = 0

                displays.forEachIndexed { index, display ->
                    val yOffset = when (alignment) {
                        Alignment.START -> 0
                        Alignment.CENTER -> (maxHeight - display.getHeight()) / 2
                        Alignment.END -> maxHeight - display.getHeight()
                    }

                    graphics.translated(currentX, yOffset) {
                        display.render(graphics)
                        currentX += display.getWidth() + spacing
                    }
                }
            }
        }
    }

    fun column(
        vararg displays: Display,
        spacing: Int = 0,
        alignment: Alignment = Alignment.START,
    ): Display {
        return object : Display {
            override fun getWidth() = displays.maxOfOrNull { it.getWidth() } ?: 0
            override fun getHeight() = displays.sumOf { it.getHeight() } + spacing * (displays.size - 1)

            override fun render(graphics: GuiGraphics) {
                val maxWidth = getWidth()
                var currentY = 0

                displays.forEach { display ->
                    val xOffset = when (alignment) {
                        Alignment.START -> 0
                        Alignment.CENTER -> (maxWidth - display.getWidth()) / 2
                        Alignment.END -> maxWidth - display.getWidth()
                    }
                    graphics.translated(xOffset, currentY) {
                        display.render(graphics)
                        currentY += display.getHeight() + spacing
                    }
                }
            }
        }
    }

    fun item(
        item: ItemLike,
        width: Int = 16,
        height: Int = 16,
        showTooltip: Boolean = false,
        showStackSize: Boolean = false,
        customStackText: Any? = null,
    ): Display {
        return item(item.asItem().defaultInstance, width, height, showTooltip, showStackSize, customStackText)
    }

    fun item(
        item: ItemStack,
        width: Int = 16,
        height: Int = 16,
        showTooltip: Boolean = false,
        showStackSize: Boolean = false,
        customStackText: Any? = null,
    ): Display {
        return PlatformDisplays.item(item, width, height, showTooltip, showStackSize, customStackText)
    }

    fun <T> renderable(renderable: T, width: Int = -1, height: Int = -1): Display
        where T : Renderable, T : LayoutElement {
        return object : Display {
            override fun getWidth(): Int = if (width == -1) renderable.width else width
            override fun getHeight(): Int = if (height == -1) renderable.height else height
            override fun render(graphics: GuiGraphics) {
                renderable.render(graphics, -1, -1, 0f)
            }
        }
    }

    fun layered(vararg displays: Display): Display {
        return object : Display {
            override fun getWidth() = displays.maxOfOrNull { it.getWidth() } ?: 0
            override fun getHeight() = displays.maxOfOrNull { it.getHeight() } ?: 0
            override fun render(graphics: GuiGraphics) {
                displays.forEach { it.render(graphics) }
            }
        }
    }

    @Deprecated("Use of pushPop no longer works when using multi version")
    fun pushPop(display: Display, operations: PoseStack.() -> Unit): Display {
        return PlatformDisplays.pushPop(display, operations)
    }

    fun entity(
        entity: LivingEntity,
        width: Int,
        height: Int,
        scale: Int,
        mouseX: Float = Float.NaN,
        mouseY: Float = Float.NaN,
        spinning: Boolean = false,
    ): Display {
        return PlatformDisplays.entity(entity, width, height, scale, mouseX, mouseY, spinning)
    }

    fun table(
        table: List<List<Display>>,
        spacing: Int = 0,
    ): Display {
        return object : Display {
            val columnWidths = (0 until table.maxOf { it.size }).map { col ->
                table.maxOfOrNull { row -> row.getOrNull(col)?.getWidth() ?: 0 } ?: 0
            }

            override fun getHeight(): Int = table.sumOf { it.maxOf { it.getHeight() } } + (table.size - 1) * spacing
            override fun getWidth(): Int = columnWidths.sum() + (columnWidths.size - 1) * spacing

            override fun render(graphics: GuiGraphics) {
                var currentY = 0

                table.forEach { row ->
                    val rowHeight = row.maxOf { it.getHeight() }
                    var currentX = 0

                    graphics.translated(0, currentY) {
                        row.forEachIndexed { col, element ->
                            graphics.translated(currentX) {
                                element.render(graphics)
                            }
                            currentX += columnWidths[col] + spacing
                        }
                    }
                    currentY += rowHeight + spacing
                }
            }
        }
    }

    fun tooltip(display: Display, component: Component): Display {
        return object : Display {
            override fun getWidth() = display.getWidth()
            override fun getHeight() = display.getHeight()
            override fun render(graphics: GuiGraphics) {
                display.render(graphics)

                if (isMouseOver(display, graphics)) {
                    graphics.showTooltip(component)
                }
            }
        }
    }

    fun fixedWidth(original: Display, maxWidth: Int? = null): Display {
        return object : Display {
            override fun getWidth() = maxWidth ?: original.getWidth()
            override fun getHeight() = original.getHeight()

            override fun render(graphics: GuiGraphics) {
                graphics.pushPop {
                    val seconds = Util.getMillis().toDouble() / 1000.0
                    graphics.scissor(0, 0, getWidth(), getHeight()) {
                        if (maxWidth != null && maxWidth < original.getWidth()) {
                            val overhang: Int = original.getWidth() - maxWidth
                            val e = max(overhang.toDouble() * 0.5, 3.0)
                            val f = sin(Mth.HALF_PI * cos(Mth.TWO_PI * seconds / e)) / 2.0 + 0.5
                            val g = Mth.lerp(f, 0.0, overhang.toDouble())
                            graphics.translate(-g, 0)
                        }
                        original.render(graphics)
                    }
                }
            }
        }
    }

    fun disableTooltips(action: () -> Unit) {
        showTooltips = false
        action()
        showTooltips = true
    }

    fun isMouseOver(display: Display, graphics: GuiGraphics): Boolean {
        val translation = graphics.getTranslation()
        val (mouseX, mouseY) = McClient.mouse
        val xRange = translation.x..(translation.x + (display.getWidth() * ScalableWidget.getCurrentScale()).floor())
        val yRange = translation.y..(translation.y + (display.getHeight() * ScalableWidget.getCurrentScale()).floor())
        return mouseX in xRange && mouseY in yRange && graphics.containsPointInScissor(
            mouseX.toInt(),
            mouseY.toInt(),
        ) && showTooltips
    }

    fun circleTexture(width: Int, height: Int, resourceLocation: ResourceLocation) = roundedTextureDisplay(width, height, resourceLocation)
}
