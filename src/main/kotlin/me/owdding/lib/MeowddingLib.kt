package me.owdding.lib

import me.owdding.lib.compat.HiddenElementRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation

object MeowddingLib : ClientModInitializer {

    override fun onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("resourcefulconfig")) {
            HiddenElementRenderer.register()
        }
    }

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath("meowdding-lib", path)
}
