package me.owdding.lib.builder

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.utils.ListenableState
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asWidget
import me.owdding.lib.extensions.floor
import me.owdding.lib.layouts.Scalable
import me.owdding.lib.layouts.ScalableLayout
import me.owdding.lib.layouts.ScalableWidget
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.*
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.text.Text

@Deprecated(message = "Deprecated in favour of LayoutFactory", replaceWith = ReplaceWith("LayoutFactory"))
object LayoutBuild {
    fun vertical(spacing: Int = 0, alignment: Float = 0f, builder: VerticalLayoutBuilder.() -> Unit): LinearLayout {
        val builder = VerticalLayoutBuilder()
        builder.builder()
        return builder.build(spacing, alignment)
    }

    fun horizontal(spacing: Int = 0, alignment: Float = 0f, builder: HorizontalLayoutBuilder.() -> Unit): LinearLayout {
        val builder = HorizontalLayoutBuilder()
        builder.builder()
        return builder.build(spacing, alignment)
    }

    fun frame(width: Int = 0, height: Int = 0, builder: FrameLayoutBuilder.() -> Unit): FrameLayout {
        val builder = FrameLayoutBuilder(width, height)
        builder.builder()
        return builder.build()
    }
}

object LayoutFactory {
    fun vertical(spacing: Int = 0, alignment: Float = 0f, builder: LayoutBuilder.() -> Unit): Layout {
        val builder = VerticalLayoutBuilder()
        builder.builder()
        return builder.build(spacing, alignment)
    }

    fun horizontal(spacing: Int = 0, alignment: Float = 0f, builder: LayoutBuilder.() -> Unit): Layout {
        val builder = HorizontalLayoutBuilder()
        builder.builder()
        return builder.build(spacing, alignment)
    }

    fun frame(width: Int = 0, height: Int = 0, builder: LayoutBuilder.() -> Unit): Layout {
        val builder = FrameLayoutBuilder(width, height)
        builder.builder()
        return builder.build()
    }

    fun empty() = frame {}
}

data class LayoutElements(val element: LayoutElement, val settings: LayoutSettings.() -> Unit)

const val LEFT = 0f
const val MIDDLE = 0.5f
const val RIGHT = 1f

abstract class LayoutBuilder {

    protected val widgets = mutableListOf<LayoutElements>()

    private fun MutableList<LayoutElements>.add(element: LayoutElement) {
        if (element is AbstractWidget) {
            add(LayoutElements(ScalableWidget(element)) {})
            return
        }
        add(LayoutElements(element) {})
    }

    fun widget(widget: LayoutElement) {
        widgets.add(widget)
    }

    fun LayoutElement.add() {
        this@LayoutBuilder.widget(this)
    }

    fun widget(widget: List<LayoutElement>) {
        widget.forEach(this::widget)
    }

    fun List<LayoutElement>.add() {
        this@LayoutBuilder.widget(this)
    }

    fun widget(widget: LayoutElement, settings: LayoutSettings.() -> Unit) {
        if (widget is AbstractWidget) {
            widgets.add(LayoutElements(ScalableWidget(widget), settings))
            return
        }
        widgets.add(LayoutElements(widget, settings))
    }

    fun LayoutElement.add(settings: LayoutSettings.() -> Unit) {
        this@LayoutBuilder.widget(this, settings)
    }

    fun string(text: String) {
        widgets.add(Widgets.text(text))
    }

    fun display(display: Display) {
        widgets.add(display.asWidget())
    }

    fun Display.add() {
        this@LayoutBuilder.display(this)
    }

    fun verticalDisplay(builder: DisplayBuilder.() -> Unit) {
        val display = VerticalDisplayBuilder()
        display.builder()
        display(display.build())
    }

    fun horizontalDisplay(builder: DisplayBuilder.() -> Unit) {
        val display = HorizontalDisplayBuilder()
        display.builder()
        display(display.build())
    }

