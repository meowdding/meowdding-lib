package me.owdding.lib.utils.suggestions

object MeowddingSuggestionProviders {

    fun <T> iterable(providers: Iterable<T>, transformer: (T) -> String = { it.toString() }): MeowddingSuggestionProvider {
        return IterableSuggestionProvider(providers, transformer)
    }

}
