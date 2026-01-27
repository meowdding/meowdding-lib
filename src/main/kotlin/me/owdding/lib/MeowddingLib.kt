package me.owdding.lib

import com.mojang.serialization.Codec
import me.owdding.ktmodules.AutoCollect
import me.owdding.ktmodules.Module
import me.owdding.lib.compat.HiddenElementRenderer
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.lib.events.StartRepoLoadingEvent
import me.owdding.lib.generated.MeowddingLibCodecs
import me.owdding.lib.generated.MeowddingLibModules
import me.owdding.lib.generated.MeowddingLibPreInitModules
import me.owdding.lib.utils.MeowddingLogger
import me.owdding.lib.utils.mod.MeowddingMod
import me.owdding.lib.utils.unsafeCast
import me.owdding.repo.RemoteRepo
import net.fabricmc.loader.api.FabricLoader
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import java.util.concurrent.CompletableFuture

@Module
object MeowddingLib : MeowddingMod("meowdding-lib") {
    private var notifyAboutRepoLoad = false

    init {
        registerEvents(MeowddingLibPreInitModules.collected)
    }

    override fun onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("resourcefulconfig")) {
            HiddenElementRenderer.register()
        }

        registerEvents(MeowddingLibModules.collected)
        loadRepo()
    }

    override fun <T : Any> getCodec(clazz: Class<T>): Codec<T> = MeowddingLibCodecs.getCodec(clazz).unsafeCast()

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
            RemoteRepo.initialize(McClient.config.resolveSibling("meowdding-repo-cache"), callback = ::finishRepoLoadingEvent)
        }
    }

    @Subscription
    fun command(event: RegisterCommandsEvent) {
        event.register("meowdding dev repo") {
            then("reload") {
                callback {
                    RemoteRepo.invalidate()
                    notifyAboutRepoLoad = true
                    loadRepo()
                }
                thenCallback("local") {
                    if (!RemoteRepo.isInitialized()) {
                        Text.of("Remote Repo hasn't been initialized yet!").send()
                        return@thenCallback
                    }
                    notifyAboutRepoLoad = true
                    CompletableFuture.runAsync {
                        finishRepoLoadingEvent()
                    }
                }
            }
            thenCallback("backup") {
                RemoteRepo.uninitialize()
                RemoteRepo.forceBackupRepo = !RemoteRepo.forceBackupRepo
                Text.of("Toggled backup repo to: ") {
                    if (RemoteRepo.forceBackupRepo) append("Enabled", TextColor.GREEN)
                    else append("Disabled", TextColor.RED)
                }
                notifyAboutRepoLoad = true
                loadRepo()
            }
        }
    }
}

@AutoCollect("PreInitModules")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class PreInitModule
