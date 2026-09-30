package me.owdding.lib.utils.mod.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import me.owdding.ktmodules.Module
import me.owdding.lib.dev.alphaOverride
import me.owdding.lib.events.NewHypixelAlphaDetectedEvent
import me.owdding.lib.utils.mod.MeowddingMod
import org.apache.commons.io.FileUtils
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJsonOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.json.JsonObject
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.relativeTo
import kotlin.io.path.useDirectoryEntries

class MeowddingProfileStorageData<T : Any> internal constructor(
    private val version: Int = 0,
    private val mod: MeowddingMod,
    private val defaultData: () -> T,
    fileName: String,
    val codec: (Int) -> Codec<T>,
    private val differentAlphaData: Boolean,
) {

    init {
        allStorageDatas.add(this)
    }

    private val fileName = "${fileName.removePrefix(".json")}.json"

    private val defaultPath get() = mod.storagePath
    private val defaultAlphaPath get() = defaultPath.resolve("alpha")

    private fun isCurrentlyActive() = lastProfile != null && hasProfile() && currentProfile == lastProfile
    private fun shouldUseAlphaData() = differentAlphaData && alphaOverride.toBoolean(LocationAPI.onAlpha)

    private lateinit var data: T
    private var alphaData: T? = null

    private lateinit var lastPath: Path
    private lateinit var lastAlphaPath: Path
    private var lastProfile: String? = null

    fun get(): T? {
        if (!isCurrentlyActive()) {
            saveActiveToSystem()
            load()
        }

        return if (shouldUseAlphaData()) getOrCreateAlphaData()
        else if (::data.isInitialized) data
        else null
    }

    fun set(new: T) {
        if (!isCurrentlyActive()) {
            saveActiveToSystem()
            load()
        }

        if (shouldUseAlphaData()) alphaData = new
        else data = new
        save()
    }

    fun save() {
        if (shouldUseAlphaData()) requiresAlphaSave.add(this)
        else requiresSave.add(this)
    }

    private val currentCodec = codec(version)

    private fun copyData(): T {
        mod.debug("Copying data from $lastPath for alpha data")
        try {
            // we convert to json and then back to make a new copy of the data and not just a reference to it
            return data.toJsonOrThrow(currentCodec).toDataOrThrow(currentCodec)
        } catch (e: Exception) {
            mod.error("Failed to copy $data to alphaData ", e)
            return defaultData()
        }
    }

    private fun getOrCreateAlphaData(): T {
        var alphaData = alphaData
        if (alphaData == null) {
            alphaData = loadData(lastAlphaPath, ::copyData)
            this.alphaData = alphaData
        }
        return alphaData
    }

    fun load() {
        if (!hasProfile()) {
            return
        }

        lastProfile = currentProfile
        val profile = lastProfile ?: return
        val uuid = McPlayer.uuid.toString()

        lastPath = defaultPath.resolve(uuid)
            .resolve(profile)
            .resolve(fileName)

        lastAlphaPath = defaultAlphaPath.resolve(uuid)
            .resolve(profile)
            .resolve(fileName)

        data = loadData(lastPath, defaultData)

        alphaData = null // alpha data it only initialized if necessary
    }

    private fun loadData(path: Path, default: () -> T): T {
        if (!path.exists()) {
            path.createParentDirectories()
            return default()
        }

        return try {
            val readJson = JsonParser.parseString(path.readText()) as JsonObject
            val version = readJson.get("@${mod.MOD_ID}:version").asInt
            val data = readJson.get("@${mod.MOD_ID}:data")
            val codec = codec(version)

            data.toDataOrThrow(codec)
        } catch (e: Exception) {
            mod.error("Failed to load ${path.relativeTo(defaultPath)}.", e)
            default()
        }
    }

    private fun deleteAlpha() {
        val profilesPath = defaultAlphaPath.resolve(McPlayer.uuid.toString())
        if (!profilesPath.isDirectory()) return
        profilesPath.useDirectoryEntries { profileFolders ->
            profileFolders.filter { it.isDirectory() }.forEach { profilePath ->
                val file = profilePath.resolve(fileName)
                if (file.isRegularFile()) file.deleteIfExists()
            }
        }
    }


    private fun saveActiveToSystem() {
        if (shouldUseAlphaData()) saveAlphaToSystem()
        else saveToSystem()
    }

    private fun saveDataToPath(lastPath: Path, data: T) {
        mod.debug("Saving $lastPath")
        try {
            val version = this.version
            val codec = this.codec(version)
            val json = JsonObject {
                this["@${mod.MOD_ID}:version"] = version
                this["@${mod.MOD_ID}:data"] = data.toJson(codec) ?: return mod.warn("Failed to encode $data to json")
            }
            FileUtils.write(lastPath.toFile(), json.toPrettyString(), Charsets.UTF_8)
            mod.debug("saved $lastPath")
        } catch (e: Exception) {
            mod.error("Failed to save $data to file", e)
        }
    }

    private fun saveToSystem() {
        if (!this::data.isInitialized) return
        saveDataToPath(lastPath, data)
    }

    private fun saveAlphaToSystem() {
        val alphaData = alphaData ?: return
        saveDataToPath(lastAlphaPath, alphaData)
    }

    @Module
    internal companion object {
        val allStorageDatas = mutableSetOf<MeowddingProfileStorageData<*>>()
        val requiresSave = mutableSetOf<MeowddingProfileStorageData<*>>()
        val requiresAlphaSave = mutableSetOf<MeowddingProfileStorageData<*>>()
        var currentProfile: String? = null

        @Subscription
        fun onProfileSwitch(event: ProfileChangeEvent) {
            currentProfile = event.name
        }

        @Subscription(NewHypixelAlphaDetectedEvent::class)
        fun onNewAlpha() {
            allStorageDatas.forEach { it.deleteAlpha() }
        }


        @Subscription(TickEvent::class)
        @TimePassed("5s")
        fun onTick() {
            MeowddingStorageData.clearAndRun(requiresSave) { it.saveToSystem() }
            MeowddingStorageData.clearAndRun(requiresAlphaSave) { it.saveAlphaToSystem() }
        }

        private fun hasProfile() = currentProfile != null
    }
}
