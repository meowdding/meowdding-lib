package me.owdding.lib.mixins;

import me.owdding.lib.accessor.FontPipelineHolder;
import me.owdding.lib.rendering.text.TextShaders;
//~ if >= 26.1 'gui.render.state' -> 'renderer.state.gui' {
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
//~ }
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderState.class)
public class GuiRenderStateMixin {

    //~ if >= 26.1 'submitText' -> 'addText'
    @Inject(method = "addText", at = @At("HEAD"))
    private void prepareTextHead(@NotNull GuiTextRenderState state, CallbackInfo ci) {
        var shader = TextShaders.getActiveShader();
        if (shader == null) return;

        var holder = FontPipelineHolder.getHolder(state);
        if (holder == null) return;

        holder.meowddinglib$setPipeline(shader.getPipeline());
    }
}
