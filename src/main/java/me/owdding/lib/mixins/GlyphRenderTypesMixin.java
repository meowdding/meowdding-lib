//~ named_identifier
package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.lib.rendering.text.TextShaders;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlyphRenderTypes.class)
public class GlyphRenderTypesMixin {

    @Unique
    private Identifier meowdding$texture;

    @ModifyReturnValue(method = {"createForColorTexture", "createForIntensityTexture"}, at = @At("RETURN"))
    private static GlyphRenderTypes create(GlyphRenderTypes original, @Local(argsOnly = true) Identifier texture) {
        ((GlyphRenderTypesMixin) ((Object) original)).meowdding$texture = texture;
        return original;
    }

    @Inject(method = "select", at = @At("HEAD"), cancellable = true)
    private void select(CallbackInfoReturnable<RenderType> cir) {
        var shader = TextShaders.getActiveShader();
        if (shader != null) {
            cir.setReturnValue(shader.getRenderType(meowdding$texture));
        }
    }

}
