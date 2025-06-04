package me.owdding.lib.builder

import me.owdding.lib.displays.*
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.text.Text

object DisplayFactory {
    fun vertical(spacing: Int = 0, alignment: Alignment = Alignment.START, builder: DisplayBuilder.() -> Unit): Display {
        val builder = VerticalDisplayBuilder()
        builder.builder()
        return builder.build(spacing, alignment)
    }

    fun horizontal(spacing: Int = 0, alignment: Alignment = Alignment.START, builder: DisplayBuilder.() -> Unit): Display {
        val builder = HorizontalDisplayBuilder()
        builder.builder()
        return builder.build(spacing, alignment)
    }
}

abstract class DisplayBuilder {
    protected val displays = mutableListOf<Display>()

    fun string(text: String) {
        displays.add(Displays.text(text))
    }

    fun string(component: Component) {
        displays.add(Displays.text(component))
    }

    fun display(display: Display) {
        displays.add(display)
    }

    fun string(text: String, init: MutableComponent.() -> Unit) {
        string(Text.of(text, init))
    }

    fun spacer(width: Int = 0, height: Int = 0) {
        displays.add(Displays.empty(width, height))
    }

    fun supplied(supplier: () -> Display) {
        displays.add(Displays.supplied(supplier))
    }

    fun textDisplay(
        text: String = "",
        color: UInt = 0x555555u,
        shadow: Boolean = false,
        displayModifier: Display.() -> Display = { this },
        init: MutableComponent.() -> Unit,
    ) {
        Displays.text(Text.of(text, init), { color }, shadow = shadow).displayModifier().let { display(it) }
    }

    fun textDisplay(text: String = "", color: UInt = 0x555555u, shadow: Boolean = false, init: MutableComponent.() -> Unit) {
        textDisplay(text, color, shadow, { this }, init)
    }

    fun wrappedText(text: String, width: Int, color: UInt = 0x555555u, shadow: Boolean = false) {
        displays.add(Displays.wrappedText(Text.of(text), width, { color }, shadow))
    }

    fun vertical(spacing: Int = 0, alignment: Alignment = Alignment.START, builder: VerticalDisplayBuilder.() -> Unit) {
        val builder = VerticalDisplayBuilder()
        builder.builder()
        displays.add(builder.build(spacing, alignment))
    }

    fun horizontal(spacing: Int = 0, alignment: Alignment = Alignment.START, builder: HorizontalDisplayBuilder.() -> Unit) {
        val builder = HorizontalDisplayBuilder()
        builder.builder()
        displays.add(builder.build(spacing, alignment))
    }

    fun layered(builder: LayeredDisplayBuilder.() -> Unit) {
        val builder = LayeredDisplayBuilder()
        builder.builder()
        displays.add(builder.build())
    }

    abstract fun build(spacing: Int = 0, alignment: Alignment = Alignment.START): Display
}

class VerticalDisplayBuilder : DisplayBuilder() {
    override fun build(spacing: Int, alignment: Alignment): Display {
        return displays.toColumn(spacing, alignment)
    }
}

class HorizontalDisplayBuilder : DisplayBuilder() {
    override fun build(spacing: Int, alignment: Alignment): Display {
        return displays.toRow(spacing, alignment)
    }
}

class LayeredDisplayBuilder : DisplayBuilder() {
    /**
     * Doesn't use spacing or alignment.
     */
    override fun build(spacing: Int, alignment: Alignment): Display {
        return displays.asLayer()
    }
}
