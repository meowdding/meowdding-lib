package me.owdding.lib.extensions

import org.jetbrains.annotations.Range

private val knownPaths = mapOf(
    "me.owdding.lib" to "mlib",
    "me.owdding.skyblockpv" to "pv",
    "tech.thatgravyboat.skyblockapi" to "sb-api",
    "tech.thatgravyboat.skycubed" to "sc",
    "me.owdding.skycubed" to "sc",
    "me.owdding.skyocean" to "so",
    "me.owdding.customscoreboard" to "cs",
    "me.owdding.patches" to "patches",
    "com.teamresourceful.resourcefulconfig" to "rconfig",
    "com.teamresourceful.resourcefullib" to "rlib",
    "earth.terrarium.olympus" to "olympus",
    "com.google.gson" to "gson",
    "net.fabricmc" to "fabric",
    "net.minecraft" to "mc",
).mapKeys { (k) -> Regex("(?:knot//)?$k") }

private fun String.applyAllReplacements(): String {
    var result = this
    knownPaths.forEach { (path, replacement) ->
        result = result.replace(path, replacement.uppercase())
    }
    return result
}

fun Throwable.getStackTraceString(elements: @Range(from = 1, to = 0xFFFFFFFF) Int): String = with(StringBuilder()) {
    val throwable = this@getStackTraceString
    throwable.stackTrace.take(elements).joinTo(this, "\n") {
        it.toString().applyAllReplacements()
    }
}.toString()
