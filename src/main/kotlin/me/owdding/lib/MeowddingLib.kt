package me.owdding.lib

import me.owdding.ktmodules.AutoCollect
import me.owdding.ktmodules.Module
import me.owdding.lib.compat.HiddenElementRenderer
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.lib.events.StartRepoLoadingEvent
import me.owdding.lib.generated.MeowddingLibModules
import me.owdding.lib.generated.MeowddingLibPreInitModules
import me.owdding.lib.utils.MeowddingLogger
import me.owdding.repo.RemoteRepo
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import java.util.concurrent.CompletableFuture

@Module
object MeowddingLib : ClientModInitializer, MeowddingLogger by MeowddingLogger.autoResolve() {

    const val MOD_ID = "meowdding-lib"
    private var notifyAboutRepoLoad = false

    init {
        MeowddingLibPreInitModules.init { SkyBlockAPI.eventBus.register(it) }
    }

    override fun onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("resourcefulconfig")) {
            HiddenElementRenderer.register()
        }

        MeowddingLibModules.init { SkyBlockAPI.eventBus.register(it) }
        loadRepo()
    }

    private fun finishRepoLoadingEvent() = McClient.runNextTick {
        FinishRepoLoadingEvent.post(SkyBlockAPI.eventBus)
        if (notifyAboutRepoLoad) {
            Text.of("Reloaded repo!").send()
            notifyAboutRepoLoad = false
        }
    }

    private fun loadRepo() {
        StartRepoLoadingEvent.post(SkyBlockAPI.eventBus)
        CompletableFuture.runAsync {
            RemoteRepo.initialize(FabricLoader.getInstance().configDir.resolveSibling("meowdding-repo-cache"), callback = ::finishRepoLoadingEvent)
        }
    }

    @Subscription
    fun command(event: RegisterCommandsEvent) {
        event.registerWithCallback("meowdding dev reload_repo") {
            RemoteRepo.invalidate()
            notifyAboutRepoLoad = true
            loadRepo()
        }
    }

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
}

@AutoCollect("PreInitModules")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class PreInitModule
