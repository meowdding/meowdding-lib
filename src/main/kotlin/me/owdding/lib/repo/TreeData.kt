package me.owdding.lib.repo

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.notkamui.keval.KevalBuilder
import com.notkamui.keval.keval
import eu.pb4.placeholders.api.ParserContext
import eu.pb4.placeholders.api.parsers.TagParser
import me.owdding.ktcodecs.*
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import me.owdding.lib.PreInitModule
import me.owdding.lib.generated.DispatchHelper
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import org.joml.Vector2i
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.extentions.toTitleCase
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.reflect.KClass

@GenerateDispatchCodec(TreeNode::class)
public enum class TreeNodes(override val type: KClass<out TreeNode>) : DispatchHelper<TreeNode> {
    PERK(LevelingTreeNode::class),
    UNLEVELABLE(UnlevelableTreeNode::class),
    ABILITY(AbilityTreeNode::class),
    CORE(CoreTreeNode::class),
    TIER(TierNode::class),
    SPACER(SpacerNode::class),
    ;

    @Module
    public companion object {
        @IncludedCodec(named = "vec_2i")
        public val VECTOR_2I: Codec<Vector2i> = Codec.INT.listOf(2, 2).xmap(
            { Vector2i(it[0], it[1]) },
            { listOf(it.x, it.y) },
        )

        @IncludedCodec(named = "reward_formula")
        public val rewardFormulaCodec: Codec<Map<String, String>> = Codec.either(
            Codec.STRING.xmap({ mapOf("reward" to it) }, { it["reward"] }),
            Codec.unboundedMap(Codec.STRING, Codec.STRING) as Codec<Map<String, String>>,
        ).xmap(
            { Either.unwrap(it) },
            { if (it.size == 1) Either.left(it) else Either.right(it) },
        )

        public fun getType(id: String): TreeNodes = valueOf(id.uppercase())
    }
}

@PreInitModule
public object TreeRepoData {
    public val hotm: List<TreeNode> by MeowddingLib.repoDelegate("mining/hotm", emptyList())
    public val hotf: List<TreeNode> by MeowddingLib.repoDelegate("foraging/hotf", emptyList())

    public fun hotmByName(name: String): TreeNode? = hotm.find { it.name == name }
    public fun hotfByName(name: String): TreeNode? = hotf.find { it.name == name }
}

public data class Context(val treeLevel: Int = -1, val perkLevel: Int = -1) {
    public fun configure(kevalBuilder: KevalBuilder): KevalBuilder = with(kevalBuilder) {
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
            value = treeLevel.coerceAtLeast(1).toDouble()
        }
        constant {
            name = "hotfLevel"
            value = treeLevel.coerceAtLeast(1).toDouble()
        }
        constant {
            name = "effectiveLevel"
            value = (perkLevel - 1).coerceAtLeast(0).toDouble()
        }
    }
}

public abstract class TreeNode(public val type: TreeNodes) {
    public abstract val name: String
    public abstract val id: String
    public abstract val location: Vector2i

    public abstract fun tooltip(context: Context): List<Component>
    public abstract fun isMaxed(level: Int): Boolean
}

public abstract class LevelableTooltipNode(type: TreeNodes) : TreeNode(type) {
    public abstract val rewards: Map<String, String>
    public abstract val tooltip: List<String>

