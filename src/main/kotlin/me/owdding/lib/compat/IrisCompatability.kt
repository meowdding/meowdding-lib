package me.owdding.lib.compat

import com.mojang.blaze3d.pipeline.RenderPipeline
import me.owdding.ktmodules.Module
import me.owdding.lib.rendering.world.RenderTypes
import me.owdding.lib.utils.KnownMods
import net.irisshaders.iris.api.v0.IrisApi
import net.irisshaders.iris.api.v0.IrisProgram

interface IrisCompatability {
    fun registerPipeline(renderPipeline: RenderPipeline, shaderType: IrisShaderType) {}

    @Module
    companion object : IrisCompatability by resolve() {
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

        init {
            registerPipeline(RenderTypes.BLOCK_FILL_TRIANGLE_THROUGH_WALLS.renderPipeline, IrisShaderType.BASIC)
            registerPipeline(RenderTypes.BLOCK_FILL_QUAD.renderPipeline, IrisShaderType.BASIC)
        }
    }
}

internal object IrisCompatNoOp : IrisCompatability

internal fun resolve(): IrisCompatability = if (KnownMods.IRIS.installed) IrisCompatImpl else IrisCompatNoOp

internal object IrisCompatImpl : IrisCompatability {
    private val instance by lazy { IrisApi.getInstance() }

    override fun registerPipeline(renderPipeline: RenderPipeline, shaderType: IrisCompatability.Companion.IrisShaderType) {
        val type = when (shaderType) {
            IrisCompatability.Companion.IrisShaderType.BASIC -> IrisProgram.BASIC
            IrisCompatability.Companion.IrisShaderType.LINES -> IrisProgram.LINES
            IrisCompatability.Companion.IrisShaderType.TEXTURED -> IrisProgram.TEXTURED
            IrisCompatability.Companion.IrisShaderType.TRANSLUCENT -> IrisProgram.TRANSLUCENT
            IrisCompatability.Companion.IrisShaderType.ARMOR_GLINT -> IrisProgram.ARMOR_GLINT
            IrisCompatability.Companion.IrisShaderType.BLOCK -> IrisProgram.BLOCK
            IrisCompatability.Companion.IrisShaderType.BLOCK_TRANSLUCENT -> IrisProgram.BLOCK_TRANSLUCENT
            IrisCompatability.Companion.IrisShaderType.BEACON_BEAM -> IrisProgram.BEACON_BEAM
        }
        instance.assignPipeline(renderPipeline, type)
    }
}
