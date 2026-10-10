package me.owdding.lib.utils.mod.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import me.owdding.ktmodules.Module
import me.owdding.lib.utils.mod.MeowddingMod
import org.apache.commons.io.FileUtils
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.hypixel.FreshHypixelAlphaDetectedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.HypixelJoinEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJsonOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.json.JsonObject
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.io.path.*

class MeowddingStorageData<T : Any> internal constructor(
    private val version: Int = 0,
    private val mod: MeowddingMod,
    private val defaultData: () -> T,
    fileName: String,
    private val codec: (Int) -> Codec<T>,
    private val differentAlphaData: Boolean,
) {

    fun get(): T = if (shouldUseAlphaData()) getOrCreateAlphaData() else getNormalData()

    fun set(value: T) {
        if (shouldUseAlphaData()) this.alphaData = value
        else this.data = value
        save()
    }

    fun save() {
        if (shouldUseAlphaData()) requiresAlphaSave.add(this)
        else requiresSave.add(this)
    }

    fun delete() {
        deletePath(path)
        this.data = defaultData()
        deletePath(alphaPath)
        alphaData = null
    }

    @JvmName("editBoolean")
    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    inline fun edit(edit: T.() -> Boolean) {
        contract {
            callsInPlace(edit, InvocationKind.EXACTLY_ONCE)
        }
        val data = get()
        if (edit(data)) save()
    }

    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    inline fun edit(edit: T.() -> Unit?) {
        contract {
            callsInPlace(edit, InvocationKind.EXACTLY_ONCE)
        }
        val data = get()
        if (edit(data) != null) save()
    }

    init {
        allStorageDatas.add(this)
    }

    @Module
    internal companion object {
        val allStorageDatas = mutableListOf<MeowddingStorageData<*>>()
        val requiresSave = mutableSetOf<MeowddingStorageData<*>>()
        val requiresAlphaSave = mutableSetOf<MeowddingStorageData<*>>()

        inline fun <reified T : Any> clearAndRun(collection: MutableCollection<T>, crossinline block: (T) -> Unit) {
            val copy = collection.toTypedArray<T>()
            collection.clear()
            if (copy.isEmpty()) return
            CompletableFuture.runAsync {
                copy.forEach(block)
            }
        }

        private var firstJoin = false

        @Subscription(HypixelJoinEvent::class)
        private fun onHypixelJoin() {
            if (firstJoin) return
            firstJoin = true
            Scheduling.async { allStorageDatas.forEach { it.get() } } // load all data async
        }

        @Subscription(FreshHypixelAlphaDetectedEvent::class)
        private fun onNewAlpha() {
            allStorageDatas.forEach { it.deleteAlpha() }
        }

        @Subscription(TickEvent::class)
        @TimePassed("5s")
        private fun onTick() {
            clearAndRun(requiresSave) { it.saveToSystem() }
            clearAndRun(requiresAlphaSave) { it.saveAlphaToSystem() }
        }
    }

    private val fileName = "${fileName.removePrefix(".json")}.json"

    private val defaultPath get() = mod.storagePath
    private val defaultAlphaPath get() = defaultPath.resolve("alpha")

    private val path: Path = defaultPath.resolve(this.fileName)
    private val alphaPath: Path = defaultAlphaPath.resolve(this.fileName)

    private var data: T? = null
    private var alphaData: T? = null

    private fun shouldUseAlphaData() = differentAlphaData && LocationAPI.onAlpha

    private fun copyData(): T {
        val data = getNormalData()
        mod.debug("Copying data from ${path.relativeTo(defaultPath)} for alpha data")
        try {
            // we convert to json and then back to make a new copy of the data and not just a reference to it
            return data.toJsonOrThrow(currentCodec).toDataOrThrow(currentCodec)
        } catch (e: Exception) {
            mod.error("Failed to copy $data to alphaData ", e)
            return defaultData()
        }
    }

    private fun getNormalData(): T {
        if (data == null) data = loadData(path, defaultData)
        return data!!
    }

    private fun getOrCreateAlphaData(): T {
        var alphaData = alphaData
        if (alphaData == null) {
            // we use the current normal data as a default for alpha data
            alphaData = loadData(alphaPath, ::copyData)
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
            var readJson: JsonObject? = null
            try {
                readJson = JsonParser.parseString(path.readText()) as JsonObject
                val version = readJson.get("@${mod.MOD_ID}:version").asInt
                var data = readJson.get("@${mod.MOD_ID}:data")
                for (version in version until this.version) {
                    data = data.toDataOrThrow(codec(version)).toJsonOrThrow(codec(version))
                }
                val codec = codec(version)
                newData = data.toDataOrThrow(codec)
            } catch (e: Exception) {
                mod.error("Failed to load ${path.relativeTo(defaultPath)} (json: $readJson)", e)
                newData = default()
            }
            return newData
        }
    }

    private val currentCodec = codec(version)

    private fun deletePath(path: Path) {
        try {
            path.deleteIfExists()
        } catch (e: Exception) {
            mod.error("Failed to delete ${path.relativeTo(defaultPath)}", e)
        }
    }

    private fun deleteAlpha() {
        this.alphaData = null
        deletePath(alphaPath)
    }

    private fun saveToSystem() {
        val data = data ?: return
        savePath(data, path)
    }

    private fun savePath(data: T, path: Path) {
        try {
            val version = this.version
            val json = JsonObject {
                this["@${mod.MOD_ID}:version"] = version
                this["@${mod.MOD_ID}:data"] = data.toJson(currentCodec) ?: return mod.warn("Failed to encode $data to json")
            }
            FileUtils.write(path.toFile(), json.toPrettyString(), Charsets.UTF_8)
            mod.debug("saved ${path.relativeTo(defaultPath)}")
        } catch (e: Exception) {
            mod.error("Failed to save $data to ${path.relativeTo(defaultPath)}", e)
        }
    }
    private fun saveAlphaToSystem() {
        val alphaData = alphaData ?: return
        savePath(alphaData, alphaPath)
    }
}
