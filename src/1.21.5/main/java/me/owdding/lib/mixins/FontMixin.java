package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.owdding.lib.helper.TextShaderHolder;
import me.owdding.lib.helper.TextShaderMixinHelper;
import me.owdding.lib.rendering.text.TextShaders;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.gui.Font$StringRenderOutput")
public class FontMixin {

    @Shadow
    @Final
    private Font.DisplayMode mode;
    @Unique
    private final int meowddinglib$shadow = ARGB.scaleRGB(0xFFFFFFFF, 0.25f);

    @WrapOperation(method = "finish", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;renderType(Lnet/minecraft/client/gui/Font$DisplayMode;)Lnet/minecraft/client/renderer/RenderType;", ordinal = 0))
    public RenderType modify(BakedGlyph instance, Font.DisplayMode displayMode, Operation<RenderType> original) {
        TextShaderMixinHelper.skip = true;
        var returnValue = original.call(instance, displayMode);
        TextShaderMixinHelper.skip = false;
        return returnValue;
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
        var previousShader = TextShaders.getActiveShader();
        var previousHolder = TextShaderMixinHelper.meowddinglib$SHADER.get();
        if (style instanceof TextShaderHolder holder && holder.meowddinglib$getTextShader() != null) {
            TextShaders.setActiveShader(holder.meowddinglib$getTextShader());
            TextShaderMixinHelper.meowddinglib$SHADER.set(holder.meowddinglib$getTextShader());
        }
        var result = original.call($$0, style, $$2);
        TextShaders.setActiveShader(previousShader);
        TextShaderMixinHelper.meowddinglib$SHADER.set(previousHolder);

        return result;
    }

    @WrapOperation(method = "renderCharacters", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;renderType(Lnet/minecraft/client/gui/Font$DisplayMode;)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType renderCharacters(
        BakedGlyph instance,
        Font.DisplayMode displayMode,
        Operation<RenderType> original,
        @Local BakedGlyph.GlyphInstance glyph
    ) {
        var previousShader = TextShaders.getActiveShader();
        if (((Object) glyph) instanceof TextShaderHolder holder && holder.meowddinglib$getTextShader() != null) {
            TextShaders.setActiveShader(holder.meowddinglib$getTextShader());
        }
        var result = original.call(instance, displayMode);
        TextShaders.setActiveShader(previousShader);
        return result;
    }

    @WrapOperation(method = "finish", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;renderEffect(Lnet/minecraft/client/gui/font/glyphs/BakedGlyph$Effect;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;I)V", ordinal = 1))
    public void renderEffect(
        BakedGlyph instance,
        BakedGlyph.Effect effect,
        Matrix4f pose,
        VertexConsumer buffer,
        int lightCoords,
        Operation<Void> original
    ) {
        if (((Object) effect) instanceof TextShaderHolder holder && holder.meowddinglib$getTextShader() != null) {
            var previousShader = TextShaders.getActiveShader();
            TextShaders.setActiveShader(holder.meowddinglib$getTextShader());
            original.call(instance, effect, pose, instance.renderType(this.mode), lightCoords);
            TextShaders.setActiveShader(previousShader);
        } else {
            original.call(instance, effect, pose, buffer, lightCoords);
        }
    }
}
