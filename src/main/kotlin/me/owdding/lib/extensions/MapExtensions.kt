package me.owdding.lib.extensions

fun <K, V> MutableMap<K, V>.removeIf(predicate: (Map.Entry<K, V>) -> Boolean): MutableMap<K, V> = apply { entries.removeIf(predicate) }
