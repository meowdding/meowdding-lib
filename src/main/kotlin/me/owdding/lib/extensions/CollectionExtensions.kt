package me.owdding.lib.extensions

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.builders.TooltipBuilder
import tech.thatgravyboat.skyblockapi.utils.text.Text

public fun MutableList<in MutableComponent>.add(number: Number, init: MutableComponent.() -> Unit = {}): Boolean =
    this.add(Text.of(number.toString(), init))
public fun MutableList<in MutableComponent>.add(boolean: Boolean, init: MutableComponent.() -> Unit = {}): Boolean =
    this.add(Text.of(boolean.toString(), init))
public fun MutableList<in MutableComponent>.add(text: String, init: MutableComponent.() -> Unit = {}): Boolean =
    this.add(Text.of(text, init))
public fun MutableList<in MutableComponent>.add(init: MutableComponent.() -> Unit): Boolean =
    this.add(Text.of("", init))

@Deprecated("Use .sortedByKeys() instead", ReplaceWith("sortedByKeys()"))
public fun <T> Map<out Number, T>.sortByKey(): Map<Number, T> = toList().sortedBy { it.first.toLong() }.toMap()
public fun <K : Number, V> Map<out K, V>.sortedByKeys(): Map<K, V> = toList().sortedBy { it.first.toLong() }.toMap()

public fun <T> List<T>.rightPad(size: Int, element: T): MutableList<T> {
    if (this !is MutableList<T>) {
        return this.toMutableList().rightPad(size, element)
    }

    while (this.size < size) {
        this.add(this.lastIndex + 1, element)
    }
    return this
}

public fun <T> List<List<T>>.transpose(): List<List<T>> {
    val list = mutableListOf<MutableList<T>>()
    for (x in indices) {
        for (y in this[x].indices) {
            if (x == 0) {
                list.add(mutableListOf())
            }
            list[y].add(this[x][y])
        }
    }
    return list
}

public fun ListMerger<out Component>.applyToTooltip(builder: TooltipBuilder) {
    destination.forEach { builder.add(it) }
}

public class ListMerger<T>(public val original: List<T>, public var index: Int = 0) {
    public val destination: MutableList<T> = mutableListOf()

    public fun peek(): T = original[index]
    public fun read(): T = original[index++]
    public fun copy(): Boolean = destination.add(read())
    public fun add(item: T): Boolean = destination.add(item)


    public fun addAfterNext(predicate: (T) -> Boolean, provider: MutableList<T>.() -> Unit) {
        addUntil(predicate)
        copy()
        destination.provider()
    }

    public fun addUntil(predicate: (T) -> Boolean) {
        while (index + 1 < original.size && !predicate(peek())) copy()
    }

    public fun addBeforeNext(predicate: (T) -> Boolean, provider: MutableList<T>.() -> Unit) {
        addUntil(predicate)
        destination.provider()
        copy()
    }

    public fun addRemaining() {
        if (index >= original.size) return
        destination.addAll(original.subList(index, original.size))
        index = original.size
    }

    public fun hasNext(predicate: (T) -> Boolean): Boolean = this.original.subList(index, original.size).any(predicate)
    public fun canRead(): Boolean = index < original.size
    public fun readSafe(): T? = if (canRead()) read() else null

    // Added for binary compatibility
    public operator fun component0(): List<T> = original
    public operator fun component1(): Int = index
}
