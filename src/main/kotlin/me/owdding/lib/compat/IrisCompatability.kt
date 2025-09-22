package me.owdding.lib.compat

import me.owdding.ktmodules.Module
import me.owdding.lib.rendering.world.RenderTypes
import me.owdding.lib.utils.KnownMods
import net.irisshaders.iris.api.v0.IrisApi
import net.irisshaders.iris.api.v0.IrisProgram

@Module
object IrisCompatability {

    init {
        KnownMods.IRIS.installed {
            val iris = IrisApi.getInstance()
            iris.assignPipeline(RenderTypes.BLOCK_FILL_TRIANGLE_THROUGH_WALLS.renderPipeline, IrisProgram.BASIC)
            iris.assignPipeline(RenderTypes.BLOCK_FILL_QUAD.renderPipeline, IrisProgram.BASIC)
        }
    }

}
