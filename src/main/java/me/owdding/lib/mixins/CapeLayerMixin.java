package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.lib.cosmetics.MlibCosmetics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static me.owdding.lib.cosmetics.ModelConstantsKt.babyCape;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin {

    @ModifyExpressionValue(
        method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/layers/CapeLayer;model:Lnet/minecraft/client/model/HumanoidModel;",
            opcode = Opcodes.GETFIELD
        )
    )
    private HumanoidModel<AvatarRenderState> modifyCapeModel(HumanoidModel<AvatarRenderState> original, @Local(argsOnly = true) AvatarRenderState state) {
        Boolean small = state.getData(MlibCosmetics.BABY_MODIFIER_DATA_KEY);
        if (small != null && small) {
            return babyCape;
        }

        return original;
    }
}
