package me.owdding.lib.utils.mod.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import me.owdding.ktmodules.Module
import me.owdding.lib.utils.mod.MeowddingMod
import org.apache.commons.io.FileUtils
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
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

class MeowddingProfileStorageData<T : Any> internal constructor(
    private val version: Int = 0,
    private val mod: MeowddingMod,
    private val defaultData: () -> T,
    val fileName: String,
    val codec: (Int) -> Codec<T>,
) {

    private fun isCurrentlyActive() = lastProfile != null && hasProfile() && currentProfile == lastProfile
    private val defaultPath get() = mod.storagePath

    private lateinit var data: T
    private lateinit var lastPath: Path
    private var lastProfile: String? = null

    fun get(): T? {
        if (isCurrentlyActive()) {
            return data
        }

        saveToSystem()
        load()

        return if (this::data.isInitialized) data else null
    }

    fun set(new: T) {
        if (isCurrentlyActive()) {
            data = new
            return
        }

        saveToSystem()
        load()
        if (this::data.isInitialized) {
            data = new
        }
    }

    fun save() {
        requiresSave.add(this)
    }

    fun load() {
        if (!hasProfile()) {
            return
        }

        lastProfile = currentProfile
        val lastProfile = lastProfile ?: return
        lastPath = defaultPath.resolve(McPlayer.uuid.toString())
            .resolve(lastProfile)
            .resolve("${fileName.removePrefix(".json")}.json")

        if (!lastPath.exists()) {
            lastPath.createParentDirectories()
            data = defaultData()
            saveToSystem()
            return
        }

        try {
            val readJson = JsonParser.parseString(lastPath.readText()) as JsonObject
            val version = readJson.get("@${mod.MOD_ID}:version").asInt
            val data = readJson.get("@${mod.MOD_ID}:data")
            val codec = codec(version)
            this.data = data.toDataOrThrow(codec)
        } catch (e: Exception) {
            mod.error("Failed to load ${lastPath.relativeTo(defaultPath)}.", e)
            this.data = defaultData()
            saveToSystem()
        }
    }

    private fun saveToSystem() {
        if (!this::data.isInitialized) return
        mod.debug("Saving $lastPath")
        try {
            val version = this.version
            val codec = this.codec(version)
            val json = JsonObject {
                this["@${mod.MOD_ID}:version"] = version
                this["@${mod.MOD_ID}:data"] = data.toJson(codec) ?: return mod.warn("Failed to encode $data to json")
            }
            lastPath.writeText(json.toPrettyString(), Charsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
            FileUtils.write(lastPath.toFile(), json.toPrettyString(), Charsets.UTF_8)
            mod.debug("saved $lastPath")
        } catch (e: Exception) {
            mod.error("Failed to save $data to file", e)
        }
    }

    @Module
    internal companion object {
        val requiresSave = mutableSetOf<MeowddingProfileStorageData<*>>()
        var currentProfile: String? = null

        @Subscription
        fun onProfileSwitch(event: ProfileChangeEvent) {
            currentProfile = event.name
        }

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

        private fun hasProfile() = currentProfile != null
    }
}
