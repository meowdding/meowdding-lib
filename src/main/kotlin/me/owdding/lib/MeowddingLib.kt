package me.owdding.lib

import me.owdding.lib.utils.HiddenElementRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader

object MeowddingLib : ClientModInitializer {

    override fun onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("resourcefulconfig")) {
            HiddenElementRenderer.register()
        }
    }
}
