package me.owdding.lib.extensions

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

fun Double.floor(): Int = floor(this).toInt()
fun Float.floor(): Int = floor(this).toInt()
fun Double.ceil(): Int = ceil(this).toInt()
fun Float.ceil(): Int = ceil(this).toInt()
fun Double.roundToHalf() = (this * 2).roundToInt() / 2.0
fun Float.roundToHalf() = (this * 2).roundToInt() / 2.0f
fun Double.floorToHalf() = (this * 2).floor() / 2.0
fun Float.floorToHalf() = (this * 2).floor() / 2.0f
