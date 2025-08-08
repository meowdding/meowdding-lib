package me.owdding.lib.utils

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.shaders.UniformType
import net.msrandom.stub.Stub

actual object MeowddingPipelines {

    actual val GAME_TIME_SNIPPET: RenderPipeline.Snippet = RenderPipeline
        .builder()
        .withUniform("Globals", UniformType.UNIFORM_BUFFER)
        .buildSnippet()
}
