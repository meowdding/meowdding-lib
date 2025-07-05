package me.owdding.lib.extensions

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.builders.TooltipBuilder
import tech.thatgravyboat.skyblockapi.utils.text.Text

fun MutableList<in MutableComponent>.add(number: Number, init: MutableComponent.() -> Unit = {}) = this.add(Text.of(number.toString(), init))
fun MutableList<in MutableComponent>.add(boolean: Boolean, init: MutableComponent.() -> Unit = {}) = this.add(Text.of(boolean.toString(), init))
fun MutableList<in MutableComponent>.add(text: String, init: MutableComponent.() -> Unit = {}) = this.add(Text.of(text, init))
fun MutableList<in MutableComponent>.add(init: MutableComponent.() -> Unit) = this.add(Text.of("", init))

@Deprecated("Use .sortedByKeys() instead", ReplaceWith("sortedByKeys()"))
fun <T> Map<out Number, T>.sortByKey(): Map<Number, T> = toList().sortedBy { it.first.toLong() }.toMap()
fun <K : Number, V> Map<out K, V>.sortedByKeys(): Map<K, V> = toList().sortedBy { it.first.toLong() }.toMap()

fun <T> List<T>.rightPad(size: Int, element: T): MutableList<T> {
    if (this !is MutableList<T>) {
        return this.toMutableList().rightPad(size, element)
    }

    while (this.size < size) {
        this.add(this.lastIndex + 1, element)
    }
    return this
}

fun <T> List<List<T>>.transpose(): List<List<T>> {
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

fun ListMerger<out Component>.applyToTooltip(builder: TooltipBuilder) {
    destination.forEach { builder.add(it) }
}

data class ListMerger<T>(val original: List<T>, var index: Int = 0) {
    val destination: MutableList<T> = mutableListOf()

    fun peek() = original[index]
    fun read() = original[index++]
    fun copy() = destination.add(read())
    fun add(item: T) = destination.add(item)


    fun addAfterNext(predicate: (T) -> Boolean, provider: MutableList<T>.() -> Unit) {
        addUntil(predicate)
        copy()
        destination.provider()
    }

    fun addUntil(predicate: (T) -> Boolean) {
        while (index + 1 < original.size && !predicate(peek())) {
            copy()
        }
    }

    fun addBeforeNext(predicate: (T) -> Boolean, provider: MutableList<T>.() -> Unit) {
        addUntil(predicate)
        destination.provider()
        copy()
    }

    fun addRemaining() {
        if (index >= original.size) {
            return
        }
        destination.addAll(original.subList(index, original.size))
    }

    fun hasNext(predicate: (T) -> Boolean): Boolean = this.original.subList(index, original.size).any(predicate)
    fun canRead(): Boolean = index < original.size
    fun readSafe(): T? = if (canRead()) read() else null
}
