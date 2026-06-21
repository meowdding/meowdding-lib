package me.owdding.lib.rendering.world

//? >= 26.2
import com.mojang.blaze3d.PrimitiveTopology
import com.mojang.blaze3d.pipeline.DepthStencilState
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.CompareOp
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType

object RenderTypes {

    private val blockFillTriangleThroughWalls = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
        .withLocation("pipeline/debug_filled_box")
        //? >= 26.2 {
        .withPrimitiveTopology(PrimitiveTopology.TRIANGLE_STRIP)
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
        //? } else
        //.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
        .withDepthStencilState(DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .build()

    val BLOCK_FILL_TRIANGLE_THROUGH_WALLS = RenderType.create(
        "mlib/filled_through_walls/triangle",
        RenderSetup.builder(blockFillTriangleThroughWalls)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            //? 26.1
            //.bufferSize(131072)
            .createRenderSetup(),
    )

    private val blockFillQuad = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
        .withLocation("pipeline/debug_filled_box")
        //? >= 26.2 {
        .withPrimitiveTopology(PrimitiveTopology.QUADS)
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
        //? } else
        //.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
        .withDepthStencilState(DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false, -1f, -10f))
        .build()

    val BLOCK_FILL_QUAD = RenderType.create(
        "mlib/depth_block_fill/quad",
        RenderSetup.builder(blockFillQuad)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            //? 26.1
            //.bufferSize(131072)
            .createRenderSetup(),
    )


    private val debugFilledBox = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
        .withLocation("pipeline/debug_filled_box")
        .build()

    val DEBUG_FILLED_BOX = RenderType.create(
        "mlib/debug_filled_box",
        RenderSetup.builder(debugFilledBox)
            .sortOnUpload()
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .createRenderSetup(),
    )
}
