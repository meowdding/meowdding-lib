package me.owdding.lib.waypoints

import me.owdding.lib.extensions.round
import me.owdding.lib.utils.RenderUtils.renderBeaconBeam
import me.owdding.lib.utils.RenderUtils.renderBox
import me.owdding.lib.utils.RenderUtils.renderLineFromCursor
import me.owdding.lib.utils.RenderUtils.renderTextInWorld
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.translated
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.awt.Color
import java.util.*
import kotlin.random.Random

data class MeowddingWaypoint(val position: Vec3) {
    constructor(block: BlockPos) : this(Vec3.atCenterOf(block))
    constructor(block: BlockPos, addToHandler: Boolean = true, builder: MeowddingWaypoint.() -> Unit) : this(Vec3.atCenterOf(block), addToHandler, builder)
    constructor(position: Vec3, addToHandler: Boolean = true, builder: MeowddingWaypoint.() -> Unit) : this(position) {
        this.builder()
        if (addToHandler) addToHandler()
    }

    var uuid: UUID = UUID.randomUUID()
    var renderTypes: Set<WaypointRenderType> = emptySet()
    var name: Component = Text.of("Waypoint $uuid")
    var color: Int = 0xFFFFFFFF.toInt()
    var inLocatorBar = false
    var renderCondition: (RenderWorldEvent) -> Boolean = { true }
    var removalDistance: Float? = null
    var ignoreY = false

    var minecraftWaypoint: MinecraftWaypoint? = null
        internal set

    val blockPos: BlockPos = BlockPos.containing(position)

    fun withName(name: String) = withName(Text.of(name))
    fun withName(name: Component) = this.apply { this.name = name }

    fun withRandomColor() = withColor(ARGB.opaque(Random.nextInt(0xFFFFFF)))
    fun withColor(color: Color) = withColor(color.rgb)
    fun withColor(color: Int) = this.apply { this.color = color }

    fun withRenderCondition(condition: (RenderWorldEvent) -> Boolean) = this.apply { this.renderCondition = condition }

    fun withRemovalDistance(range: Float? = 5f) = this.apply { this.removalDistance = range }

    fun withNormalRenderTypes() = withRenderTypes(WaypointRenderType.TEXT, WaypointRenderType.BOX, WaypointRenderType.BEAM, WaypointRenderType.DISTANCE)
    fun withAllRenderTypes() = withRenderTypes(*WaypointRenderType.entries.toTypedArray())
    fun withRenderTypes(vararg types: WaypointRenderType) = this.apply { this.renderTypes = types.toSet() }

    fun inLocatorBar(boolean: Boolean = true) = this.apply { this.inLocatorBar = boolean }

    fun withIgnoreY(boolean: Boolean = true) = this.apply { this.ignoreY = boolean }

    fun addToHandler() = MeowddingWaypointHandler.addWaypoint(this)

    internal fun render(event: RenderWorldEvent) {
        if (renderTypes.isEmpty()) return
        if (ignoreY) position.y =
            Mth.lerp(event.ctx.tickCounter().getGameTimeDeltaPartialTick(false).toDouble(), McPlayer.self!!.oldPosition().y, McPlayer.position!!.y)

        event.poseStack.translated(-0.5, 0.0, -0.5) {
            for (type in renderTypes.sorted()) {
                when (type) {
                    WaypointRenderType.TEXT -> event.renderTextInWorld(position, name)
                    WaypointRenderType.DISTANCE -> event.renderDistance(position)
                    WaypointRenderType.BOX -> event.renderBox(position, color)
                    WaypointRenderType.BEAM -> event.renderBeaconBeam(position, color)
                    WaypointRenderType.TRACER -> event.poseStack.translated(0.5, 0, 0.5) { event.renderLineFromCursor(position.add(0.0, 0.5, 0.0), color) }
                }
            }
        }
    }

    private fun RenderWorldEvent.renderDistance(position: Vec3) {
        val text = Text.of("Distance: ") {
            this.color = TextColor.GRAY
            append(McPlayer.position!!.distanceTo(position).round()) { this.color = TextColor.YELLOW }
        }
        this.renderTextInWorld(position.add(0.0, -0.5, 0.0), text, yOffset = if (WaypointRenderType.TEXT in renderTypes) McFont.height.toFloat() else 0f)
    }
}

enum class WaypointRenderType {
    BEAM,
    BOX,
    TEXT,
    DISTANCE,
    TRACER,
}
