package me.owdding.lib.utils.mod.data

import com.mojang.serialization.Codec
import me.owdding.lib.utils.mod.MeowddingMod
import kotlin.io.path.*

class MeowddingFolderStorageData<T : Any> internal constructor(
    private val version: Int = 0,
    private val mod: MeowddingMod,
    private val folderName: String,
    private val codec: (Int) -> Codec<T>,
) {

    private val storages = mutableMapOf<String, MeowddingStorageData<T>>()
    private val defaultPath get() = mod.storagePath

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
            )
        }.save()
    }

    fun get(id: String): T? = storages[id]?.get()

    fun remove(id: String) {
        storages.remove(id)?.delete()
    }

    private fun files() =
        defaultPath.apply { createDirectories() }.listDirectoryEntries("*.json").toList().filter { it.isRegularFile() && it.extension == "json" }

    internal fun getStorages() = storages
    fun getAll(): Map<String, T> = storages.mapValues { it.value.get() }

    fun refresh() {
        storages.clear()
        load()
    }
}
