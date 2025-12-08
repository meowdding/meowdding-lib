package me.owdding.lib.dev

import me.owdding.ktmodules.Module
import me.owdding.lib.generated.CodecUtils
import me.owdding.lib.generated.MeowddingLibCodecs
import me.owdding.lib.repo.EnchantmentRepoData
import me.owdding.lib.repo.EnchantmentRepoData.PassthroughRequirement
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.contains
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseRomanOrArabic
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick

@Module
object EnchantmentGuideParser {

    private val maxLevelOverwrites = mapOf(
        "Ender Slayer" to 7,
        "Smite" to 7,
    )

    var enabled = false
    private val enchantments = mutableSetOf<EnchantmentRepoData.ParsedEnchantmentData>()


    private data class LoreReader(val content: List<String>) {
        private var index = 0

        fun canRead(): Boolean = index < content.size
        fun peek(): String = content[index]
        fun read(): String = content[index++]
        fun skip() = index++
        fun reset() {
            index = 0
        }

        val isTitle: Boolean
            get() = this.canRead() && !this.peek().startsWith(" ")
    }

    @Subscription
    fun itemChangeEvent(event: InventoryChangeEvent) {
        if (!enabled) return
        if (!event.title.endsWith("Enchantments Guide")) return
        if (!event.isInMainPart) return
        if (event.item !in Items.ENCHANTED_BOOK) return

        val lore = LoreReader(event.item.getRawLore())
        while (lore.canRead() && lore.peek() != "") lore.skip()
        lore.skip()

        val map: MutableMap<String, List<String>> = mutableMapOf()

        while (lore.canRead()) {
            if (!lore.isTitle) lore.skip()
            val title = lore.read()
            val list = mutableListOf<String>()
            while (lore.canRead() && lore.peek().startsWith(" - ")) {
                list.add(lore.read())
            }
            map[title] = list
            while (lore.canRead() && lore.peek().isBlank()) lore.skip()
        }

        val name = event.item.cleanName.substringBeforeLast(" ")
        val maxLevel = event.item.cleanName.removePrefix(name).trim().toInt()
        val sources = parseSources(map.getOrDefault("Sources:", map.getOrDefault("Source:", emptyList())))
        if (name == "Smite") {
            sources.removeIf { (_, min, max) -> min == max && max == 7 }
            sources.add(EnchantmentRepoData.EnchantmentSource("Severed Hand", 7, 7))
        }

        val minLevel = sources.minOf { it.min }

        enchantments.add(
            EnchantmentRepoData.ParsedEnchantmentData(
                name,
                minLevel,
                maxLevelOverwrites[name] ?: maxLevel,
                event.item.getRawLore().contains("You can only have 1 Ultimate"),
                sources,
                map.getOrDefault("Conflicts:", emptyList()).removeListElements(),
                map.getOrDefault("Applied To:", emptyList()).removeListElements(),
                map.getOrDefault("Requirements:", emptyList()).removeListElements().map(::PassthroughRequirement),
            ),
        )
    }

    private fun List<String>.removeListElements() = this.map { it.removePrefix(" -").trim() }

    private val sourceRegex = Regex(" - (?<name>.*?) \\((?:(?<min>[IVX]+)-)?(?<max>[IVX]+)\\)")
    private fun parseSources(sources: List<String>): MutableList<EnchantmentRepoData.EnchantmentSource> = buildList {
        sources.forEach {
            val match = sourceRegex.matchEntire(it) ?: return@forEach
            val name = match.groups["name"]!!.value
            val max = match.groups["max"]!!.value.parseRomanOrArabic()
            val min = match.groups["min"]?.value?.parseRomanOrArabic() ?: max
            add(EnchantmentRepoData.EnchantmentSource(name, min, max))
        }
    }.toMutableList()

    @Subscription
    fun commandRegisterEvent(event: RegisterCommandsEvent) {
        event.registerWithCallback("meowdding dev serialize_enchantments") {
            Text.of("Click to copy max enchantments!") {
                onClick { McClient.clipboard = enchantments.toJson(CodecUtils.mutableSet(MeowddingLibCodecs.getCodec())).toPrettyString() }
            }.send()
        }
        event.registerWithCallback("meowdding dev toggle enchantment_parser") {
            enabled = !enabled
            Text.of {
                if (enabled) append("Enabled") { color = TextColor.GREEN }
                else append("Disabled") { color = TextColor.RED }
                append(" enchantment parser!")
            }.send()
        }
    }

}
