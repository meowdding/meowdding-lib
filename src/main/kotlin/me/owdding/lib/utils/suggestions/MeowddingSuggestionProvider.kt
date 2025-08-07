package me.owdding.lib.utils.suggestions

import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import tech.thatgravyboat.skyblockapi.utils.extentions.sanitizeForCommandInput


interface MeowddingSuggestionProvider : SuggestionProvider<FabricClientCommandSource> {

    fun suggest(builder: SuggestionsBuilder, name: String) {
        val filtered = name.sanitizeForCommandInput()
        if (SharedSuggestionProvider.matchesSubStr(builder.remaining.lowercase(), filtered.lowercase())) {
            builder.suggest(filtered)
        }
    }

}
