package me.owdding.lib.mixins;

import me.owdding.lib.cosmetics.MlibCosmetics;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {

    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
        at = @At("TAIL")
    )
    public <AvatarlikeEntity extends Avatar & ClientAvatarEntity> void postExtractRenderStateModifyCapeData(
        AvatarlikeEntity entity,
        AvatarRenderState state,
        float partialTicks,
        CallbackInfo ci
    ) {
        MlibCosmetics.tryModify(entity, state, partialTicks);
    }

}
