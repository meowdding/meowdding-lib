package me.owdding.lib.displays

import com.mojang.blaze3d.platform.InputConstants
import earth.terrarium.olympus.client.components.buttons.Button
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.builders.TooltipBuilder
import tech.thatgravyboat.skyblockapi.utils.text.Text

public fun List<Any>.toColumn(spacing: Int = 0, alignment: Alignment = Alignment.START): Display {
    return Displays.column(
        *this.map {
            when (it) {
                is String -> Displays.text(it)
                is Component -> Displays.text(it)
                is Display -> it
                else -> throw IllegalArgumentException("Unsupported type: ${it::class.simpleName}")
            }
        }.toTypedArray(),
        spacing = spacing,
        alignment = alignment,
    )
}

public fun List<Any>.toRow(spacing: Int = 0, alignment: Alignment = Alignment.START): Display {
    return Displays.row(
        *this.map {
            when (it) {
                is String -> Displays.text(it)
                is Component -> Displays.text(it)
                is Display -> it
                else -> throw IllegalArgumentException("Unsupported type: ${it::class.simpleName}")
            }
        }.toTypedArray(),
        spacing = spacing,
        alignment = alignment,
    )
}

public fun List<Any>.asLayer(): Display {
    return Displays.layered(
        *this.map {
            when (it) {
                is String -> Displays.text(it)
                is Component -> Displays.text(it)
                is Display -> it
                else -> throw IllegalArgumentException("Unsupported type: ${it::class.simpleName}")
            }
        }.toTypedArray(),
    )
}

public fun List<List<Any>>.asTable(spacing: Int = 0): Display =
    Displays.table(
        this.map { list ->
            list.map {
                when (it) {
                    is Display -> it
                    is Identifier -> Displays.sprite(it, 12, 12)
                    else -> Displays.text(it.toString(), color = { 0x555555u }, shadow = false)
                }
            }
        },
        spacing,
    )

public fun Display.centerIn(width: Int, height: Int): Display = Displays.center(width, height, this)

public fun Display.withBackground(color: UInt): Display = Displays.background(color, this)

public fun Display.asWidget(): DisplayWidget = DisplayWidget(this)

public fun Display.withTooltip(builder: TooltipBuilder.() -> Unit): Display = TooltipBuilder().apply(builder).takeUnless { it.isEmpty() }
    ?.let { Displays.tooltip(this, it.build()) } ?: this

public fun Display.withTooltip(vararg tooltip: Any?): Display = Displays.tooltip(this, Text.multiline(*tooltip))

@Deprecated("Use asButtonLeft instead", ReplaceWith("asButtonLeft(action)"))
public fun Display.asButton(action: (Button) -> Unit): Button = asButton(leftClick = action)

public fun Display.asButtonLeft(action: (Button) -> Unit): Button = asButton(leftClick = action)

public fun Display.asButtonRight(action: (Button) -> Unit): Button = asButton(rightClick = action)

public fun Display.asButton(leftClick: (Button) -> Unit = {}, rightClick: (Button) -> Unit = {}): Button = Button().apply {
    val display = this@asButton

    withTexture(null)
    withRenderer(DisplayWidget.displayRenderer(display))
    setSize(display.getWidth(), display.getHeight())
    withCallback(InputConstants.MOUSE_BUTTON_LEFT) { leftClick(this) }
    withCallback(InputConstants.MOUSE_BUTTON_RIGHT) { rightClick(this) }
}

public fun Display.withPadding(padding: Int = 0, left: Int? = null, right: Int? = null, top: Int? = null, bottom: Int? = null): Display =
    Displays.padding(left ?: padding, right ?: padding, top ?: padding, bottom ?: padding, this)

public fun Display.withOutline(color: UInt = 0xFF000000u): Display = Displays.outline({ color }, this)

public fun Display.withIconographicCompat(item: ItemStack): Display = Displays.iconographicCompat(this, item)
