package me.owdding.lib.builder

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.ComponentLike
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent

public object ComponentFactory {
    public fun line(builder: ComponentBuilder.() -> Unit): MutableComponent {
        val builder = SingularLineComponentBuilder()
        builder.builder()
        return builder.component
    }

    public fun multiline(builder: MultilineComponentBuilder.() -> Unit): MutableComponent {
        val builder = MultilineComponentBuilder()
        builder.builder()
        return builder.component
    }
}

public abstract class ComponentBuilder {
    internal var component: MutableComponent = CommonText.EMPTY.copy()

    public open fun component(component: MutableComponent, init: MutableComponent.() -> Unit = {}) {
        this.component.append(component.apply(init))
    }

    public fun string(text: String, init: MutableComponent.() -> Unit = {}) {
        component(Text.of(text), init)
    }

    public fun component(component: Component, init: MutableComponent.() -> Unit = {}) {
        component(component.copy(), init)
    }

    public fun component(init: MutableComponent.() -> Unit) {
        component(Component.empty(), init)
    }

    public fun MutableComponent.append(like: ComponentLike): MutableComponent = this.append(like.toComponent())
    public fun MutableComponent.append(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(text.asComponent(init))
    public fun MutableComponent.append(component: Component, init: MutableComponent.() -> Unit): MutableComponent = this.append(component.copy().apply(init))
    public fun MutableComponent.append(number: Number, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(number.toString().asComponent(init))
    public fun MutableComponent.append(boolean: Boolean, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(boolean.toString().asComponent(init))
}

public class SingularLineComponentBuilder : ComponentBuilder()

public class MultilineComponentBuilder : ComponentBuilder() {
    private var isFirstLine = true

    public fun newLine() {
        component.append(CommonText.NEWLINE)
    }

    public override fun component(component: MutableComponent, init: MutableComponent.() -> Unit) {
        if (!isFirstLine) newLine()
        super.component(component, init)
        isFirstLine = false
    }
}
