package me.owdding.lib.utils.mod.data

import com.mojang.serialization.Codec
import me.owdding.ktmodules.Module
import me.owdding.lib.dev.alphaOverride
import me.owdding.lib.events.NewHypixelAlphaDetectedEvent
import me.owdding.lib.utils.mod.MeowddingMod
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import java.util.concurrent.CompletableFuture
import kotlin.io.path.*

// TODO: this needs to get finished
class MeowddingFolderStorageData<T : Any> internal constructor(
    private val version: Int = 0,
    private val mod: MeowddingMod,
    private val folderName: String,
    private val codec: (Int) -> Codec<T>,
    private val differentAlphaData: Boolean,
) {
    private fun shouldUseAlphaData() = differentAlphaData && alphaOverride.toBoolean(LocationAPI.onAlpha)
    fun fileName(id: String): String {
        return if (shouldUseAlphaData()) "$folderName/alpha/$id"
        else "$folderName/$id"
    }

    private val storages = mutableMapOf<String, MeowddingStorageData<T>>()
    private val defaultPath get() = mod.storagePath.resolve(folderName)
    private val alphaPath get() = defaultPath.resolve("alpha")

    init {
        load()
    }

    fun load() {
        this.storages.putAll(
            files().mapNotNull {
                val id = it.nameWithoutExtension
                try {
                    id to MeowddingStorageData(
                        version = version,
                        mod = mod,
                        defaultData = { throw IllegalStateException("No default data for folder storage!") },
                        fileName = "$folderName/$id",
                        codec = codec,
                        differentAlphaData = false,
                    )
                } catch (e: Exception) {
                    mod.error("Failed to load storage file: ${it.relativeTo(defaultPath)}", e)
                    null
                }
            },
        )
    }

    fun add(value: T) = set(value.hashCode().toString(), value)

    fun set(id: String, value: T) {
        storages.getOrPut(id) {
            MeowddingStorageData(
                version = version,
                mod = mod,
                defaultData = { value },
                fileName = "$folderName/$id",
                codec = codec,
                differentAlphaData = false
            )
        }.save()
    }

    fun get(id: String): T? = storages[id]?.get()

    fun remove(id: String) {
        val storage = storages.remove(id) ?: return
        storage.delete()
        MeowddingStorageData.allStorageDatas.remove(storage)
    }

    private fun files() =
        defaultPath.apply { createDirectories() }.listDirectoryEntries("*.json").toList().filter { it.isRegularFile() && it.extension == "json" }

    internal fun getStorages() = storages
    fun getAll(): Map<String, T> = storages.mapValues { it.value.get() }

    fun refresh() {
        MeowddingStorageData.allStorageDatas.removeAll(storages.values)
        storages.clear()
        load()
    }

    @Module
    internal companion object {
        val allStorageDatas = mutableListOf<MeowddingFolderStorageData<*>>()

        @Subscription(NewHypixelAlphaDetectedEvent::class)
        fun onNewAlpha() {
            allStorageDatas.forEach { it.deleteAlpha() }
        }

        @Subscription(TickEvent::class)
        @TimePassed("5s")
        fun onTick() {
            clearAndRun(requiresSave) { it.saveToSystem() }
            clearAndRun(requiresAlphaSave) { it.saveAlphaToSystem() }
        }
    }
}
