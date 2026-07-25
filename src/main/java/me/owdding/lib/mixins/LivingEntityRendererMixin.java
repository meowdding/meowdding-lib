package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.owdding.lib.cosmetics.MlibCosmetics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @ModifyExpressionValue(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;model:Lnet/minecraft/client/model/EntityModel;",
            opcode = Opcodes.GETFIELD
        )
    )
    private M modifyModelForBaby(M original, S state) {
        if (state instanceof AvatarRenderState meowState && original instanceof PlayerModel) {
            Boolean small = state.getData(MlibCosmetics.BABY_MODIFIER_DATA_KEY);
            if (small != null && small) {
                return (M) MlibCosmetics.getBabyModel(meowState);
            }
        }
        return original;
    }
}
