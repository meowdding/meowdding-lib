package me.owdding.lib.displays.entity

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.vertex.PoseStack
import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.component1
import org.joml.component2
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.getTranslation
import java.util.function.Function

class EntityStateRenderer(buffer: MultiBufferSource.BufferSource) : PictureInPictureRenderer<EntityStateRenderer.State>(buffer) {

    private var lastState: State? = null

    override fun getRenderStateClass(): Class<State> = State::class.java

    override fun textureIsReadyToBlit(state: State): Boolean {
        return lastState != null && lastState == state
    }

    override fun renderToTexture(state: State, stack: PoseStack) {
        val dispatcher = McClient.self.entityRenderDispatcher
        val renderer = McClient.self.gameRenderer

        renderer.lighting.setupFor(Lighting.Entry.ENTITY_IN_UI)
        stack.translate(state.translation.x, state.translation.y, state.translation.z)
        stack.mulPose(state.rotation)
        if (state.cameraAngle != null) {
            //dispatcher.overrideCameraOrientation(state.cameraAngle.conjugate(Quaternionf()).rotateY(Mth.PI))
        }

        //dispatcher.setRenderShadow(false)
        //dispatcher.render(state.state, 0.0, 0.0, 0.0, stack, this.bufferSource, 15728880)
        //dispatcher.setRenderShadow(true)
    }

    override fun getTextureLabel(): String = "meowdding_lib_entity_state"

    data class State(
        val state: EntityRenderState,
        val translation: Vector3f, val rotation: Quaternionf, val cameraAngle: Quaternionf?,
        val x0: Int, val y0: Int, val x1: Int, val y1: Int,
        val scale: Float,
        val scissor: ScreenRectangle?, val bounds: ScreenRectangle?,
    ) : OlympusPictureInPictureRenderState<State> {

        override fun getFactory(): Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<State>> =
            Function { EntityStateRenderer(it) }

        override fun x0(): Int = x0
        override fun x1(): Int = x1
        override fun y0(): Int = y0
        override fun y1(): Int = y1
        override fun scale(): Float = scale
        override fun scissorArea(): ScreenRectangle? = scissor
        override fun bounds(): ScreenRectangle? = bounds

        override fun hashCode(): Int {
            return System.identityHashCode(state)
        }

        override fun equals(other: Any?): Boolean {
            return when (other) {
                is State -> state == other.state
                else -> false
            }
        }
    }

    companion object {

        fun <T : Entity, S : EntityRenderState> EntityRenderer<in T, S>.createNewState(entity: T): EntityRenderState {
            val state = this.createRenderState()
            this.extractRenderState(entity, state, 1f)

            state.hitboxesRenderState = null
            state.x = 0.0
            state.y = 0.0
            state.z = 0.0

            return state
        }

        fun draw(
            graphics: GuiGraphics,
            entity: LivingEntity,
            width: Int, height: Int, scale: Float,
            translation: Vector3f, rotation: Quaternionf, cameraAngle: Quaternionf?,
        ) {
            val entityState = McClient.self.entityRenderDispatcher.getRenderer(entity).createNewState(entity)

            val (x, y) = graphics.getTranslation()

            val x0 = x.toInt()
            val y0 = y.toInt()
            val x1 = width + x0
            val y1 = height + y0

            val state = State(
                entityState,
                translation, rotation, cameraAngle,
                x0, y0, x1, y1,
                scale,
                graphics.scissorStack.peek(),
                PictureInPictureRenderState.getBounds(x0, y0, x1, y1, graphics.scissorStack.peek()),
            )
            graphics.guiRenderState.submitPicturesInPictureState(state)
        }
    }
}
