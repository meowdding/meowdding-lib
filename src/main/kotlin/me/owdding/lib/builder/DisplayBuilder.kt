package me.owdding.lib.builder

import me.owdding.lib.displays.Alignment
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asLayer
import me.owdding.lib.displays.toColumn
import me.owdding.lib.displays.toRow
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import tech.thatgravyboat.skyblockapi.utils.text.Text

public object DisplayFactory {
    public fun vertical(spacing: Int = 0, alignment: Alignment = Alignment.START, builder: DisplayBuilder.() -> Unit): Display {
        val builder = VerticalDisplayBuilder()
        builder.builder()
        return builder.build(spacing, alignment)
    }

    public fun horizontal(spacing: Int = 0, alignment: Alignment = Alignment.START, builder: DisplayBuilder.() -> Unit): Display {
        val builder = HorizontalDisplayBuilder()
        builder.builder()
        return builder.build(spacing, alignment)
    }
}

public abstract class DisplayBuilder {
    protected val displays: MutableList<Display> = mutableListOf()

    public fun string(text: String) {
        displays.add(Displays.text(text))
    }

    public fun string(component: Component) {
        displays.add(Displays.component(component, maxWidth = Int.MAX_VALUE))
    }

    public fun string(text: String, init: MutableComponent.() -> Unit) {
        string(Text.of(text, init))
    }

    public fun singularComponent(builder: ComponentBuilder.() -> Unit) {
        val builder = SingularLineComponentBuilder()
        builder.builder()
        string(builder.component)
    }

    public fun multilineComponent(builder: MultilineComponentBuilder.() -> Unit) {
        val builder = MultilineComponentBuilder()
        builder.builder()
        string(builder.component)
    }

    public fun display(display: Display) {
        displays.add(display)
    }

    public open fun item(
        item: ItemStack,
        width: Int = 16,
        height: Int = 16,
        showTooltip: Boolean = false,
        showStackSize: Boolean = false,
        customStackText: Any? = null,
    ) {
        display(Displays.item(item, width, height, showTooltip, showStackSize, customStackText))
    }

    public open fun item(
        item: ItemLike,
        width: Int = 16,
        height: Int = 16,
        customStackText: Any? = null,
    ) {
        display(Displays.item(item, width, height, customStackText = customStackText))
    }


    public fun spacer(width: Int = 0, height: Int = 0) {
        displays.add(Displays.empty(width, height))
    }

    public fun supplied(supplier: () -> Display) {
        displays.add(Displays.supplied(supplier))
    }

    public fun textDisplay(
        text: String = "",
        color: UInt = 0x555555u,
        shadow: Boolean = false,
        displayModifier: Display.() -> Display = { this },
        init: MutableComponent.() -> Unit,
    ) {
        Displays.text(Text.of(text, init), { color }, shadow = shadow).displayModifier().let { display(it) }
    }

    public fun textDisplay(text: String = "", color: UInt = 0x555555u, shadow: Boolean = false, init: MutableComponent.() -> Unit) {
        textDisplay(text, color, shadow, { this }, init)
    }

    public fun wrappedText(text: String, width: Int, color: UInt = 0x555555u, shadow: Boolean = false) {
        displays.add(Displays.wrappedText(Text.of(text), width, { color }, shadow))
    }

    public fun vertical(spacing: Int = 0, alignment: Alignment = Alignment.START, builder: VerticalDisplayBuilder.() -> Unit) {
        val builder = VerticalDisplayBuilder()
        builder.builder()
        displays.add(builder.build(spacing, alignment))
    }

    public fun horizontal(spacing: Int = 0, alignment: Alignment = Alignment.START, builder: HorizontalDisplayBuilder.() -> Unit) {
        val builder = HorizontalDisplayBuilder()
        builder.builder()
        displays.add(builder.build(spacing, alignment))
    }

    public fun layered(builder: LayeredDisplayBuilder.() -> Unit) {
        val builder = LayeredDisplayBuilder()
        builder.builder()
        displays.add(builder.build())
    }

    public abstract fun build(spacing: Int = 0, alignment: Alignment = Alignment.START): Display
}

public class VerticalDisplayBuilder : DisplayBuilder() {
    public override fun build(spacing: Int, alignment: Alignment): Display {
        return displays.toColumn(spacing, alignment)
    }
}

public class HorizontalDisplayBuilder : DisplayBuilder() {
    public override fun build(spacing: Int, alignment: Alignment): Display {
        return displays.toRow(spacing, alignment)
    }
}

public class LayeredDisplayBuilder : DisplayBuilder() {
    /**
     * Doesn't use spacing or alignment.
     */
    public override fun build(spacing: Int, alignment: Alignment): Display {
        return displays.asLayer()
    }
}
