package me.owdding.lib.accessor;

import com.mojang.blaze3d.pipeline.RenderPipeline;

public interface FontPipelineHolder {

    ThreadLocal<RenderPipeline> ACTIVE_PIPELINE = ThreadLocal.withInitial(() -> null);

    RenderPipeline meowddinglib$getPipeline();

    void meowddinglib$setPipeline(RenderPipeline pipeline);

    static FontPipelineHolder getHolder(Object instance) {
        if (instance instanceof FontPipelineHolder holder) {
            return holder;
        }
        return null;
    }
}
