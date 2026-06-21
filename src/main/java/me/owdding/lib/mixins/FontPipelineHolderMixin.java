package me.owdding.lib.mixins;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.owdding.lib.accessor.FontPipelineHolder;
import me.owdding.lib.rendering.text.TextShader;
import net.minecraft.client.gui.Font;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = {
    "net.minecraft.client.renderer.state.gui.GuiTextRenderState",
    "net.minecraft.client.gui.Font$PreparedTextBuilder",
    "net.minecraft.client.gui.font.glyphs.BakedSheetGlyph$GlyphInstance",
    "net.minecraft.client.gui.font.glyphs.BakedSheetGlyph$EffectInstance",
})
public class FontPipelineHolderMixin implements FontPipelineHolder {

    @Unique
    private TextShader meowddinglib$pipeline;

    @Override
    public TextShader meowddinglib$getPipeline() {
        return this.meowddinglib$pipeline;
    }

    @Override
    public void meowddinglib$setPipeline(TextShader pipeline) {
        this.meowddinglib$pipeline = pipeline;
    }
}
