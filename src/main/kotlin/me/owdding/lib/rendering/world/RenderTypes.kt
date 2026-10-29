package me.owdding.lib.rendering.world

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.renderpearl.api.pipeline.CompareOp
import com.mojang.renderpearl.api.pipeline.DepthStencilState
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType

//? >= 26.3
import net.minecraft.client.renderer.oit.OitPipelineSet

object RenderTypes {

    private val blockFillTriangleThroughWalls = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
        .withLocation("pipeline/debug_filled_box")
        .withPrimitiveTopology(PrimitiveTopology.TRIANGLE_STRIP)
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
        .withDepthStencilState(DepthStencilState(CompareOp.ALWAYS_PASS, false));

    //? >= 26.3 {
    val blockFillTriangleThroughWallsOit: OitPipelineSet = RenderPipelines.register(OitPipelineSet.builder("meowddinglib/pipeline/debug_filled_box",
        RenderPipeline.builder(RenderPipelines.OIT_DEBUG_FILLED_SNIPPET)
            .withPrimitiveTopology(PrimitiveTopology.TRIANGLE_STRIP)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
            .withDepthStencilState(DepthStencilState(CompareOp.ALWAYS_PASS, false))
        ).withoutDepthTest().build())
    //? }

    val BLOCK_FILL_TRIANGLE_THROUGH_WALLS: RenderType = RenderType.create(
        "mlib/filled_through_walls/triangle",
        RenderSetup.builder(RenderPipelines.register(blockFillTriangleThroughWalls.build()))
            //? >= 26.3
            .setOitPipelines(blockFillTriangleThroughWallsOit)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .createRenderSetup(),
    )

    private val blockFillQuad = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
        .withLocation("pipeline/debug_filled_box")
        .withPrimitiveTopology(PrimitiveTopology.QUADS)
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
        .withDepthStencilState(DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, -1f, -10f))
    //? >= 26.3 {
    val blockFillQuadOit: OitPipelineSet = RenderPipelines.register(OitPipelineSet.builder("meowddinglib/pipeline/debug_filled_box_quad",
            RenderPipeline.builder(RenderPipelines.OIT_DEBUG_FILLED_SNIPPET)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
            .withDepthStencilState(DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, -1f, -10f))
    ).withoutDepthTest().build())
    //? }

    val BLOCK_FILL_QUAD = RenderType.create(
        "mlib/depth_block_fill/quad",
        RenderSetup.builder(RenderPipelines.register(blockFillQuad.build()))
            //? >= 26.3
            .setOitPipelines(blockFillQuadOit)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .createRenderSetup(),
    )


    private val debugFilledBox = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
        .withLocation("pipeline/debug_filled_box")
        .build()
    //? >= 26.3 {
    val debugFilledBoxOit: OitPipelineSet = RenderPipelines.register(OitPipelineSet.builder("meowddinglib/pipeline/debug_filled_box_quad", RenderPipeline.builder(RenderPipelines.OIT_DEBUG_FILLED_SNIPPET)).withoutDepthTest().build())
    //? }

    val DEBUG_FILLED_BOX = RenderType.create(
        "mlib/debug_filled_box",
        RenderSetup.builder(debugFilledBox)
            //? >= 26.3
            .setOitPipelines(debugFilledBoxOit)
            .sortOnUpload()
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .createRenderSetup(),
    )
}
