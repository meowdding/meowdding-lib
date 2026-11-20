package me.owdding.lib.utils.suggestions

public object MeowddingSuggestionProviders {

    public fun <T> iterable(providers: Iterable<T>, transformer: (T) -> String = { it.toString() }): MeowddingSuggestionProvider {
        return IterableSuggestionProvider(providers, transformer)
    }
}