    public fun evaluate(context: Context): Map<String, String> {
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
            TagParser.QUICK_TEXT_SAFE.parseComponent(
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


public abstract class LevelableTreeNode(type: TreeNodes) : LevelableTooltipNode(type) {
    public abstract val maxLevel: Int

    public abstract fun costForLevel(level: Int): Pair<CostType, Int>
    public abstract fun getPowderType(level: Int): CostType
}

@GenerateCodec
public data class UnlevelableTreeNode(
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
            TagParser.QUICK_TEXT_SAFE.parseComponent(it.replace("%reward%", replacement), ParserContext.of())
        }
    }

    override fun isMaxed(level: Int): Boolean = level > 0
}

@GenerateCodec
public data class LevelingTreeNode(
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

    override fun isMaxed(level: Int): Boolean = level >= maxLevel
}

@GenerateCodec
public data class AbilityTreeNode(
    override val id: String,
    override val name: String,
    @NamedCodec("vec_2i") override val location: Vector2i,
    @NamedCodec("reward_formula") @FieldName("reward_formula") override val rewards: Map<String, String>,
    override val tooltip: List<String>,
) : LevelableTooltipNode(TreeNodes.ABILITY) {
    override fun isMaxed(level: Int): Boolean = level == 3
}

@GenerateCodec
public data class CoreTreeNode(
    override val id: String,
    override val name: String,
    @NamedCodec("vec_2i") override val location: Vector2i,
    val level: List<CotmLevel>,
) : LevelableTreeNode(TreeNodes.CORE) {

    @GenerateCodec
    public data class CotmLevel(
        val cost: CostType = FreeCostType,
        val include: List<Int> = emptyList(),
        @Compact val reward: List<String>,
    ) {
        public fun tooltip(treeNode: CoreTreeNode): List<String> {
            val tooltip = mutableListOf<String>()

            include.map { treeNode.getLevel(it) }.flatMap { it.reward }.forEach { tooltip.add(it) }
            tooltip.addAll(reward)

            return tooltip
        }
    }

    public fun getLevel(level: Int): CotmLevel {
        return this.level[(level - 1).coerceAtLeast(0)]
    }

    override val rewards: Map<String, String>
        get() = emptyMap()
    override val tooltip: List<String>
        get() = emptyList()

    override fun tooltip(context: Context): List<Component> {
        return getLevel(context.perkLevel).tooltip(this).map {
            TagParser.QUICK_TEXT_SAFE.parseComponent(it, ParserContext.of())
        }
    }

    override fun isMaxed(level: Int): Boolean = level == maxLevel
    override val maxLevel: Int = level.size
    override fun costForLevel(level: Int): Pair<CostType, Int> = this.level[level - 1].cost.let { it to it.amount!! }
    override fun getPowderType(level: Int): CostType = this.level[level - 1].cost
}

@GenerateCodec
public data class TierNode(
    override val name: String,
    @NamedCodec("vec_2i") override val location: Vector2i,
    val rewards: List<String>,
) : TreeNode(TreeNodes.TIER) {
    override val id: String = ""
    override fun tooltip(context: Context): List<Component> = rewards.map {
        TagParser.QUICK_TEXT_SAFE.parseComponent(it, ParserContext.of())
    }

    override fun isMaxed(level: Int): Boolean = level >= (location.y + 1)
}

@GenerateCodec
public data class SpacerNode(
    @NamedCodec("vec_2i") override val location: Vector2i,
    @NamedCodec("vec_2i") val size: Vector2i,
) : TreeNode(TreeNodes.SPACER) {
    override val name: String = ""
    override val id: String = ""

    override fun tooltip(context: Context): List<Component> = emptyList()
    override fun isMaxed(level: Int): Boolean = false
}

@GenerateDispatchCodec(CostType::class)
public enum class CostTypes(override val type: KClass<out CostType>) : DispatchHelper<CostType> {
    POWDER(PowderCostType::class),
    WHISPER(WhisperCostType::class),
    FREE(FreeCostType::class),
    ;

    public companion object {
        public fun getType(id: String): CostTypes = CostTypes.valueOf(id.uppercase())
    }
}

public object FreeCostType : CostType(CostTypes.FREE) {
    override val amount: Int? = null
    override val displayName: Component? = null
    override val formatting: ChatFormatting? = null
    override val currency: SkillTreeCurrency? = null
}

public sealed class CostType(public val type: CostTypes) {
    public abstract val amount: Int?
    public abstract val displayName: Component?
    public abstract val formatting: ChatFormatting?
    public abstract val currency: SkillTreeCurrency?
}

@GenerateCodec
public data class PowderCostType(
    @FieldName("kind") val powderType: PowderType,
    override val amount: Int?,
) : CostType(CostTypes.POWDER) {
    override val displayName: Component = powderType.displayName
    override val formatting: ChatFormatting = powderType.formatting
    override val currency: SkillTreeCurrency = powderType
}

@GenerateCodec
public data class WhisperCostType(
    @FieldName("kind") val whisperType: WhisperType,
    override val amount: Int?,
) : CostType(CostTypes.WHISPER) {
    override val displayName: Component = whisperType.displayName
    override val formatting: ChatFormatting = whisperType.formatting
    override val currency: SkillTreeCurrency = whisperType
}

public interface SkillTreeCurrency {
    public val displayName: Component
}

public enum class PowderType(internal val formatting: ChatFormatting) : SkillTreeCurrency {
    MITHRIL(ChatFormatting.DARK_GREEN),
    GEMSTONE(ChatFormatting.LIGHT_PURPLE),
    GLACITE(ChatFormatting.AQUA),
    ;

    override val displayName: Component = Text.of(name.toTitleCase()) {
        append(" Powder")
        withStyle(formatting)
    }
}

public enum class WhisperType(internal val formatting: ChatFormatting) : SkillTreeCurrency {
    FOREST(ChatFormatting.DARK_AQUA),
    DESERT(ChatFormatting.GOLD),
    ;

    override val displayName: Component = Text.of(name.toTitleCase()) {
        append(" Whispers")
        withStyle(formatting)
    }
}
