package me.owdding.lib.platform

import it.unimi.dsi.fastutil.ints.IntArraySet
import it.unimi.dsi.fastutil.ints.IntSet
import tech.thatgravyboat.skyblockapi.helpers.McScreen

internal object KeyHelper {
    val states: IntSet = IntArraySet(5)

    fun press(button: Int) {
        states.add(button)
    }
    fun release(button: Int) {
        states.remove(button)
    }

    fun isPressed(button: Int) = states.contains(button)
}

fun McScreen.isMouseDown(button: Int): Boolean = KeyHelper.isPressed(button)
