package me.owdding.lib.mixins;

import me.owdding.lib.platform.KeyHelper;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onButton", at = @At("HEAD"))
    public void onButton(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
        if (action == 1) {
            KeyHelper.INSTANCE.press(rawButtonInfo.button());
        } else {
            KeyHelper.INSTANCE.release(rawButtonInfo.button());
        }
    }

}
