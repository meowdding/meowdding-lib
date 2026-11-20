package me.owdding.lib.cosmetics

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import earth.terrarium.olympus.client.images.ImageProvider
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktcodecs.Inline
import me.owdding.lib.MeowddingLib
import me.owdding.lib.PostInitModule
import me.owdding.lib.events.CosmeticLoadEvent
import me.owdding.lib.events.MeowddingLibRegisterCommandsEvent
import me.owdding.lib.generated.MeowddingLibCodecs
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

public const val COSMETIC_VERSION: Int = 1

@PostInitModule
public object CosmeticManager {

    public val cosmetics: Map<String, Cosmetic>
        field: MutableMap<String, Cosmetic> = ConcurrentHashMap()
    public val cosmeticList: Collection<Cosmetic> get() = cosmetics.values

    public val players: Map<UUID, PlayerData>
        field = ConcurrentHashMap<UUID, PlayerData>()
    public val playerList: Collection<PlayerData> get() = players.values

    @JvmStatic
    internal val imageProvider: ImageProvider<URI> get() = CosmeticImageProvider

    private var client: HttpClient = HttpClient.newBuilder()
        .connectTimeout(10.seconds.toJavaDuration())
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .build()

    init {
        updateCosmetics()
    }

    public fun updateCosmetics() {
        CompletableFuture.runAsync {
            runCatching {
                loadData()
            }.onFailure {
                MeowddingLib.error("Failed to load cosmetics!", it)
            }
        }
    }

    private fun loadData() {
        cosmetics.clear()
        players.clear()
        val response = client.send(
            HttpRequest.newBuilder(URI("https://cosmetics.meowdd.ing")).GET().build(),
            HttpResponse.BodyHandlers.ofString(Charsets.UTF_8),
        )
        if (response.statusCode() != 200) {
            MeowddingLib.error("Failed to fetch cosmetics! Status code: ${response.statusCode()}")
            MeowddingLib.error(response.body())
            return
        }
        val body = response.body().readJson<JsonObject>()
        try {
            body.toDataOrThrow<Unit>(
                RecordCodecBuilder.create {
                    it.group(
                        MeowddingLibCodecs.CompletablePlayerDataCodec.codec().listOf().fieldOf("players").forGetter { emptyList() },
                        MeowddingLibCodecs.CosmeticCodec.codec().listOf().fieldOf("cosmetics").forGetter { emptyList() },
                    ).apply(it) { players, cosmetics ->
                        val cosmetics = cosmetics.filter { (_, version) -> version >= COSMETIC_VERSION }
                        this.cosmetics.putAll(cosmetics.associateBy(Cosmetic::id))
                        this.players.putAll(players.map(CompletablePlayerData::complete).associateBy(PlayerData::uuid))
                    }
                },
            )
        } catch (e: Exception) {
            MeowddingLib.error("Failed to load cosmetics!", e)
            return
        }
        MeowddingLib.info("Fetched ${players.size} players and ${cosmetics.size} cosmetics")
        CosmeticLoadEvent.post(SkyBlockAPI.eventBus)
    }

    @IncludedCodec(named = "cosmetic_url_type")
    internal val COSMETIC_URL: Codec<URI> = Codec.STRING.flatXmap(
        { string ->
            runCatching {
                URI.create(string).takeUnless { it.host != "files.meowdd.ing" }
            }.getOrNull()?.let {
                DataResult.success(it)
            } ?: DataResult.error { "Invalid cosmetic url!" }
        },
        { DataResult.success(it.toString()) },
    )

    @GenerateCodec
    internal data class CompletablePlayerData(
        val uuid: UUID,
        @FieldName("extra_data") val extraData: JsonObject,
        val cosmetics: List<String>,
    ) {
        fun complete() = PlayerData(
            uuid,
            JsonObject().apply {
                extraData.entrySet().forEach { (key, value) -> add(key, value) }

                cosmetics.mapNotNull { CosmeticManager.cosmetics[it] }.filter { it.version <= COSMETIC_VERSION }.forEach {
                    it.data.entrySet().forEach { (key, value) ->
                        if (key == "version" || key == "id") return@forEach
                        if (has(key)) return@forEach
                        add(key, value)
                    }
                }
            },
        )
    }

    public data class PlayerData(
        val uuid: UUID,
        val data: JsonObject,
    )

    @GenerateCodec
    public data class Cosmetic(
        val id: String,
        val version: Int,
        @Inline val data: JsonObject,
    )

    @Subscription
    internal fun onCommand(event: MeowddingLibRegisterCommandsEvent) {
        event.register("dev cosmetics") {
            thenCallback("reload") {
                updateCosmetics()
            }
        }
    }
}
