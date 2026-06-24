package me.owdding.lib.cosmetics

import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.player.PlayerCapeModel
import net.minecraft.client.model.player.PlayerModel
import net.minecraft.client.renderer.entity.ArmorModelSet
import net.minecraft.client.renderer.entity.state.AvatarRenderState

const val SMALL_PLAYER_AGE_SCALE = 0.5f

private class SmallPlayerModel(root: ModelPart, slim: Boolean) : PlayerModel(root, slim) {
    override fun setupAnim(state: AvatarRenderState) {
        val originalAgeScale = state.ageScale
        state.ageScale = SMALL_PLAYER_AGE_SCALE
        try {
            super.setupAnim(state)
        } finally {
            state.ageScale = originalAgeScale
        }
    }
}

@JvmField
val smallPlayerModelWide: PlayerModel = SmallPlayerModel(
    LayerDefinition.create(
        PlayerModel.createMesh(CubeDeformation.NONE, false),
        64,
        64,
    ).apply(HumanoidModel.BABY_TRANSFORMER).bakeRoot(),
    false,
)

@JvmField
val smallPlayerModelSlim: PlayerModel = SmallPlayerModel(
    LayerDefinition.create(
        PlayerModel.createMesh(CubeDeformation.NONE, true),
        64,
        64,
    ).apply(HumanoidModel.BABY_TRANSFORMER).bakeRoot(),
    true,
)

@JvmField
val babyArmor: ArmorModelSet<PlayerModel> = PlayerModel.createArmorMeshSet(
    CubeDeformation(0.5f),
    CubeDeformation(1f),
).map {
    SmallPlayerModel(
        LayerDefinition.create(it, 64, 32)
            .apply(HumanoidModel.BABY_TRANSFORMER)
            .bakeRoot(),
        false,
    )
}

@JvmField
val babyCape = PlayerCapeModel(
    PlayerCapeModel.createCapeLayer()
        .apply(HumanoidModel.BABY_TRANSFORMER)
        .bakeRoot(),
)
