package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.owdding.lib.accessor.FontPipelineHolder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(targets = {
    "net.minecraft.client.gui.font.glyphs.BakedSheetGlyph$GlyphInstance",
    "net.minecraft.client.gui.font.glyphs.BakedSheetGlyph$EffectInstance",
})
public class TextRenderablesMixin {

    @WrapMethod(method = "guiPipeline")
    private RenderPipeline meowddinglib$usePipeline(Operation<RenderPipeline> original) {
        var holder = FontPipelineHolder.getHolder(this);
        var pipeline = holder != null ? holder.meowddinglib$getPipeline() : null;
        if (pipeline == null) {
            return original.call();
        }
        return pipeline.getPipeline(null, false);
    }

    @SuppressWarnings("ConstantValue")
    @WrapOperation(
        method = "renderType",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/GlyphRenderTypes;select(Lnet/minecraft/client/gui/Font$DisplayMode;)Lnet/minecraft/client/renderer/rendertype/RenderType;")
    )
    private RenderType meowddinglib$usePipeline(GlyphRenderTypes instance, Font.DisplayMode displayMode, Operation<RenderType> original) {
        var holder = FontPipelineHolder.getHolder(this);
        var pipeline = holder != null ? holder.meowddinglib$getPipeline() : null;

        if (((Object) instance) instanceof FontPipelineHolder.FontPipelineCreator typeHolder && pipeline != null) {
            var rendertype = typeHolder.meowddinglib$createType(pipeline, displayMode);
            if (rendertype != null) {
                return rendertype;
            }
        }
        return original.call(instance, displayMode);
    }
}
