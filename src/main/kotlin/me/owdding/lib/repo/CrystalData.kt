package me.owdding.lib.repo

import net.minecraft.ChatFormatting
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.toTitleCase
import tech.thatgravyboat.skyblockapi.utils.text.Text

enum class CrystalType(val formatting: ChatFormatting, val skyblockId: SkyBlockId) {
    JADE(ChatFormatting.GREEN, SkyBlockId.item("jade_crystal")),
    AMBER(ChatFormatting.GOLD, SkyBlockId.item("amber_crystal")),
    AMETHYST(ChatFormatting.DARK_PURPLE, SkyBlockId.item("amethyst_crystal")),
    SAPPHIRE(ChatFormatting.AQUA, SkyBlockId.item("sapphire_crystal")),
    TOPAZ(ChatFormatting.YELLOW, SkyBlockId.item("topaz_crystal")),
    JASPER(ChatFormatting.LIGHT_PURPLE, SkyBlockId.item("jasper_crystal")),
    RUBY(ChatFormatting.RED, SkyBlockId.item("ruby_crystal")),
    OPAL(ChatFormatting.WHITE, SkyBlockId.item("opal_crystal")),
    AQUAMARINE(ChatFormatting.DARK_BLUE, SkyBlockId.item("aquamarine_crystal")),
    PERIDOT(ChatFormatting.DARK_GREEN, SkyBlockId.item("peridot_crystal")),
    ONYX(ChatFormatting.DARK_GRAY, SkyBlockId.item("onyx_crystal")),
    CITRINE(ChatFormatting.DARK_RED, SkyBlockId.item("citrine_crystal"))
    ;

    val displayName = Text.of(name.toTitleCase()) {
        append(" Crystal")
        withStyle(formatting)
    }
}

enum class CrystalStatus {
    UNOBTAINED,
    OBTAINED,
    PLACED,
}
