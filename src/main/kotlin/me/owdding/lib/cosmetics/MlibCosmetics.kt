package me.owdding.lib.cosmetics

import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.Lenient
import me.owdding.ktcodecs.NamedCodec
import me.owdding.lib.PreInitModule
import me.owdding.lib.events.CosmeticLoadEvent
import me.owdding.lib.extensions.associateNotNull
import me.owdding.lib.generated.MeowddingLibCodecs
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@PreInitModule
object MlibCosmetics {
    private val _mlibCosmetics = ConcurrentHashMap<UUID, MlibCosmeticData>()

    @get:JvmStatic
    val mlibCosmetics: Map<UUID, MlibCosmeticData> get() = _mlibCosmetics

    @GenerateCodec
    data class MlibCosmeticData(
        @Lenient val suffix: Component?,
        @NamedCodec("cosmetic_url_type") @Lenient @FieldName("cape_texture") val capeTexture: URI?,
    )

    private val emptyCosmetic = MlibCosmeticData(null, null)

    @Subscription
    fun onCosmeticLoad(event: CosmeticLoadEvent) {
        this._mlibCosmetics.putAll(
            CosmeticManager.playerList.associateNotNull(
                keySelector = { it.uuid },
                valueSelector = {
                    it.data.toData(MeowddingLibCodecs.getCodec<MlibCosmeticData>())?.takeUnless { data -> data == emptyCosmetic }
                },
            ),
        )
    }
}
