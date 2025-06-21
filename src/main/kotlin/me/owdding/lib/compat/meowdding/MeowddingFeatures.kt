package me.owdding.lib.compat.meowdding

import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfigElement
import com.teamresourceful.resourcefulconfig.api.types.elements.ResourcefulConfigEntryElement
import com.teamresourceful.resourcefulconfig.api.types.elements.ResourcefulConfigObjectEntryElement
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

    private fun ResourcefulConfig.getConfig(): Set<String> = buildSet {
        elements().forEach { addAll(it.getName()) }
        categories().filterNot { it.value.info().isHidden }.forEach { addAll(it.value.getConfig()) }
    }

    private fun ResourcefulConfigElement.getName(): List<String> {
        if (isHidden) return emptyList()
        return when (this) {
            is ResourcefulConfigObjectEntryElement -> entry().elements().filterNot { it.isHidden }.flatMap { it.getName() }
            is ResourcefulConfigEntryElement -> listOf(entry().options().title().toLocalizedString())
            else -> emptyList()
        }
    }
}
