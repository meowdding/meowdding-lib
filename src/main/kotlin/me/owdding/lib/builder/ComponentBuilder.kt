package me.owdding.lib.builder

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.ComponentLike
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent

object ComponentFactory {
    fun line(builder: ComponentBuilder.() -> Unit): MutableComponent {
        val builder = SingularLineComponentBuilder()
        builder.builder()
        return builder.component
    }

    fun multiline(builder: MultilineComponentBuilder.() -> Unit): MutableComponent {
        val builder = MultilineComponentBuilder()
        builder.builder()
        return builder.component
    }
}

abstract class ComponentBuilder {
    internal var component: MutableComponent = CommonText.EMPTY.copy()

    open fun component(component: MutableComponent, init: MutableComponent.() -> Unit = {}) {
        this.component.append(component.apply(init))
    }

    fun string(text: String, init: MutableComponent.() -> Unit = {}) {
        component(Text.of(text), init)
    }

    fun component(component: Component, init: MutableComponent.() -> Unit = {}) {
        component(component.copy(), init)
    }

    fun component(init: MutableComponent.() -> Unit) {
        component(Component.empty(), init)
    }

    fun MutableComponent.append(like: ComponentLike): MutableComponent = this.append(like.toComponent())
    fun MutableComponent.append(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(text.asComponent(init))
    fun MutableComponent.append(component: Component, init: MutableComponent.() -> Unit): MutableComponent = this.append(component.copy().apply(init))
    fun MutableComponent.append(number: Number, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(number.toString().asComponent(init))
    fun MutableComponent.append(boolean: Boolean, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(boolean.toString().asComponent(init))
}

class SingularLineComponentBuilder : ComponentBuilder()

class MultilineComponentBuilder : ComponentBuilder() {
    fun newLine() {
        component.append(CommonText.NEWLINE)
    }

    override fun component(component: MutableComponent, init: MutableComponent.() -> Unit) {
        super.component(component, init)
        newLine()
    }
}
