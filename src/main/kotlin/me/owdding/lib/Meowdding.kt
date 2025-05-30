package me.owdding.lib

import me.owdding.ktmodules.Module
import me.owdding.lib.generated.MeowddingLibModules
import net.fabricmc.api.ModInitializer
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

@Module
object Meowdding : ModInitializer {
    override fun onInitialize() {
        MeowddingLibModules.init { SkyBlockAPI.eventBus.register(it) }
    }
}
