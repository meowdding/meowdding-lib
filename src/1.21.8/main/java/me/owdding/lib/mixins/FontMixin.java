package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.owdding.lib.accessor.FontPipelineHolder;
import me.owdding.lib.helper.TextShaderHolder;
import me.owdding.lib.rendering.text.TextShaders;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.Font$PreparedTextBuilder")
public class FontMixin {

    @Unique
    private final int meowddinglib$shadow = ARGB.scaleRGB(0xFFFFFFFF, 0.25f);

    @Inject(method = "addGlyph", at = @At("HEAD"))
    private void apply(BakedGlyph.GlyphInstance instance, CallbackInfo ci) {
        var holder = FontPipelineHolder.getHolder(instance);
        if (holder == null) return;
        holder.meowddinglib$setPipeline(FontPipelineHolder.ACTIVE_PIPELINE.get());
    }

    @Inject(method = "addEffect", at = @At("HEAD"))
    private void apply(BakedGlyph.Effect effect, CallbackInfo ci) {
        var holder = FontPipelineHolder.getHolder(effect);
        if (holder == null) return;
        holder.meowddinglib$setPipeline(FontPipelineHolder.ACTIVE_PIPELINE.get());
    }

    @ModifyReturnValue(method = "getTextColor", at = @At("RETURN"))
    public int getTextColor(int original) {
        var shader = TextShaders.getActiveShader();
        if (shader != null && shader.getUseWhite()) {
            return ARGB.color(ARGB.alpha(original), 0xFFFFFF);
        }
        return original;
    }

    @ModifyReturnValue(method = "getShadowColor", at = @At("RETURN"))
    public int getShadowColor(int original) {
        var shader = TextShaders.getActiveShader();
        if (shader == null) {
            return original;
        }
        var shadow = shader.getHasShadow();
        if (shadow != null && !shadow && !shader.getUseWhite()) {
            return original;
        }
        return ARGB.color(ARGB.alpha(original), meowddinglib$shadow);
    }

    @WrapMethod(method = "accept")
    public boolean accept(int $$0, Style style, int $$2, Operation<Boolean> original) {
        var previous = FontPipelineHolder.ACTIVE_PIPELINE.get();
        var previousShader = TextShaders.getActiveShader();
        if (style instanceof TextShaderHolder holder && holder.meowddinglib$getTextShader() != null) {
            FontPipelineHolder.ACTIVE_PIPELINE.set(holder.meowddinglib$getTextShader().getPipeline());
            TextShaders.setActiveShader(holder.meowddinglib$getTextShader());
        }
        var result = original.call($$0, style, $$2);
        FontPipelineHolder.ACTIVE_PIPELINE.set(previous);
        TextShaders.setActiveShader(previousShader);

        return result;
    }
}
