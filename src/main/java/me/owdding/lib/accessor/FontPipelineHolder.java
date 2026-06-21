package me.owdding.lib.accessor;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.owdding.lib.rendering.text.TextShader;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jetbrains.annotations.Nullable;

public interface FontPipelineHolder {

    ThreadLocal<TextShader> ACTIVE_PIPELINE = new ThreadLocal<>();

    @Nullable TextShader meowddinglib$getPipeline();

    void meowddinglib$setPipeline(TextShader pipeline);

    static @Nullable FontPipelineHolder getHolder(Object instance) {
        return instance instanceof FontPipelineHolder holder ? holder : null;
    }

    interface FontPipelineCreator extends FontPipelineHolder {
        static @Nullable FontPipelineCreator getHolder(Object instance) {
            return instance instanceof FontPipelineCreator holder ? holder : null;
        }

        RenderType meowddinglib$createType(TextShader shader, Font.DisplayMode displayMode);

    }
}
