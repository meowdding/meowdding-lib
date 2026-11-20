package me.owdding.lib.utils.mod.data

import com.mojang.serialization.Codec
import me.owdding.lib.utils.mod.MeowddingMod
import kotlin.io.path.createDirectories
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.relativeTo

public class MeowddingFolderStorageData<T : Any> internal constructor(
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

    internal fun load() {
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

    public fun add(value: T): Unit = set(value.hashCode().toString(), value)

    public fun set(id: String, value: T) {
        storages.getOrPut(id) {
            MeowddingStorageData(
                version = version,
                mod = mod,
                defaultData = { value },
                fileName = "$folderName/$id",
                codec = codec,
                differentAlphaData = false,
            )
        }.set(value)
    }

    public fun get(id: String): T? = storages[id]?.get()

    public fun remove(id: String) {
        val storage = storages.remove(id) ?: return
        storage.delete()
        MeowddingStorageData.allStorageDatas.remove(storage)
    }

    private fun files() =
        defaultPath.apply { createDirectories() }.listDirectoryEntries("*.json").toList().filter { it.isRegularFile() && it.extension == "json" }

    internal fun getStorages() = storages
    public fun getAll(): Map<String, T> = storages.mapValues { it.value.get() }

    public fun refresh() {
        MeowddingStorageData.allStorageDatas.removeAll(storages.values)
        storages.clear()
        load()
    }
}
