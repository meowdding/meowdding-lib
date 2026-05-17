package me.owdding.lib.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import me.owdding.lib.cosmetics.MlibCosmetics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
//~ if >= 26.1 'state' -> 'state.level'
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Unique
    private M adultModel;

    @Shadow
    protected M model;

    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At("HEAD")
    )
    private void modifyModel(
        LivingEntityRenderState state,
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        CameraRenderState camera,
        CallbackInfo ci
    ) {
       if (!(state instanceof AvatarRenderState meowState)) return;
       if (!(this.model instanceof PlayerModel)) return;

       if (adultModel == null) {
           adultModel = this.model;
       }

       Boolean small = state.getData(MlibCosmetics.BABY_MODIFIER_DATA_KEY);
       if (small != null && small) {
           this.model = (M) MlibCosmetics.getBabyModel(meowState);
       } else {
           this.model = adultModel;
       }
    }
}
