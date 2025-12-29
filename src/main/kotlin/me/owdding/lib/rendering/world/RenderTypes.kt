package me.owdding.lib.rendering.world

//? > 1.21.10 {
//?}
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType


object RenderTypes {

    private val blockFillTriangleThroughWalls = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
        .withLocation("pipeline/debug_filled_box")
        .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build()

    val BLOCK_FILL_TRIANGLE_THROUGH_WALLS =
        //? if > 1.21.10 {
        RenderType.create(
            "mlib/filled_through_walls/triangle",
            RenderSetup.builder(blockFillTriangleThroughWalls)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .bufferSize(131072)
                .createRenderSetup(),
        )
    //?} else {
    /*RenderType.create(
        "mlib/filled_through_walls/triangle",
        131072,
        blockFillTriangleThroughWalls,
        RenderType.CompositeState.builder()
            .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(false),
    )
*///?}

    private val blockFillQuad = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
        .withLocation("pipeline/debug_filled_box")
        .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
        .withDepthBias(-1f, -10f)
        .build()

    val BLOCK_FILL_QUAD =
        //? if > 1.21.10 {
        RenderType.create(
            "mlib/depth_block_fill/quad",
            RenderSetup.builder(blockFillQuad)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .bufferSize(131072)
                .createRenderSetup(),
        )
    //?} else {
    /*RenderType.create(
        "mlib/depth_block_fill/quad",
        131072,
        blockFillQuad,
        RenderType.CompositeState.builder()
            .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(false),
    )
*///?}


    private val debugFilledBox = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
        .withLocation("pipeline/debug_filled_box")
        .build()

    val DEBUG_FILLED_BOX =
        //? if > 1.21.10 {
        RenderType.create(
            "mlib/debug_filled_box",
            RenderSetup.builder(debugFilledBox)
                .sortOnUpload()
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .createRenderSetup(),
        )
    //?} else {
    /*RenderType.create(
        "mlib/debug_filled_box",
        1536,
        false,
        true,
        debugFilledBox,
        RenderType.CompositeState.builder()
            .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(false),
    )
     
    *///?}
}
