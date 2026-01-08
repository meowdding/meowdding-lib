package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//? > 1.21.5
import me.owdding.lib.accessor.FontPipelineHolder;
import me.owdding.lib.helper.TextShaderHolder;
import me.owdding.lib.rendering.text.TextShaders;
//? > 1.21.8
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
//? > 1.21.5 {
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}

//? < 1.21.9
//import net.minecraft.client.gui.font.glyphs.BakedGlyph;
//? < 1.21.6 {
/*import me.owdding.lib.helper.TextShaderMixinHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
*///?}

@Mixin(
    targets = /*? if > 1.21.5 {*/ "net.minecraft.client.gui.Font$PreparedTextBuilder" /*?} else {*/ /*"net.minecraft.client.gui.Font$StringRenderOutput" *//*?}*/
)
public class FontMixin {

    //? if 1.21.5 {
    /*@Shadow
    @Final
    private Font.DisplayMode mode;
    @Shadow
    @Final
    MultiBufferSource bufferSource;
    *///?}

    @Unique
    private final int meowddinglib$shadow = ARGB.scaleRGB(0xFFFFFFFF, 0.25f);

    //? if > 1.21.5 {
    @Inject(method = "addGlyph", at = @At("HEAD"))
    private void applyGlyph(
        //? > 1.21.10 {
        TextRenderable.Styled instance,
        //?} else if > 1.21.8 {
        /*TextRenderable instance,
         *///?} else
        //BakedGlyph.GlyphInstance instance,
        CallbackInfo ci
    ) {
        var holder = FontPipelineHolder.getHolder(instance);
        if (holder == null) {
            return;
        }
        holder.meowddinglib$setPipeline(FontPipelineHolder.ACTIVE_PIPELINE.get());
    }

    @Inject(method = "addEffect", at = @At("HEAD"))
    private void applyEffewct(
        //? > 1.21.8 {
        TextRenderable effect,
        //?} else
        //BakedGlyph.Effect effect,
        CallbackInfo ci
    ) {
        var holder = FontPipelineHolder.getHolder(effect);
        if (holder == null) {
            return;
        }
        holder.meowddinglib$setPipeline(FontPipelineHolder.ACTIVE_PIPELINE.get());
    }

    @WrapMethod(method = "accept")
    public boolean accept(int $$0, Style style, int $$2, Operation<Boolean> original) {
        var previous = FontPipelineHolder.ACTIVE_PIPELINE.get();
        var previousShader = TextShaders.getActiveShader();
        if ((Object) style instanceof TextShaderHolder holder && holder.meowddinglib$getTextShader() != null) {
            FontPipelineHolder.ACTIVE_PIPELINE.set(holder.meowddinglib$getTextShader().getPipeline());
            TextShaders.setActiveShader(holder.meowddinglib$getTextShader());
        }
        var result = original.call($$0, style, $$2);
        FontPipelineHolder.ACTIVE_PIPELINE.set(previous);
        TextShaders.setActiveShader(previousShader);

        return result;
    }
    //?} else {
    /*@WrapOperation(method = "finish", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;renderType(Lnet/minecraft/client/gui/Font$DisplayMode;)Lnet/minecraft/client/renderer/RenderType;", ordinal = 0))
    public RenderType modify(BakedGlyph instance, Font.DisplayMode displayMode, Operation<RenderType> original) {
        TextShaderMixinHelper.skip = true;
        var returnValue = original.call(instance, displayMode);
        TextShaderMixinHelper.skip = false;
        return returnValue;
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
            original.call(instance, effect, pose, bufferSource.getBuffer(instance.renderType(mode)), lightCoords);
            TextShaders.setActiveShader(previousShader);
        } else {
            original.call(instance, effect, pose, buffer, lightCoords);
        }
    }
    *///?}


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
}
