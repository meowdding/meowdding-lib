package me.owdding.lib.cosmetics

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktcodecs.Lenient
import me.owdding.ktcodecs.NamedCodec
import me.owdding.lib.PreInitModule
import me.owdding.lib.events.CosmeticLoadEvent
import me.owdding.lib.extensions.associateNotNull
import me.owdding.lib.generated.MeowddingLibCodecs
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
import net.minecraft.client.entity.ClientAvatarEntity
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.player.PlayerModel
import net.minecraft.client.renderer.entity.state.AvatarRenderState
import net.minecraft.client.renderer.entity.state.HumanoidRenderState
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.core.ClientAsset
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Avatar
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.PlayerModelType
import net.minecraft.world.entity.player.PlayerSkin
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
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
        @NamedCodec("cosmetic_scale") @Lenient val small: Float?,
    )

    @IncludedCodec(named = "cosmetic_scale")
    val cosmeticScaleCodec = Codec.either(Codec.BOOL, Codec.FLOAT).xmap({Either.unwrap(it.mapLeft { 0.5f })}, Either<*, Double>::right)

    private val emptyCosmetic = MlibCosmeticData(null, null, null)

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

    @JvmStatic
    fun <AvatarlikeEntity> tryModify(
        entity: AvatarlikeEntity,
        state: AvatarRenderState,
        delta: Float,
    ) where AvatarlikeEntity : Avatar, AvatarlikeEntity : ClientAvatarEntity {
        val cosmetic = mlibCosmetics[entity.uuid] ?: return

        tryModifyCape(cosmetic, state)
        tryApplySmallModifier(cosmetic, state)
    }

    fun tryModifyCape(cosmetic: MlibCosmeticData, state: AvatarRenderState) {
        val texture = cosmetic.capeTexture ?: return

        val image = CosmeticManager.imageProvider.get(texture)
        if (image.equals(MissingTextureAtlasSprite.getLocation())) return

        val skin = state.skin
        state.skin = PlayerSkin(
            skin.body,
            ClientAsset.ResourceTexture(image, image),
            skin.elytra,
            skin.model,
            skin.secure,
        )
    }

    fun tryApplySmallModifier(cosmetic: MlibCosmeticData, state: AvatarRenderState) {
        if (!LocationAPI.isOnSkyBlock) return
        if (cosmetic.small != null) {
            state.setData(BABY_MODIFIER_DATA_KEY, cosmetic.small)
        }
    }

    @JvmField
    val BABY_MODIFIER_DATA_KEY: RenderStateDataKey<Float> = RenderStateDataKey.create { "meowdding_lib:baby_modifier" }
}
