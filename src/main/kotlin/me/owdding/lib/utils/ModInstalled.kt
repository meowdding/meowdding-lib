package me.owdding.lib.utils

import net.fabricmc.loader.api.FabricLoader
import kotlin.jvm.optionals.getOrNull

enum class KnownMods(val modId: String) {
    // Meowdding
    CUSTOM_SCOREBOARD("customscoreboard"),
    SKYCUBED("skycubed"),
    SKYBLOCK_PV("skyblockpv"),
    SKYOCEAN("skyocean"),

    // Other (potentially important for compat)
    SKYBLOCKER("skyblocker"),
    AARONS_MOD("aaron-mod"),
    SODIUM("sodium"),
    IRIS("iris"),
    ;

    val installed by lazy { FabricLoader.getInstance().isModLoaded(modId) }
    val version: String? by lazy { FabricLoader.getInstance().getModContainer(modId).getOrNull()?.metadata?.version?.friendlyString }
}
