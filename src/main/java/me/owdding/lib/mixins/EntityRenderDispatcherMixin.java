package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import me.owdding.lib.cosmetics.MlibCosmetics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"))
    public <S extends EntityRenderState> void scale(
        CallbackInfo ci,
        @Local(argsOnly = true) S renderState,
        @Local(argsOnly = true) PoseStack poseStack
    ) {
        if (renderState instanceof AvatarRenderState avatarState) {
            float scale = avatarState.getDataOrDefault(MlibCosmetics.BABY_MODIFIER_DATA_KEY, -1f);
            if (scale != -1f) {
                var trueScale = Math.clamp(scale, 0.3f, 1.7f);
                poseStack.scale(trueScale, trueScale, trueScale);
            }
        }
    }
}
