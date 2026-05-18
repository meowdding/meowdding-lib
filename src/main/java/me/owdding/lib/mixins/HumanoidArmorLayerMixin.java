package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.owdding.lib.cosmetics.MlibCosmetics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> {
    @ModifyReturnValue(
        method = "getArmorModel",
        at = @At("RETURN")
    )
    private A modifyArmorModel(A original, S state, EquipmentSlot slot) {
        return MlibCosmetics.getArmorModel(original, state, slot);
    }
}
