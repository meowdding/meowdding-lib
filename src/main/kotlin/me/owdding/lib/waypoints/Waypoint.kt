package me.owdding.lib.waypoints

import me.owdding.lib.extensions.round
import me.owdding.lib.utils.RenderUtils.renderBox
import me.owdding.lib.utils.RenderUtils.renderTextInWorld
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.translated
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.awt.Color

data class Waypoint(
    val id: Int,
    val position: Vec3,
) {
    constructor(id: Int, block: BlockPos) : this(id, Vec3.atCenterOf(block))
    constructor(id: Int, block: BlockPos, builder: Waypoint.() -> Unit) : this(id, Vec3.atCenterOf(block), builder)
    constructor(id: Int, position: Vec3, builder: Waypoint.() -> Unit) : this(id, position) {
        this.builder()
    }

    var renderTypes: List<WaypointRenderType> = emptyList()
    var name: Component = Text.of("Waypoint $id")
    var color: Int = 0xFFFFFFFF.toInt()
    var inLocatorBar = false

    fun withName(name: String) = withName(Text.of(name))
    fun withName(name: Component) = this.apply { this.name = name }

    fun withColor(color: Color) = withColor(color.rgb)
    fun withColor(color: Int) = this.apply { this.color = color }

    fun withNormalRenderTypes() = withRenderTypes(WaypointRenderType.TEXT, WaypointRenderType.BOX, WaypointRenderType.BEAM, WaypointRenderType.DISTANCE)
    fun withAllRenderTypes() = withRenderTypes(*WaypointRenderType.entries.toTypedArray())
    fun withRenderTypes(vararg types: WaypointRenderType) = this.apply { this.renderTypes = types.toList() }

    fun inLocatorBar(boolean: Boolean = true) = this.apply { this.inLocatorBar = boolean }

    internal fun render(event: RenderWorldEvent) {
        if (renderTypes.isEmpty()) return

        event.poseStack.translated(-0.5, 0.0, -0.5) {
            for (type in renderTypes.sorted()) {
                when (type) {
                    WaypointRenderType.TEXT -> event.renderTextInWorld(position, name)
                    WaypointRenderType.DISTANCE -> event.renderDistance(position)
                    WaypointRenderType.BOX -> event.renderBox(position, color)
                    WaypointRenderType.BEAM -> event.renderBeam(position, color)
                    WaypointRenderType.TRACER -> {}
                }
            }
        }
    }

    private fun RenderWorldEvent.renderDistance(position: Vec3) {
        val text = Text.of("Distance: ${McPlayer.position!!.distanceTo(position).round()}") {
            this.color = TextColor.YELLOW
        }
        this.renderTextInWorld(position.add(0.0, -0.5, 0.0), text)
    }

    private fun RenderWorldEvent.renderBeam(position: Vec3, color: Int) {
        atCamera {
            translate(position)
            BeaconRenderer.renderBeaconBeam(
                poseStack, buffer, BeaconRenderer.BEAM_LOCATION,
                0f, Mth.PI, McLevel.self.gameTime, 0, McLevel.self.maxY * 2,
                ARGB.opaque(color), 0.2f, 0.25f,
            )
        }
    }
}

enum class WaypointRenderType {
    BEAM,
    BOX,
    TEXT,
    DISTANCE,
    TRACER,
}
