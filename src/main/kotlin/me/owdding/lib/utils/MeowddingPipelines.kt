package me.owdding.lib.utils

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.shaders.UniformType

object MeowddingPipelines {

    val GAME_TIME_SNIPPET: RenderPipeline.Snippet = RenderPipeline
        .builder()
        //? if > 1.21.5 {
        .withUniform("Globals", UniformType.UNIFORM_BUFFER)
        //?} else
        /*.withUniform("GameTime", UniformType.FLOAT)*/
        .buildSnippet()
}
