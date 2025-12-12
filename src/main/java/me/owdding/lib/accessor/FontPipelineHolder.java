//? if > 1.21.5 {
package me.owdding.lib.accessor;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.jetbrains.annotations.Nullable;

public interface FontPipelineHolder {

    ThreadLocal<RenderPipeline> ACTIVE_PIPELINE = ThreadLocal.withInitial(() -> null);

    @Nullable RenderPipeline meowddinglib$getPipeline();

    void meowddinglib$setPipeline(RenderPipeline pipeline);

    static @Nullable FontPipelineHolder getHolder(Object instance) {
        return instance instanceof FontPipelineHolder holder ? holder : null;
    }
}
//?}
