package me.owdding.lib.cosmetics

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.ktcodecs.*
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import me.owdding.lib.extensions.associateNotNull
import me.owdding.lib.generated.MeowddingLibCodecs
import me.owdding.lib.utils.CosmeticImageProvider
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
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

const val COSMETIC_VERSION = 1

@Module
object CosmeticManager {

    private val _cosmetics: MutableMap<CosmeticId, Cosmetic> = ConcurrentHashMap()
    val cosmetics: Map<CosmeticId, Cosmetic> get() = _cosmetics
    val cosmeticList: Collection<Cosmetic> get() = _cosmetics.values

    private val _players = ConcurrentHashMap<UUID, PlayerData>()
    val players: Map<UUID, PlayerData> get() = _players
    val playerList: Collection<PlayerData> get() = _players.values

    private val _mlibCosmetics = ConcurrentHashMap<UUID, MlibCosmeticData>()
    val mlibCosmetics: Map<UUID, MlibCosmeticData> get() = _mlibCosmetics

    @JvmStatic
    val imageProvider get() = CosmeticImageProvider

    var client: HttpClient = HttpClient.newBuilder()
        .connectTimeout(10.seconds.toJavaDuration())
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .build()

    init {
        CompletableFuture.runAsync { loadData() }
    }

    fun loadData() {
        _cosmetics.clear()
        _players.clear()
        val response = client.send(
            HttpRequest.newBuilder(URI("https://cosmetics.meowdd.ing")).GET().build(),
            HttpResponse.BodyHandlers.ofString(Charsets.UTF_8),
        )
        if (response.statusCode() != 200) return
        val body = response.body().readJson<JsonObject>()
        try {
            body.toDataOrThrow<Unit>(
                RecordCodecBuilder.create {
                    it.group(
                        MeowddingLibCodecs.CompletablePlayerDataCodec.codec().listOf().fieldOf("players").forGetter { emptyList() },
                        MeowddingLibCodecs.CosmeticCodec.codec().listOf().fieldOf("cosmetics").forGetter { emptyList() },
                    ).apply(it) { players, cosmetics ->
                        val cosmetics = cosmetics.filter { (_, version) -> version >= COSMETIC_VERSION }
                        this._cosmetics.putAll(cosmetics.associateBy(Cosmetic::id))
                        this._players.putAll(players.map(CompletablePlayerData::complete).associateBy(PlayerData::uuid))
                    }
                },
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        this._mlibCosmetics.putAll(
            playerList.associateNotNull(
                keySelector = { it.uuid },
                valueSelector = {
                    it.data.toData(MeowddingLibCodecs.getCodec<MlibCosmeticData>())?.takeUnless { data -> data == emptyCosmetic }
                },
            ),
        )
        MeowddingLib.info("Fetched ${players.size} players and ${cosmetics.size} cosmetics")
    }

    @GenerateCodec
    internal data class CompletablePlayerData(
        val uuid: UUID,
        @FieldName("extra_data") val extraData: JsonObject,
        val cosmetics: List<CosmeticId>,
    ) {
        fun complete() = PlayerData(
            uuid,
            JsonObject().apply {
                extraData.entrySet().forEach { (key, value) -> add(key, value) }

                cosmetics.mapNotNull { it.resolve() }.filter { it.version <= COSMETIC_VERSION }.forEach {
                    it.data.entrySet().forEach { (key, value) ->
                        if (key == "version" || key == "id") return@forEach
                        if (has(key)) return@forEach
                        add(key, value)
                    }
                }
            },
        )
    }

    data class PlayerData(
        val uuid: UUID,
        val data: JsonObject,
    )

    @GenerateCodec
    data class Cosmetic(
        val id: CosmeticId,
        val version: Int,
        @Unnamed val data: JsonObject,
    )

    @JvmInline
    value class CosmeticId(val cosmeticId: String)

    fun CosmeticId.resolve(): Cosmetic? = cosmetics[this]

    @IncludedCodec
    internal val COSMETIC_ID_CODEC: Codec<CosmeticId> = Codec.STRING.xmap(::CosmeticId, CosmeticId::cosmeticId)

    @GenerateCodec
    data class MlibCosmeticData(
        @Lenient val suffix: Component?,
        @NamedCodec("cosmetic_url_type") @Lenient @FieldName("cape_texture") val capeTexture: URI?,
    )

    private val emptyCosmetic = MlibCosmeticData(null, null)

    @IncludedCodec(named = "cosmetic_url_type")
    val URL_KEY: Codec<URI> = Codec.STRING.flatXmap(
        { string ->
            runCatching {
                URI.create(string).takeUnless { it.host != "files.meowdd.ing" }
            }.getOrNull()?.let {
                DataResult.success(it)
            } ?: DataResult.error { "Invalid cosmetic url!" }
        },
        { DataResult.success(it.toString()) },
    )
}
