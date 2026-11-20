package me.owdding.lib.extensions

public fun <K, V> MutableMap<K, V>.removeIf(predicate: (Map.Entry<K, V>) -> Boolean): MutableMap<K, V> = apply { entries.removeIf(predicate) }

public inline fun <Origin, Key, Value> Iterable<Origin>.associateNotNull(keySelector: (Origin) -> Key?, valueSelector: (Origin) -> Value?): Map<Key, Value> =
    buildMap {
        for (element in this@associateNotNull) put(keySelector(element) ?: continue, valueSelector(element) ?: continue)
    }
