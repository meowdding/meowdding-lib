@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package me.owdding.lib.repo

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.GenerateDispatchCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.lib.events.StartRepoLoadingEvent
import me.owdding.lib.generated.CodecUtils
import me.owdding.lib.generated.DispatchHelper
import me.owdding.lib.generated.MeowddingLibCodecs
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemOrigin.BINGO
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemOrigin.RIFT
import java.util.EnumSet
import kotlin.collections.get
import kotlin.math.roundToInt
import kotlin.reflect.KClass

@Module
object AccessoryRepoData {

    var families: Map<String, AccessoryFamily> = emptyMap()
        private set
    var rarityUpgraded: Map<SkyBlockId, AccessoryRarityUpgraded> = emptyMap()
        private set
    var disallowedOriginFamilies: Set<String> = emptySet()
        private set

    private val unobtainable: Set<SkyBlockId> by MeowddingLib.repoDelegate("accessories/unobtainable_accessories", emptySet())
    private val accessoryPowerData: AccessoryPowerRepoData? by MeowddingLib.repoDelegate("accessories/magical_power")

    fun getFamily(id: SkyBlockId): AccessoryFamily? = families.values.find { it.contains(id) }
    fun getRarityUpgraded(id: SkyBlockId): AccessoryRarityUpgraded? = rarityUpgraded[id]
    fun isIgnored(id: SkyBlockId): Boolean = id in unobtainable
    fun upgradesRarity(id: SkyBlockId): Boolean = id in rarityUpgraded

    fun isDisallowed(family: AccessoryFamily): Boolean = family.family in disallowedOriginFamilies

    fun isDisallowedOrigin(id: SkyBlockId): Boolean = getFamily(id)?.let(::isDisallowed) == true
    fun getAp(item: ItemStack): Int = accessoryPowerData?.getAccessoryPower(item) ?: 1
    fun AccessoryFamily.isRiftPrism(): Boolean = family == "rift_prism"

    private fun calculateIsDisallowedOrigin(family: AccessoryFamily): Boolean {
        return family.flatMapItems().any { id ->
            val data = ItemData.getItemData(id.skyblockId) ?: return@any false
            when (data.origin) {
                BINGO -> true
                RIFT -> !data.riftTransferable
                else -> false
            }
        }
    }

    @Subscription(StartRepoLoadingEvent::class)
    private fun onRepoStart() {
        families = emptyMap()
        rarityUpgraded = emptyMap()
        disallowedOriginFamilies = emptySet()
    }

    @Subscription(FinishRepoLoadingEvent::class)
    private fun onRepoLoad() {
        families = MeowddingLib.loadRepo<List<AccessoryFamily>>("accessories/families")
            ?.associateBy { it.family }
            .orEmpty()
            .onEach { (_, family) ->
                // Initialize the tier of the tiers
                family.forEachIndexed { index, tier ->
                    tier.tier = index
                }
            }

        rarityUpgraded = MeowddingLib.loadRepo<List<AccessoryRarityUpgraded>>("accessories/rarity_upgraded")
            ?.associateBy { it.item }.orEmpty()

        disallowedOriginFamilies = families.filterValues(::calculateIsDisallowedOrigin).keys
    }

}


@GenerateCodec
data class AccessoryFamily(
    val family: String,
    @Compact val tiers: List<AccessoryTier>,
    @FieldName("ignore_duplicates") val ignoreDuplicates: Boolean = false,
) : List<AccessoryTier> by tiers {
    //region Functions
    inline val maxTier: Int get() = lastIndex
    fun flatMapItems(): Sequence<SkyBlockId> = asSequence().flatMap(AccessoryTier::items)
    operator fun get(id: SkyBlockId): AccessoryTier? = find { id in it }
    fun contains(id: SkyBlockId): Boolean = any { id in it }
    //endregion

    companion object {
        val CODEC: Codec<AccessoryFamily> = MeowddingLibCodecs.getCodec()
    }
}

// Not a data class so that equality checks aren't done with its contents
class AccessoryTier(
    val items: Set<SkyBlockId>,
) : Set<SkyBlockId> by items, Comparable<AccessoryTier> {
    /** Index of the [AccessoryTier] in its family, gets initialized when family is created */
    var tier: Int = -1
        internal set

    override fun compareTo(other: AccessoryTier): Int = tier.compareTo(other.tier)

    companion object {
        @IncludedCodec
        val CODEC: Codec<AccessoryTier> = CodecUtils.compactSet(SkyBlockId.CODEC).xmap(::AccessoryTier, AccessoryTier::items)
    }
}

@GenerateCodec
data class AccessoryRarityUpgraded(
    val item: SkyBlockId,
    val rarities: EnumSet<SkyBlockRarity>,
) : Set<SkyBlockRarity> by rarities {
    fun isMax(rarity: SkyBlockRarity) = rarities.maxOrNull() == rarity
    fun nextAfter(rarity: SkyBlockRarity): SkyBlockRarity? = firstOrNull { it > rarity }

    companion object {
        val CODEC: Codec<AccessoryRarityUpgraded> = MeowddingLibCodecs.getCodec()
    }
}

@GenerateCodec
data class AccessoryPowerRepoData(
    val rarity: Map<SkyBlockRarity, Int>,
    val overrides: Map<SkyBlockId, Override>,
) {
    fun getAccessoryPower(item: ItemStack): Int {
        val override = overrides[item.getSkyBlockId()]
        val ap = rarity[item.getData(DataTypes.RARITY)] ?: 0

        return override?.apply(ap) ?: ap
    }
}

abstract class Override(val type: OverrideTypes) {
    abstract fun apply(num: Int): Int
}

@GenerateCodec
data class MultiplyOverride(val value: Double) : Override(OverrideTypes.MULTIPLY) {
    override fun apply(num: Int) = (num * value).roundToInt()
}

@GenerateDispatchCodec(Override::class)
enum class OverrideTypes(override val type: KClass<out Override>) : DispatchHelper<Override> {
    MULTIPLY(MultiplyOverride::class),
    ;

    companion object {
        fun getType(id: String) = valueOf(id.uppercase())
    }
}
