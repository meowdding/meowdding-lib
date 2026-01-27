package me.owdding.lib.utils.mod.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import me.owdding.ktmodules.Module
import me.owdding.lib.utils.mod.MeowddingMod
import org.apache.commons.io.FileUtils
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJsonOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.json.JsonObject
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.CompletableFuture
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.relativeTo
import kotlin.io.path.writeText

class MeowddingStorageData<T : Any> internal constructor(
    private val version: Int = 0,
    private val mod: MeowddingMod,
    defaultData: () -> T,
    fileName: String,
    codec: (Int) -> Codec<T>,
) {

    fun get(): T = data

    fun save() {
        requiresSave.add(this)
    }

    @Module
    internal companion object {
        val requiresSave = mutableSetOf<MeowddingStorageData<*>>()

        @Subscription(TickEvent::class)
        @TimePassed("5s")
        fun onTick() {
            val toSave = requiresSave.toTypedArray()
            requiresSave.clear()
            CompletableFuture.runAsync {
                toSave.forEach {
                    it.saveToSystem()
                }
            }
        }
    }

    private val path: Path = mod.storagePath.resolve("${fileName.removePrefix(".json")}.json")

    private val data: T

    init {
        if (!path.exists()) {
            path.createParentDirectories()
            this.data = defaultData()
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
                newData = defaultData()
            }
            this.data = newData
        }
    }

    private val currentCodec = codec(version)

    fun delete() {
        try {
            path.deleteIfExists()
        } catch (e: Exception) {
            mod.error("Failed to delete $path", e)
        }
    }

    private fun saveToSystem() {
        mod.debug("Saving $path")
        try {
            val version = this.version
            val json = JsonObject {
                this["@${mod.MOD_ID}:version"] = version
                this["@${mod.MOD_ID}:data"] = data.toJson(currentCodec) ?: return mod.warn("Failed to encode $data to json")
            }
            path.writeText(json.toPrettyString(), Charsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
            FileUtils.write(path.toFile(), json.toPrettyString(), Charsets.UTF_8)
            mod.debug("saved $path")
        } catch (e: Exception) {
            mod.error("Failed to save $data to file", e)
        }
    }
}
