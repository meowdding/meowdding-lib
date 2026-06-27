package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.owdding.lib.accessor.FontPipelineHolder;
import me.owdding.lib.helper.TextShaderRenderTypeHolder;
import me.owdding.lib.rendering.text.TextShader;
import me.owdding.lib.rendering.text.TextShaderKt;
import me.owdding.lib.rendering.text.TextShaders;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlyphRenderTypes.class)
public abstract class GlyphRenderTypesMixin implements TextShaderRenderTypeHolder, FontPipelineHolder.FontPipelineCreator {

    @Unique
    private Identifier meowdding$texture;
    @Unique
    private Boolean meowdding$grayscale = false;

    @ModifyReturnValue(method = {"createForColorTexture", "createForIntensityTexture", "createForGrayscaleTexture"}, at = @At("RETURN"))
    private static GlyphRenderTypes create(GlyphRenderTypes original, @Local(argsOnly = true) Identifier name) {
        ((GlyphRenderTypesMixin) ((Object) original)).meowdding$texture = name;
        return original;
    }

    @ModifyReturnValue(method = {"createForGrayscaleTexture"}, at = @At("RETURN"), require = 0)
    private static GlyphRenderTypes grayscale(GlyphRenderTypes original) {
        ((GlyphRenderTypesMixin) ((Object) original)).meowdding$grayscale = true;
        return original;
    }

    @Inject(method = "select", at = @At("HEAD"), cancellable = true)
    private void select(Font.DisplayMode mode, CallbackInfoReturnable<RenderType> cir) {
        var shader = TextShaders.getActiveShader();
        if (shader != null) {
            cir.setReturnValue(meowddinglib$getRenderType(shader.getPipeline(mode, meowdding$grayscale), mode, meowdding$grayscale));
        }
    }

    @Override
    public RenderType meowddinglib$getRenderType(RenderPipeline pipeline, Font.DisplayMode displayMode, Boolean grayScale) {
        return TextShaderKt.createTextRenderType(pipeline, meowdding$texture, displayMode, grayScale);
    }

    @Override
    public RenderType meowddinglib$createType(TextShader shader, Font.DisplayMode displayMode) {
        return TextShaderKt.createTextRenderType(shader, meowdding$texture, displayMode, meowdding$grayscale);
    }
}
