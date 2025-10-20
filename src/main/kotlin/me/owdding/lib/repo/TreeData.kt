package me.owdding.lib.repo

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.notkamui.keval.KevalBuilder
import com.notkamui.keval.keval
import eu.pb4.placeholders.api.ParserContext
import eu.pb4.placeholders.api.parsers.TagParser
import me.owdding.ktcodecs.*
import me.owdding.ktmodules.Module
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.lib.generated.DispatchHelper
import me.owdding.lib.generated.MeowddingLibCodecs
import me.owdding.repo.RemoteRepo
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import org.joml.Vector2i
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.extentions.toTitleCase
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

@GenerateDispatchCodec(TreeNode::class)
enum class TreeNodes(override val type: KClass<out TreeNode>) : DispatchHelper<TreeNode> {
    PERK(LevelingTreeNode::class),
    UNLEVELABLE(UnlevelableTreeNode::class),
    ABILITY(AbilityTreeNode::class),
    CORE(CoreTreeNode::class),
    TIER(TierNode::class),
    SPACER(SpacerNode::class),
    ;

    @Module
    companion object {
        @IncludedCodec(named = "vec_2i")
        val VECTOR_2I: Codec<Vector2i> = Codec.INT.listOf(2, 2).xmap(
            { Vector2i(it[0], it[1]) },
            { listOf(it.x, it.y) },
        )

        @IncludedCodec(named = "reward_formula")
        val rewardFormulaCodec: Codec<Map<String, String>> = Codec.either(
            Codec.STRING.xmap({ mapOf("reward" to it) }, { it["reward"] }),
            Codec.unboundedMap(Codec.STRING, Codec.STRING) as Codec<Map<String, String>>,
        ).xmap(
            { Either.unwrap(it) },
            { if (it.size == 1) Either.left(it) else Either.right(it) },
        )

        fun getType(id: String) = valueOf(id.uppercase())
    }
}

@Module
object TreeRepoData {
    private val _hotm = AtomicReference<List<TreeNode>>()
    val hotm: List<TreeNode> get() = _hotm.get()

    private val _hotf = AtomicReference<List<TreeNode>>()
    val hotf: List<TreeNode> get() = _hotf.get()

    fun hotmByName(name: String) = hotm.find { it.name == name }
    fun hotfByName(name: String) = hotf.find { it.name == name }

    @Subscription
    fun finishRepoLoading(event: FinishRepoLoadingEvent) {
        RemoteRepo.getFileContentAsJson("hotm.json")?.toData(MeowddingLibCodecs.TreeNodeCodec.codec().listOf())?.apply(_hotm::set)
        RemoteRepo.getFileContentAsJson("hotf.json")?.toData(MeowddingLibCodecs.TreeNodeCodec.codec().listOf())?.apply(_hotf::set)
    }
}

data class Context(val hotmLevel: Int = -1, val perkLevel: Int = -1) {
    fun configure(kevalBuilder: KevalBuilder) = with(kevalBuilder) {
        function {
            name = "min"
            arity = 2
            implementation = DoubleArray::min
        }
        constant {
            name = "level"
            value = perkLevel.coerceAtLeast(1).toDouble()
        }
        constant {
            name = "hotmLevel"
            value = hotmLevel.coerceAtLeast(1).toDouble()
        }
        constant {
            name = "effectiveLevel"
            value = (perkLevel - 1).coerceAtLeast(0).toDouble()
        }
    }
}

abstract class TreeNode(val type: TreeNodes) {
    abstract val name: String
    abstract val id: String
    abstract val location: Vector2i

    abstract fun tooltip(context: Context): List<Component>
    abstract fun isMaxed(level: Int): Boolean
}

abstract class LevelableTooltipNode(type: TreeNodes) : TreeNode(type) {
    abstract val rewards: Map<String, String>
    abstract val tooltip: List<String>

    fun evaluate(context: Context): Map<String, String> {
        return rewards.mapValues {
            it.value.keval {
                includeDefault()
                context.configure(this)
            }.toFormattedString()
        }
    }

    override fun tooltip(context: Context): List<Component> {
        val replacement = evaluate(context)

        return tooltip.map {
            TagParser.QUICK_TEXT_SAFE.parseText(
                it.let {
                    var text = it
                    replacement.forEach { entry ->
                        text = text.replace("%${entry.key}%", entry.value)
                    }
                    text
                },
                ParserContext.of(),
            )
        }
    }
}


abstract class LevelableTreeNode(type: TreeNodes) : LevelableTooltipNode(type) {
    abstract val maxLevel: Int

    abstract fun costForLevel(level: Int): Pair<CostType, Int>
    abstract fun getPowderType(level: Int): CostType
}

@GenerateCodec
data class UnlevelableTreeNode(
    override val id: String,
    override val name: String,
    @NamedCodec("vec_2i") override val location: Vector2i,
    @FieldName("reward_formula") val rewardFormula: String = "0",
    val tooltip: List<String>,
) : TreeNode(TreeNodes.UNLEVELABLE) {
    private fun evaluate(context: Context): Double? {
        if (rewardFormula.isEmpty() || rewardFormula == "0") return null

        return rewardFormula.keval {
            includeDefault()
            context.configure(this)
        }
    }

    override fun tooltip(context: Context): List<Component> {
        val replacement = evaluate(context)?.toFormattedString() ?: ""

        return tooltip.map {
            TagParser.QUICK_TEXT_SAFE.parseText(it.replace("%reward%", replacement), ParserContext.of())
        }
    }

    override fun isMaxed(level: Int) = level > 0
}

