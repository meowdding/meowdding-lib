package me.owdding.lib

import com.mojang.serialization.Codec
import me.owdding.ktmodules.AutoCollect
import me.owdding.ktmodules.Module
import me.owdding.lib.compat.HiddenElementRenderer
import me.owdding.lib.compat.meowdding.MeowddingConfigTranslationChecker
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.lib.events.MeowddingLibRegisterCommandsEvent
import me.owdding.lib.events.StartRepoLoadingEvent
import me.owdding.lib.generated.*
import me.owdding.lib.utils.mod.MeowddingMod
import me.owdding.lib.utils.sendWithPrefix
import me.owdding.lib.utils.unsafeCast
import me.owdding.repo.RemoteRepo
import org.spongepowered.asm.mixin.MixinEnvironment
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KType

@Module
object MeowddingLib : MeowddingMod("meowdding-lib", loggerName = "MeowddingLib") {
    private var notifyAboutRepoLoad = false

    init {
        if (McClient.isDev) { // Ensures all mixins always apply while in dev
            MixinEnvironment.getCurrentEnvironment().audit()
        }
        registerEvents(MeowddingLibPreInitModules.collected)
    }

    override fun onInitializeClient() {
        HiddenElementRenderer.register()

        registerEvents(MeowddingLibModules.collected)
        if (McClient.isDev) registerEvents(MeowddingLibDevModules.collected)
        MeowddingConfigTranslationChecker.addModToWarn(MOD_ID)
    }

    override fun postInit() {
        loadRepo()
        registerEvents(MeowddingLibPostInitModules.collected)
    }

    override fun <T : Any> getCodec(type: KType): Codec<T> = MeowddingLibCodecs.getCodec(type).unsafeCast()

    //? if < 26.4 {
    @Deprecated("Use getCodec(KType) instead. This will be removed on 26.4")
    override fun <T : Any> getCodec(clazz: Class<T>): Codec<T> = MeowddingLibCodecs.getCodec(clazz).unsafeCast()
    //?}

    private fun finishRepoLoadingEvent() = McClient.runNextTick {
        debug("Finished loading repo!")
        FinishRepoLoadingEvent.post(SkyBlockAPI.eventBus)
        if (notifyAboutRepoLoad) {
            Text.of("Reloaded repo!", TextColor.PINK).sendWithPrefix()
            notifyAboutRepoLoad = false
        }
    }

    private fun loadRepo() {
        debug("Start loading repo")
        StartRepoLoadingEvent.post(SkyBlockAPI.eventBus)
        CompletableFuture.runAsync {
            RemoteRepo.initialize(McClient.config.resolveSibling("meowdding-repo-cache"), callback = ::finishRepoLoadingEvent)
        }
    }

    @Subscription
    private fun onRegisterCommands(event: RegisterCommandsEvent) {
        MeowddingLibRegisterCommandsEvent(event).post(SkyBlockAPI.eventBus)
    }

    @Subscription
    internal fun command(event: MeowddingLibRegisterCommandsEvent) {
        event.register("dev repo") {
            then("reload") {
                callback {
                    Text.of("Reloading Repo!", TextColor.GREEN).sendWithPrefix()
                    RemoteRepo.invalidate()
                    notifyAboutRepoLoad = true
                    loadRepo()
                }
                thenCallback("local") {
                    if (!RemoteRepo.isInitialized()) {
                        Text.of("Remote Repo hasn't been initialized yet!", TextColor.RED).sendWithPrefix()
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
                    color = TextColor.GRAY
                    if (RemoteRepo.forceBackupRepo) append("Enabled", TextColor.GREEN)
                    else append("Disabled", TextColor.RED)
                }.sendWithPrefix()
                notifyAboutRepoLoad = true
                loadRepo()
            }
        }
    }
}

@AutoCollect("PreInitModules")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
internal annotation class PreInitModule

@AutoCollect("PostInitModules")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
internal annotation class PostInitModule

@AutoCollect("DevModules")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
internal annotation class DevModule