    fun layeredDisplay(builder: DisplayBuilder.() -> Unit) {
        val display = LayeredDisplayBuilder()
        display.builder()
        display(display.build())
    }

    fun string(component: Component) {
        widgets.add(Widgets.text(component))
    }

    fun string(text: String, init: MutableComponent.() -> Unit) {
        string(Text.of(text, init))
    }

    fun spacer(width: Int = 0, height: Int = 0) {
        widgets.add(SpacerElement(width, height))
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

    fun vertical(spacing: Int = 0, alignment: Float = 0f, builder: VerticalLayoutBuilder.() -> Unit) {
        val builder = VerticalLayoutBuilder()
        builder.builder()
        widgets.add(builder.build(spacing, alignment))
    }

    fun horizontal(spacing: Int = 0, alignment: Float = 0f, builder: HorizontalLayoutBuilder.() -> Unit) {
        val builder = HorizontalLayoutBuilder()
        builder.builder()
        widgets.add(builder.build(spacing, alignment))
    }

    fun textInput(
        state: ListenableState<String>,
        placeholder: String = "",
        width: Int,
        height: Int = 20,
        onChange: (String) -> Unit = {},
        onEnter: (String) -> Unit = {},
    ) {
        val input = Widgets.textInput(state) { box ->
            box.withEnterCallback {
                onEnter(box.value)
            }
            box.withChangeCallback {
                onChange(it)
            }
        }
        input.withPlaceholder(placeholder)
        input.withSize(width, height)

        widget(input)
    }

    abstract fun build(spacing: Int = 0, alignment: Float = 0.0f): Layout

    companion object {
        @Deprecated("Use the .setPos() in layouts/LayoutExtension.kt instead")
        fun Layout.setPos(x: Int, y: Int): Layout {
            this.setPosition(x, y)
            return this
        }
    }
}

class ScalableLinearLayout(orientation: Orientation, val spacing: Int, width: Int = 0, height: Int = 0) : LinearLayout(width, height, orientation),
    ScalableLayout {

    init {
        spacing(spacing)
    }

    override fun scale(scale: Double) {
        super.spacing((spacing * scale).floor())
        this.visitChildren {
            if (it is Scalable) {
                it.scale(scale)
            }
        }
        this.arrangeElements()
    }

    companion object {
        fun vertical(spacing: Int) = ScalableLinearLayout(Orientation.VERTICAL, spacing)
        fun horizontal(spacing: Int) = ScalableLinearLayout(Orientation.HORIZONTAL, spacing)
    }
}

class ScalableFrameLayout(width: Int, height: Int) : FrameLayout(width, height), ScalableLayout {
    override fun scale(scale: Double) {
        this.visitChildren {
            if (it is Scalable) {
                it.scale(scale)
            }
        }
        this.arrangeElements()
    }
}

class FrameLayoutBuilder(val width: Int, val height: Int) : LayoutBuilder() {
    override fun build(spacing: Int, alignment: Float): FrameLayout {
        val layout = ScalableFrameLayout(width, height)
        widgets.forEach { layout.addChild(it.element, it.settings) }
        layout.arrangeElements()
        return layout
    }
}

class VerticalLayoutBuilder : LayoutBuilder() {
    override fun build(spacing: Int, alignment: Float): LinearLayout {
        val layout = ScalableLinearLayout.vertical(spacing)
        widgets.forEach { layout.addChild(it.element, layout.newCellSettings().alignHorizontally(alignment).apply(it.settings)) }
        layout.arrangeElements()
        return layout
    }
}

class HorizontalLayoutBuilder : LayoutBuilder() {
    override fun build(spacing: Int, alignment: Float): LinearLayout {
        val layout = ScalableLinearLayout.horizontal(spacing)
        widgets.forEach { layout.addChild(it.element, layout.newCellSettings().alignVertically(alignment).apply(it.settings)) }
        layout.arrangeElements()
        return layout
    }
}
