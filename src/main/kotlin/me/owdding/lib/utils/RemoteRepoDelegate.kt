package me.owdding.lib.utils

import com.mojang.serialization.Codec
import me.owdding.ktmodules.Module
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.lib.events.StartRepoLoadingEvent
import me.owdding.repo.RemoteRepo
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

class RemoteRepoDelegate<T : Any, R : T?> private constructor(
    private val path: String,
    private val logger: MeowddingLogger,
    private val default: R?,
    private val codec: Codec<T>,
) {
    private val cachedValue: AtomicReference<T> = AtomicReference()

    init {
        instances.add(this)
        // we set the value on initialization in case the delegate gets created
        // *after* the repo gets initialized
        if (RemoteRepo.isInitialized()) update()
    }

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): R = (cachedValue.get() ?: default) as R

    private fun update() = logger.runCatching("Loading $path from remote repo!") {
        cachedValue.set(RemoteRepo.getFileContentAsJson("$path.json").toDataOrThrow(codec))
        logger.debug("Loaded $path from remote repo!")
    }
    private fun unset() = cachedValue.setRelease(null)

    @Module
    companion object {
        private val instances: MutableSet<RemoteRepoDelegate<*, *>> = CopyOnWriteArraySet()

        context(logger: MeowddingLogger)
        fun <T : Any> load(
            path: String,
            codec: Codec<T>,
        ): RemoteRepoDelegate<T, T?> = RemoteRepoDelegate(path, logger, null, codec)

        context(logger: MeowddingLogger)
        fun <T : Any> load(
            path: String,
            default: T,
            codec: Codec<T>,
        ): RemoteRepoDelegate<T, T> = RemoteRepoDelegate(path, logger, default, codec)


        @Subscription(StartRepoLoadingEvent::class)
        private fun onRepoUnload() = instances.forEach(RemoteRepoDelegate<*, *>::unset)

        @Subscription(FinishRepoLoadingEvent::class)
        private fun onRepoLoad() = instances.forEach(RemoteRepoDelegate<*, *>::update)
    }
}

