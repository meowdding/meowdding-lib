package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.owdding.lib.accessor.FontPipelineHolder;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Mixin(targets = {
    "net.minecraft.client.gui.font.glyphs.BakedSheetGlyph$GlyphInstance",
    "net.minecraft.client.gui.font.glyphs.BakedGlyph$EffectInstance",
})
public class TextRenderablesMixin {

    @WrapMethod(method = "guiPipeline")
    private RenderPipeline meowddinglib$usePipeline(Operation<RenderPipeline> original) {
       var holder = FontPipelineHolder.getHolder(this);
       var pipeline = holder != null ? holder.meowddinglib$getPipeline() : null;
       return Objects.requireNonNullElseGet(pipeline, original::call);
    }
}
