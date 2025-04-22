package me.owdding.lib.extensions

import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.text.Text

fun MutableList<in MutableComponent>.add(number: Number, init: MutableComponent.() -> Unit = {}) = this.add(Text.of(number.toString(), init))
fun MutableList<in MutableComponent>.add(boolean: Boolean, init: MutableComponent.() -> Unit = {}) = this.add(Text.of(boolean.toString(), init))
fun MutableList<in MutableComponent>.add(text: String, init: MutableComponent.() -> Unit = {}) = this.add(Text.of(text, init))
fun MutableList<in MutableComponent>.add(init: MutableComponent.() -> Unit) = this.add(Text.of("", init))

fun <T> Map<out Number, T>.sortByKey(): Map<Number, T> = this.entries.sortedBy { it.key.toLong() }.associate { it.toPair() }

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
