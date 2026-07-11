package me.owdding.lib.utils

import kotlinx.coroutines.runBlocking
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import me.owdding.lib.generated.MeowddingLibCodecs
import me.owdding.lib.utils.MeowddingLogger.Companion.featureLogger
import net.hypixel.data.region.Environment
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.HypixelJoinEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import java.util.concurrent.CompletableFuture

const val API_URL = "https://skyblock-pack.meowdd.ing/v2"

@Module
object SkyblockPackInfo : MeowddingLogger by MeowddingLib.featureLogger() {
    val regex = Regex("(?i)^https://resourcepacks\\d*\\.hypixel\\.net/SkyBlock(?:ResourcePack)?/(?<uuid>[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/(?<num>\\d{2}).zip$")
    private val codec = MeowddingLibCodecs.getCodec<PackInfo>()

    var onDev = false

    @Subscription
    fun onHypixelJoin(event: HypixelJoinEvent) {
        this.onDev = event.environment == Environment.BETA
    }

    @Subscription(ServerDisconnectEvent::class)
    fun onServerDisconnect() {
        this.onDev = false
    }

    @Subscription
    private fun onPacketReceived(event: PacketReceivedEvent) {
        if (!LocationAPI.onHypixel) return
        if (onDev) return
        val packet = event.packet as? ClientboundResourcePackPushPacket ?: return

        val match = regex.matchEntire(packet.url) ?: return
        val uuid = match.groups["uuid"]?.value ?: return

        val packInfo = PackInfo(uuid, LocationAPI.onAlpha,)

        val json = packInfo.toJson(codec) ?: return

        debug("Sending $json to server.")
        CompletableFuture.runAsync {
            runBlocking {
                Http.post(
                    url = API_URL,
                    body = json,
                    handler = { },
                )
            }
        }
    }
}


@GenerateCodec
data class PackInfo(
    @FieldName("uuid") val packId: String,
    val alpha: Boolean,
)
