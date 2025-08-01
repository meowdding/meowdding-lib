package me.owdding.lib.mixins;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.owdding.lib.accessor.RenderPipelineBuilderAccessor;
import net.minecraft.client.renderer.ShaderDefines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(RenderPipeline.Builder.class)
public class RenderPipelineBuilderMixin implements RenderPipelineBuilderAccessor {

    @Shadow
    private Optional<ShaderDefines.Builder> definesBuilder;

    @Override
    public void meowddinglib$define(String name, String value) {
        if (this.definesBuilder.isEmpty()) {
            this.definesBuilder = Optional.of(ShaderDefines.builder());
        }
        this.definesBuilder.get().define(name, value);
    }
}
