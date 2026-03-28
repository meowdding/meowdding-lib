package me.owdding.lib.mixins;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.owdding.lib.accessor.FontPipelineHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = {
    //~ if >= 26.1 'gui.render.state' -> 'renderer.state.gui'
    "net.minecraft.client.renderer.state.gui.GuiTextRenderState",
    "net.minecraft.client.gui.Font$PreparedTextBuilder",
    "net.minecraft.client.gui.font.glyphs.BakedSheetGlyph$GlyphInstance",
    "net.minecraft.client.gui.font.glyphs.BakedSheetGlyph$EffectInstance",
})
public class FontPipelineHolderMixin implements FontPipelineHolder {

    @Unique
    private RenderPipeline meowddinglib$pipeline;

    @Override
    public RenderPipeline meowddinglib$getPipeline() {
        return this.meowddinglib$pipeline;
    }

    @Override
    public void meowddinglib$setPipeline(RenderPipeline pipeline) {
        this.meowddinglib$pipeline = pipeline;
    }
}
