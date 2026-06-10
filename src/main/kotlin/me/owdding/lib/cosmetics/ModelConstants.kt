package me.owdding.lib.cosmetics

import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.LayerDefinition
//~ if >= 1.21.11 'model' -> 'model.player'
import net.minecraft.client.model.player.PlayerCapeModel
//~ if >= 1.21.11 'model' -> 'model.player'
import net.minecraft.client.model.player.PlayerModel
import net.minecraft.client.renderer.entity.ArmorModelSet

@JvmField
val smallPlayerModelWide = PlayerModel(
    LayerDefinition.create(
        PlayerModel.createMesh(CubeDeformation.NONE, false),
        64,
        64,
    ).apply(HumanoidModel.BABY_TRANSFORMER).bakeRoot(),
    false,
)

@JvmField
val smallPlayerModelSlim = PlayerModel(
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
    PlayerModel(
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
