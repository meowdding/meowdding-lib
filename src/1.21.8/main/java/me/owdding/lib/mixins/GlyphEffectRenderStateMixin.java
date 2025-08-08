package me.owdding.lib.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.owdding.lib.accessor.FontPipelineHolder;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.render.state.GlyphEffectRenderState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(GlyphEffectRenderState.class)
public class GlyphEffectRenderStateMixin {

    @Shadow
    @Final
    @NotNull
    private BakedGlyph.Effect effect;

    @WrapMethod(method = "pipeline")
    private RenderPipeline meowddinglib$usePipeline(Operation<RenderPipeline> original) {
        var holder = FontPipelineHolder.getHolder(this.effect);
        var pipeline = holder != null ? holder.meowddinglib$getPipeline() : null;
        return Objects.requireNonNullElseGet(pipeline, original::call);
    }
}
