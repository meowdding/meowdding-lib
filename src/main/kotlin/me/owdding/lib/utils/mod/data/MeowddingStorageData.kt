package me.owdding.lib.utils.mod.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import me.owdding.ktmodules.Module
import me.owdding.lib.utils.mod.MeowddingMod
import org.apache.commons.io.FileUtils
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJsonOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.json.JsonObject
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.relativeTo

class MeowddingStorageData<T : Any> internal constructor(
    private val version: Int = 0,
    private val mod: MeowddingMod,
    defaultData: () -> T,
    fileName: String,
    private val codec: (Int) -> Codec<T>,
    private val differentAlphaData: Boolean,
) {

    fun get(): T = if (shouldUseAlphaData()) getAlphaData() else data

    fun save() {
        if (shouldUseAlphaData()) requiresAlphaSave.add(this)
        else requiresSave.add(this)
    }

    fun delete() {
        deletePath(path)
        deletePath(alphaPath)
    }

    init {
        allStorageDatas.add(this)
    }

    @Module
    internal companion object {
        val allStorageDatas = mutableSetOf<MeowddingStorageData<*>>()
        val requiresSave = mutableSetOf<MeowddingStorageData<*>>()
        val requiresAlphaSave = mutableSetOf<MeowddingStorageData<*>>()

        private val newAlphaRegex = "^Welcome to Hypixel SkyBlock on the Alpha Network!".toRegex()

        inline fun <reified T : Any> clearAndRun(collection: MutableCollection<T>, crossinline block: (T) -> Unit) {
            val copy = collection.toTypedArray<T>()
            collection.clear()
            if (copy.isEmpty()) return
            CompletableFuture.runAsync {
                copy.forEach(block)
            }
        }

        @Subscription
        fun onChatReceived(event: ChatReceivedEvent.Pre) {
            if (!newAlphaRegex.contains(event.text)) return
            allStorageDatas.forEach { it.deleteAlpha() }
        }

        @Subscription(TickEvent::class)
        @TimePassed("5s")
        fun onTick() {
            clearAndRun(requiresSave) { it.saveToSystem() }
            clearAndRun(requiresAlphaSave) { it.saveAlphaToSystem() }
        }
    }

    private val fileName = "${fileName.removePrefix(".json")}.json"
    private val path: Path = mod.storagePath.resolve(this.fileName)
    private val alphaPath: Path = mod.storagePath.resolve("alpha").resolve(this.fileName)

    private var data: T
    private var alphaData: T? = null

    private fun shouldUseAlphaData() = differentAlphaData && LocationAPI.onAlpha

    private fun getAlphaData(): T {
        var alphaData = alphaData
        if (alphaData == null) {
            // we use the current normal data as a default for alpha data
            alphaData = loadData(alphaPath) { data }
            this.alphaData = alphaData
        }
        return alphaData
    }

    private fun loadData(path: Path, default: () -> T): T {
        if (!path.exists()) {
            path.createParentDirectories()
            return default()
        } else {
            var newData: T
            try {
                val readJson = JsonParser.parseString(path.readText()) as JsonObject
                val version = readJson.get("@${mod.MOD_ID}:version").asInt
                var data = readJson.get("@${mod.MOD_ID}:data")
                for (version in version until this.version) {
                    data = data.toDataOrThrow(codec(version)).toJsonOrThrow(codec(version))
                }
                val codec = codec(version)
                newData = data.toDataOrThrow(codec)
            } catch (e: Exception) {
                mod.error("Failed to load ${path.relativeTo(mod.storagePath)}.", e)
                newData = default()
            }
            return newData
        }
    }

    init {
        this.data = loadData(path, defaultData)
    }

    private val currentCodec = codec(version)

    private fun deletePath(path: Path) {
        try {
            path.deleteIfExists()
        } catch (e: Exception) {
            mod.error("Failed to delete $path", e)
        }
    }

    private fun deleteAlpha() {
        if (!shouldUseAlphaData()) return
        this.alphaData = null
        deletePath(alphaPath)
    }

    private fun saveToSystem() {
        savePath(data, path)
    }

    private fun savePath(data: T, path: Path) {
        mod.debug("Saving $path")
        try {
            val version = this.version
            val json = JsonObject {
                this["@${mod.MOD_ID}:version"] = version
                this["@${mod.MOD_ID}:data"] = data.toJson(currentCodec) ?: return mod.warn("Failed to encode $data to json")
            }
            FileUtils.write(path.toFile(), json.toPrettyString(), Charsets.UTF_8)
            mod.debug("saved $path")
        } catch (e: Exception) {
            mod.error("Failed to save $data to file", e)
        }
    }
    private fun saveAlphaToSystem() {
        val alphaData = alphaData ?: return
        savePath(alphaData, alphaPath)
    }
}
