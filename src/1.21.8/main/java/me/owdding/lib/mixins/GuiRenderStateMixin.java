package me.owdding.lib.mixins;

import me.owdding.lib.accessor.FontPipelineHolder;
import me.owdding.lib.rendering.text.TextShaders;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderState.class)
public class GuiRenderStateMixin {

    @Inject(method = "submitText", at = @At("HEAD"))
    private void prepareTextHead(@NotNull GuiTextRenderState state, CallbackInfo ci) {
        var shader = TextShaders.getActiveShader();
        if (shader == null) return;
        var holder = FontPipelineHolder.getHolder(state);
        if (holder == null) return;
        holder.meowddinglib$setPipeline(shader.getPipeline());
    }
}
