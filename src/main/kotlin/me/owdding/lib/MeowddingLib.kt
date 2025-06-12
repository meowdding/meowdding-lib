package me.owdding.lib

import me.owdding.lib.generated.MeowddingLibModules
import me.owdding.lib.utils.HiddenElementRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

object MeowddingLib : ClientModInitializer {

    override fun onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("resourcefulconfig")) {
            HiddenElementRenderer.register()
        }

        MeowddingLibModules.init { SkyBlockAPI.eventBus.register(it) }
    }

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath("meowdding-lib", path)
}
