package me.owdding.lib.extensions

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

public fun Double.floor(): Int = floor(this).toInt()
public fun Float.floor(): Int = floor(this).toInt()
public fun Double.ceil(): Int = ceil(this).toInt()
public fun Float.ceil(): Int = ceil(this).toInt()
public fun Double.roundToHalf(): Double = (this * 2).roundToInt() / 2.0
public fun Float.roundToHalf(): Float = (this * 2).roundToInt() / 2.0f
public fun Double.floorToHalf(): Double = (this * 2).floor() / 2.0
public fun Float.floorToHalf(): Float = (this * 2).floor() / 2.0f
