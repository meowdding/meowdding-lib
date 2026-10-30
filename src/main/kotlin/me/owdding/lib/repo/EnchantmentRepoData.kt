package me.owdding.lib.repo

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.*
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

@Module
object EnchantmentRepoData {

    val data: List<ParsedEnchantmentData> by MeowddingLib.repoDelegate("enchantments", emptyList())

    @GenerateCodec
    data class ParsedEnchantmentData(
        val name: String,
        @FieldName("min_level") @OptionalInt(1) val minLevel: Int = 1,
        @FieldName("max_level") val maxLevel: Int,
        @FieldName("per_item_overrides") @OptionalIfEmpty val perItemOverrides: Map<SkyBlockId, Int>,
        @FieldName("is_ultimate") @OptionalBoolean(false) val isUltimate: Boolean = false,
        val sources: List<EnchantmentSource>,
        @OptionalIfEmpty val conflicts: List<String> = listOf(),
        @OptionalIfEmpty @FieldName("applicable_to") val applicableTo: List<String> = listOf(),
        @NamedCodec("repo:enchantment:requirements") val requirements: List<Requirement> = emptyList(),
    )

    @GenerateCodec
    data class EnchantmentSource(
        val name: String,
        val min: Int,
        val max: Int,
    )

    interface Requirement {
        fun canUse(): Boolean
        val formatted: String
    }

    data class EnchantingLevel(
        val level: Int,
    ) : Requirement {
        override fun canUse() = true // todo add checks once skill api is a thing
        override val formatted: String = "Enchanting Level $level"
    }

    internal data class PassthroughRequirement(override val formatted: String) : Requirement {
        override fun canUse(): Boolean = true
    }

    @IncludedCodec(named = "repo:enchantment:requirements")
    val REQUIREMENT_CODEC: Codec<List<Requirement>> = Codec.STRING.listOf().xmap(
        {
            it.mapNotNull {
                when {
                    it.startsWith("Enchanting Level") -> EnchantingLevel(it.substringAfterLast(" ").toIntOrNull() ?: return@mapNotNull null)
                    else -> null
                }
            }
        },
        { it.map { it.formatted } },
    )
}
