package me.owdding.lib.utils

import com.google.gson.JsonArray
import kotlinx.coroutines.runBlocking
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.Version
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.http.Http
import java.util.concurrent.CompletableFuture

private const val URL = "https://api.modrinth.com/v2/project/%project%/version"

class MeowddingUpdateChecker(val projectSlug: String, val modContainer: ModContainer, val callback: UpdateCallback) {

    init {
        SkyBlockAPI.eventBus.register(this)
    }

    private var firstLoad = true

    @Subscription(event = [ServerChangeEvent::class])
    fun onJoinHypixel() {
        if (!firstLoad) return
        firstLoad = false
        CompletableFuture.runAsync {
            runBlocking {
                checkForUpdates()
            }
        }
    }

    private suspend fun checkForUpdates() {
        val mcVersion = McClient.version
        val currentVersion = modContainer.metadata.version

        Http.getResult<JsonArray>(URL.replace("%project%", projectSlug)).onSuccess { value ->
            val versionsForMc = value.filter { it.asJsonObject.getAsJsonArray("game_versions").any { version -> version.asString == mcVersion } }
            var nextVersion: Pair<String, Version>? = null

            for (versionEntry in versionsForMc) {
                val version = runCatching {
                    Version.parse(versionEntry.asJsonObject.get("version_number").asString)
                }.getOrNull() ?: continue

                if (currentVersion < version && (nextVersion == null || nextVersion.second < version)) {
                    nextVersion = versionEntry.asJsonObject.get("id").asString to version
                }
            }

            if (nextVersion != null) {
                callback(
                    "https://modrinth.com/mod/$projectSlug/version/${nextVersion.first}",
                    currentVersion.friendlyString,
                    nextVersion.second.friendlyString,
                )
            }
        }
    }
}

fun interface UpdateCallback {
    fun accept(link: String, current: String, new: String)
    operator fun invoke(link: String, current: String, new: String) = accept(link, current, new)
}
