package me.owdding.lib.mixins;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.owdding.lib.accessor.FontPipelineHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = {
    "net.minecraft.client.gui.render.state.GuiTextRenderState",
    "net.minecraft.client.gui.font.glyphs.BakedGlyph$GlyphInstance",
    "net.minecraft.client.gui.font.glyphs.BakedGlyph$Effect",
    "net.minecraft.client.gui.font.Font$PreparedTextBuilder",
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