@GenerateCodec
data class LevelingTreeNode(
    override val id: String,
    override val name: String,
    @NamedCodec("vec_2i") override val location: Vector2i,
    @FieldName("max_level") override val maxLevel: Int,
    @FieldName("cost") val powderType: CostType,
    @FieldName("cost_formula") val costFormula: String,
    @NamedCodec("reward_formula") @FieldName("reward_formula") override val rewards: Map<String, String>,
    override val tooltip: List<String>,
) : LevelableTreeNode(TreeNodes.PERK) {
    override fun costForLevel(level: Int): Pair<CostType, Int> {
        return getPowderType(level) to costFormula.keval {
            includeDefault()
            constant {
                name = "level"
                value = level.toDouble()
            }
            constant {
                name = "nextLevel"
                value = (level + 1).toDouble()
            }
        }.toInt()
    }

    override fun getPowderType(level: Int): CostType = powderType

    override fun isMaxed(level: Int) = level >= maxLevel
}

@GenerateCodec
data class AbilityTreeNode(
    override val id: String,
    override val name: String,
    @NamedCodec("vec_2i") override val location: Vector2i,
    @NamedCodec("reward_formula") @FieldName("reward_formula") override val rewards: Map<String, String>,
    override val tooltip: List<String>,
) : LevelableTooltipNode(TreeNodes.ABILITY) {
    override fun isMaxed(level: Int) = level == 3
}

@GenerateCodec
data class CoreTreeNode(
    override val id: String,
    override val name: String,
    @NamedCodec("vec_2i") override val location: Vector2i,
    val level: List<CotmLevel>,
) : LevelableTreeNode(TreeNodes.CORE) {

    @GenerateCodec
    data class CotmLevel(
        val cost: CostType = FreeCostType,
        val include: List<Int> = emptyList(),
        @Compact val reward: List<String>,
    ) {
        fun tooltip(treeNode: CoreTreeNode): List<String> {
            val tooltip = mutableListOf<String>()

            include.map { treeNode.getLevel(it) }.flatMap { it.reward }.forEach { tooltip.add(it) }
            tooltip.addAll(reward)

            return tooltip
        }
    }

    fun getLevel(level: Int): CotmLevel {
        return this.level[(level - 1).coerceAtLeast(0)]
    }

    override val rewards: Map<String, String>
        get() = emptyMap()
    override val tooltip: List<String>
        get() = emptyList()

    override fun tooltip(context: Context): List<Component> {
        return getLevel(context.perkLevel).tooltip(this).map {
            TagParser.QUICK_TEXT_SAFE.parseText(it, ParserContext.of())
        }
    }

    override fun isMaxed(level: Int) = level == maxLevel
    override val maxLevel: Int = level.size
    override fun costForLevel(level: Int): Pair<CostType, Int> = this.level[level - 1].cost.let { it to it.amount!! }
    override fun getPowderType(level: Int): CostType = this.level[level - 1].cost
}

@GenerateCodec
data class TierNode(
    override val name: String,
    @NamedCodec("vec_2i") override val location: Vector2i,
    val rewards: List<String>,
) : TreeNode(TreeNodes.TIER) {
    override val id: String = ""
    override fun tooltip(context: Context) = rewards.map {
        TagParser.QUICK_TEXT_SAFE.parseText(it, ParserContext.of())
    }

    override fun isMaxed(level: Int) = level >= (location.y + 1)
}

@GenerateCodec
data class SpacerNode(
    @NamedCodec("vec_2i") override val location: Vector2i,
    @NamedCodec("vec_2i") val size: Vector2i,
) : TreeNode(TreeNodes.SPACER) {
    override val name: String = ""
    override val id: String = ""

    override fun tooltip(context: Context) = emptyList<Component>()
    override fun isMaxed(level: Int) = false
}

@GenerateDispatchCodec(CostType::class)
enum class CostTypes(override val type: KClass<out CostType>) : DispatchHelper<CostType> {
    POWDER(PowderCostType::class),
    WHISPER(WhisperCostType::class),
    FREE(FreeCostType::class),
    ;

    companion object {
        fun getType(id: String) = CostTypes.valueOf(id.uppercase())
    }
}

object FreeCostType : CostType(CostTypes.FREE) {
    override val amount: Int? = null
    override val displayName: Component? = null
    override val formatting: ChatFormatting? = null
}

sealed class CostType(val type: CostTypes) {
    abstract val amount: Int?
    abstract val displayName: Component?
    abstract val formatting: ChatFormatting?
}

@GenerateCodec
data class PowderCostType(
    @FieldName("kind") val powderType: PowderType,
    override val amount: Int?,
) : CostType(CostTypes.POWDER) {
    override val displayName: Component = powderType.displayName
    override val formatting: ChatFormatting = powderType.formatting
}

@GenerateCodec
data class WhisperCostType(
    @FieldName("kind") val whisperType: WhisperType,
    override val amount: Int?,
) : CostType(CostTypes.WHISPER) {
    override val displayName: Component = whisperType.displayName
    override val formatting: ChatFormatting = whisperType.formatting
}

enum class PowderType(val formatting: ChatFormatting) {
    MITHRIL(ChatFormatting.DARK_GREEN),
    GEMSTONE(ChatFormatting.LIGHT_PURPLE),
    GLACITE(ChatFormatting.AQUA),
    ;

    val displayName = Text.of(name.toTitleCase()) {
        append(" Powder")
        withStyle(formatting)
    }
}

enum class WhisperType(val formatting: ChatFormatting) {
    FOREST(ChatFormatting.DARK_AQUA),
    ;

    val displayName = Text.of(name.toTitleCase()) {
        append(" Whisper")
        withStyle(formatting)
    }
}
