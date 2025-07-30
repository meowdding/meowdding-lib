package me.owdding.lib.waypoints

import me.owdding.lib.utils.RenderUtils.renderTextInWorld
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.utils.text.Text
import java.awt.Color

data class Waypoint(
    val id: Int,
    val position: Vec3,
) {
    constructor(id: Int, block: BlockPos) : this(id, Vec3.atCenterOf(block))
    constructor(id: Int, position: Vec3, builder: Waypoint.() -> Unit) : this(id, position) {
        this.builder()
    }

    var renderTypes: List<WaypointRenderType> = emptyList()
    var name: Component = Text.of("Waypoint $id")
    var color: Int = 0xFFFFFFFF.toInt()

    fun withName(name: String) = withName(Text.of(name))
    fun withName(name: Component) = this.apply { this.name = name }

    fun withColor(color: Color) = withColor(color.rgb)
    fun withColor(color: Int) = this.apply { this.color = color }

    fun withAllRenderTypes() = withRenderTypes(*WaypointRenderType.entries.toTypedArray())
    fun withRenderTypes(vararg types: WaypointRenderType) = this.apply { this.renderTypes = types.toList() }

    internal fun RenderWorldEvent.render() {
        if (renderTypes.isEmpty()) return

        for (type in renderTypes) {
            when (type) {
                WaypointRenderType.TEXT -> this.renderTextInWorld(position, name, color)
                WaypointRenderType.DISTANCE -> this.renderDistance(name, position, color)
                //WaypointRenderType.BOX -> this.renderBox(position, color)
                //WaypointRenderType.BEAM -> this.renderBeam(position, color)
                else -> {}
            }
        }
    }

    private fun RenderWorldEvent.renderDistance(name: Component, position: Vec3, color: Int) {
        this.renderTextInWorld(position.add(0.0, -1.0, 0.0), name, color)
    }
}

enum class WaypointRenderType {
    TEXT,
    DISTANCE,
    BOX,
    BEAM,
}
