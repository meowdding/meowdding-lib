package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.lib.cosmetics.MlibCosmetics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WingsLayer.class)
public abstract class WingsLayerMixin<S extends HumanoidRenderState, M extends EntityModel<S>> {

    @ModifyExpressionValue(
        method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;isBaby:Z",
            opcode = Opcodes.GETFIELD
        )
    )
    private boolean modifyElytraModel(boolean original, @Local(argsOnly = true) S state) {
        Boolean small = state.getData(MlibCosmetics.BABY_MODIFIER_DATA_KEY);
        return original || (small != null && small);
    }
}
