package me.owdding.lib.compat

import com.mojang.blaze3d.pipeline.RenderPipeline
import me.owdding.ktmodules.Module
import me.owdding.lib.rendering.world.RenderTypes
import me.owdding.lib.utils.KnownMods
import net.irisshaders.iris.api.v0.IrisApi
import net.irisshaders.iris.api.v0.IrisProgram
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes as MinecraftRenderTypes

interface IrisCompatability {
    fun registerPipeline(renderPipeline: RenderPipeline, shaderType: IrisShaderType) {}

    //? if > 1.21.10 {
    fun registerRenderType(renderPipeline: RenderType, shaderType: IrisShaderType) {
        registerPipeline(renderPipeline.pipeline(), shaderType)
    }
    //?} else {
    /*fun registerRenderType(renderPipeline: RenderType.CompositeRenderType, shaderType: IrisShaderType) {
        registerPipeline(renderPipeline.renderPipeline, shaderType)
    }
    *///?}

    @Module
    companion object : IrisCompatability by resolve() {
        init {
            registerRenderType(RenderTypes.BLOCK_FILL_TRIANGLE_THROUGH_WALLS, IrisShaderType.BASIC)
            registerRenderType(RenderTypes.BLOCK_FILL_QUAD, IrisShaderType.BASIC)
            registerRenderType(MinecraftRenderTypes.debugFilledBox(), IrisShaderType.BASIC)
        }
    }
}

enum class IrisShaderType {
    LINES,
    BASIC,
    TEXTURED,
    TRANSLUCENT,
    ARMOR_GLINT,
    BLOCK,
    BLOCK_TRANSLUCENT,
    BEACON_BEAM,
}


internal object IrisCompatNoOp : IrisCompatability

internal fun resolve(): IrisCompatability = if (KnownMods.IRIS.installed) IrisCompatImpl else IrisCompatNoOp

internal object IrisCompatImpl : IrisCompatability {
    private val instance by lazy { IrisApi.getInstance() }

    override fun registerPipeline(renderPipeline: RenderPipeline, shaderType: IrisShaderType) {
        val type = when (shaderType) {
            IrisShaderType.BASIC -> IrisProgram.BASIC
            IrisShaderType.LINES -> IrisProgram.LINES
            IrisShaderType.TEXTURED -> IrisProgram.TEXTURED
            IrisShaderType.TRANSLUCENT -> IrisProgram.TRANSLUCENT
            IrisShaderType.ARMOR_GLINT -> IrisProgram.ARMOR_GLINT
            IrisShaderType.BLOCK -> IrisProgram.BLOCK
            IrisShaderType.BLOCK_TRANSLUCENT -> IrisProgram.BLOCK_TRANSLUCENT
            IrisShaderType.BEACON_BEAM -> IrisProgram.BEACON_BEAM
        }
        instance.assignPipeline(renderPipeline, type)
    }
}
