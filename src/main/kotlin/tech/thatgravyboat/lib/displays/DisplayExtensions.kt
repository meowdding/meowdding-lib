package tech.thatgravyboat.lib.displays

import earth.terrarium.olympus.client.components.buttons.Button
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.lib.builder.TooltipBuilder
import tech.thatgravyboat.skyblockapi.utils.text.Text

fun List<Any>.toColumn(spacing: Int = 0, alignment: Alignment = Alignment.START): Display {
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

fun List<Any>.toRow(spacing: Int = 0, alignment: Alignment = Alignment.START): Display {
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

fun List<Any>.asLayer(): Display {
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

fun List<List<Any>>.asTable(spacing: Int = 0): Display =
    Displays.table(
        this.map {
            it.map {
                when (it) {
                    is Display -> it
                    is ResourceLocation -> Displays.sprite(it, 12, 12)
                    else -> Displays.text(it.toString(), color = { 0x555555u }, shadow = false)
                }
            }
        },
        spacing,
    )

fun Display.centerIn(width: Int, height: Int): Display = Displays.center(width, height, this)

fun Display.withBackground(color: UInt): Display = Displays.background(color, this)

fun Display.asWidget(): DisplayWidget = DisplayWidget(this)

fun Display.withTooltip(builder: TooltipBuilder.() -> Unit): Display = TooltipBuilder().apply(builder).takeUnless { it.isEmpty() }
    ?.let { Displays.tooltip(this, it.build()) } ?: this

fun Display.withTooltip(vararg tooltip: Any?): Display = Displays.tooltip(this, Text.multiline(*tooltip))
/*fun Display.withTranslatedTooltip(key: String, vararg args: Any?): Display {
    var raw = I18nAccessor.getLanguage().getOrDefault(key)
    args.forEachIndexed { index, any ->
        raw = raw.replace("<$index>", any.toString())
    }

    val text = TagParser.QUICK_TEXT_SAFE.parseText(raw, ParserContext.of())
    return Displays.tooltip(this, text)
}*/

fun Display.asButton(action: (Button) -> Unit): Button {
    val button = Button()
    button.withTexture(null)
    button.withRenderer(DisplayWidget.displayRenderer(this))
    button.setSize(this.getWidth(), this.getHeight())
    button.withCallback { action(button) }

    return button
}

fun Display.withPadding(padding: Int = 0, left: Int? = null, right: Int? = null, top: Int? = null, bottom: Int? = null): Display =
    Displays.padding(left ?: padding, right ?: padding, top ?: padding, bottom ?: padding, this)
