package me.owdding.lib.repo

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktcodecs.NamedCodec
import me.owdding.ktcodecs.OptionalBoolean
import me.owdding.ktcodecs.OptionalIfEmpty
import me.owdding.ktcodecs.OptionalInt
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import tech.thatgravyboat.repolib.api.EnchantsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

@Module
public object EnchantmentRepoData {

    public val data: List<ParsedEnchantmentData> by MeowddingLib.repoDelegate("enchantments", emptyList())

    @GenerateCodec
    public data class ParsedEnchantmentData(
        val id: String,
        val name: String,
        @FieldName("min_level") @OptionalInt(1) val minLevel: Int = 1,
        @FieldName("max_level") val maxLevel: Int,
        @FieldName("per_item_overrides") @OptionalIfEmpty val perItemOverrides: Map<SkyBlockId, Int> = emptyMap(),
        @FieldName("is_ultimate") @OptionalBoolean(false) val isUltimate: Boolean = false,
        val sources: List<EnchantmentSource> = emptyList(),
        @OptionalIfEmpty val conflicts: List<String> = emptyList(),
        @OptionalIfEmpty @FieldName("applicable_to") val applicableTo: List<String> = emptyList(),
        @NamedCodec("repo:enchantment:requirements") val requirements: List<Requirement> = emptyList(),
    ) {
        val enchant: EnchantsAPI.Enchant?
            get() = if (RepoAPI.isInitialized()) {
                RepoAPI.enchantments().getEnchantment(id)
            } else null
    }

    @GenerateCodec
    public data class EnchantmentSource(
        val name: String,
        val min: Int,
        val max: Int,
    )

    public interface Requirement {
        public fun canUse(): Boolean
        public val formatted: String
    }

    public data class EnchantingLevel(
        val level: Int,
    ) : Requirement {
        override fun canUse(): Boolean = true // todo add checks once skill api is a thing
        override val formatted: String = "Enchanting Level $level"
    }

    internal data class PassthroughRequirement(override val formatted: String) : Requirement {
        override fun canUse(): Boolean = true
    }

    @IncludedCodec(named = "repo:enchantment:requirements")
    public val REQUIREMENT_CODEC: Codec<List<Requirement>> = Codec.STRING.listOf().xmap(
        { requirements ->
            requirements.mapNotNull { it ->
                when {
                    it.startsWith("Enchanting Level") -> EnchantingLevel(it.substringAfterLast(" ").toIntOrNull() ?: return@mapNotNull null)
                    else -> null
                }
            }
        },
        { it.map { requirement -> requirement.formatted } },
    )
}
