package me.owdding.lib.internal

import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import me.owdding.lib.generated.MeowddingLibCodecs
import net.minecraft.SharedConstants
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket
import net.minecraft.server.packs.PackType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJsonOrThrow
import java.util.*
import java.util.concurrent.CompletableFuture

private const val API_URL = "https://PLACEHOLDER.thatgravyboat.tech%s"

@Module
object PackSharing {
    private val gson = GsonBuilder().create()
    private val codec = MeowddingLibCodecs.getCodec<ResourcePackInfo>()

    @Subscription
    private fun onPacketReceived(event: PacketReceivedEvent) {
        if (!LocationAPI.onHypixel) return
        val packet = event.packet as? ClientboundResourcePackPushPacket ?: return

        val packInfo = ResourcePackInfo(
            packet.url,
            packet.hash,
            packet.id,
            McPlayer.uuid,
            SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES).major,
            LocationAPI.onAlpha,
        )

        val json = gson.toJson(packInfo.toJsonOrThrow(codec))

        CompletableFuture.runAsync {
            runBlocking {
                Http.post(
                    url = API_URL.format("/submit"),
                    queries = emptyMap(),
                    headers = mapOf(
                        "User-Agent" to "MeowddingLib/${MeowddingLib.VERSION}",
                    ),
                    body = json,
                    handler = { },
                )
            }
        }
    }
}

@GenerateCodec
data class ResourcePackInfo(
    @FieldName("pack_url") val packUrl: String,
    @FieldName("pack_hash") val packHash: String,
    @FieldName("pack_id") val packId: UUID,
    @FieldName("player_uuid") val playerUuid: UUID,
    @FieldName("pack_version") val packVersion: Int,
    val alpha: Boolean,
)
