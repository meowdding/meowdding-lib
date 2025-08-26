package me.owdding.lib.utils.type

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.SharedSuggestionProvider
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

data class EnumArgumentType<T : Enum<T>>(val enum: KClass<T>, val string: (T) -> String = { it.name }) : ArgumentType<T> {

    val identifierNotFound: DynamicCommandExceptionType = DynamicCommandExceptionType { id: Any? ->
        Text.of("Entry ") {
            append("$id") { this.color = TextColor.GOLD }
            append(" not found")
        }
    }
    val entries = enum.java.enumConstants.toList().associateBy { string(it) }

    override fun parse(reader: StringReader): T {
        val name = reader.readString()
        return entries[name] ?: run {
            throw identifierNotFound.createWithContext(reader, name)
        }
    }

    override fun <S : Any?> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        fun matches(arg: String): Boolean = SharedSuggestionProvider.matchesSubStr(builder.remaining.lowercase(), arg)

        entries.keys.forEach {
            if (matches(it)) {
                builder.suggest(it)
            }
        }

        return builder.buildFuture()
    }
}
