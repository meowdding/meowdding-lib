package me.owdding.lib.utils

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.shaders.UniformType

actual object MeowddingPipelines {

    actual val GAME_TIME_SNIPPET: RenderPipeline.Snippet = RenderPipeline
        .builder()
        .withUniform("GameTime", UniformType.FLOAT)
        .buildSnippet()
}
