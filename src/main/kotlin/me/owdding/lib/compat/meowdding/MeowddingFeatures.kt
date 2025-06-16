package me.owdding.lib.compat.meowdding

import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfigElement
import com.teamresourceful.resourcefulconfig.api.types.elements.ResourcefulConfigEntryElement
import com.teamresourceful.resourcefulconfig.common.config.Configurations
import me.owdding.ktmodules.Module

@Module
object MeowddingFeatures {
    val configurators: Map<MeowddingMod, ResourcefulConfig> by lazy {
        @Suppress("UnstableApiUsage", "UNCHECKED_CAST")
        MeowddingModsParser.mods.associateWith { mod ->
            Configurations.INSTANCE.modToConfigs[mod.modId]?.firstOrNull()?.let { Configurations.INSTANCE.configs[it] }
        }.filterValues { it != null } as Map<MeowddingMod, ResourcefulConfig>
    }

    val features by lazy {
        configurators.entries.associate { (key, value) -> key to value.getConfig() }
    }

    // todo: config obj dont work, obj keys are in there though (bad i think?)

    private fun ResourcefulConfig.getConfig(): List<String> = buildList {
        elements().forEach { it.getName()?.let { add(it) } }
        categories().forEach { addAll(it.value.getConfig()) }
    }

    private fun ResourcefulConfigElement.getName(): String? = when (this) {
        is ResourcefulConfigEntryElement -> entry().options().title().toLocalizedString()
        else -> null
    }
}
