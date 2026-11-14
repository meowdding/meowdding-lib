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
import java.util.UUID
import kotlin.random.Random

public class MeowddingWaypoint(internal val position: Vec3) {
    public constructor(block: BlockPos) : this(Vec3.atBottomCenterOf(block))
    public constructor(block: BlockPos, addToHandler: Boolean = true, builder: MeowddingWaypoint.() -> Unit) : this(
        Vec3.atBottomCenterOf(block),
        addToHandler,
        builder,
    )
    public constructor(position: Vec3, addToHandler: Boolean = true, builder: MeowddingWaypoint.() -> Unit) : this(position) {
        this.builder()
        if (addToHandler) addToHandler()
    }

    public var uuid: UUID = UUID.randomUUID()
    public var name: Component = Text.of("Waypoint $uuid")
    public var color: Int = 0xFFFFFFFF.toInt()
    public var renderTypes: Set<WaypointRenderType> = emptySet()
        set(value) {
            field = value.toSortedSet()
        }
    public var tags: Set<MeowddingWaypointTag> = emptySet()
    public var inLocatorBar: Boolean = false
    public var renderCondition: (RenderWorldEvent) -> Boolean = { true }
    public var removalDistance: Float? = null
    public var ignoreY: Boolean = false

    public var minecraftWaypoint: MinecraftWaypoint? = null
        internal set

    public fun withName(name: String): MeowddingWaypoint = withName(Text.of(name))
    public fun withName(name: Component): MeowddingWaypoint = this.apply { this.name = name }

    public  fun withRandomColor(): MeowddingWaypoint = withColor(ARGB.opaque(Random.nextInt(0xFFFFFF)))
    public fun withColor(color: Color): MeowddingWaypoint = withColor(color.rgb)
    public fun withColor(color: Int): MeowddingWaypoint = this.apply { this.color = color }

    public fun withRenderCondition(condition: (RenderWorldEvent) -> Boolean): MeowddingWaypoint = this.apply { this.renderCondition = condition }

    public fun withRemovalDistance(range: Float? = 5f): MeowddingWaypoint = this.apply { this.removalDistance = range }

    public fun withNormalRenderTypes(): MeowddingWaypoint = withRenderTypes(WaypointRenderType.TEXT, WaypointRenderType.BOX, WaypointRenderType.BEAM, WaypointRenderType.DISTANCE)
    public fun withAllRenderTypes(): MeowddingWaypoint = withRenderTypes(*WaypointRenderType.entries.toTypedArray())
    public fun withRenderTypes(vararg types: WaypointRenderType): MeowddingWaypoint = this.apply { this.renderTypes = types.toSet() }

    public fun withTags(vararg tags: MeowddingWaypointTag): MeowddingWaypoint = this.apply { this.tags = tags.toSet() }

    public fun inLocatorBar(boolean: Boolean = true): MeowddingWaypoint = this.apply { this.inLocatorBar = boolean }

    public fun withIgnoreY(boolean: Boolean = true): MeowddingWaypoint = this.apply { this.ignoreY = boolean }

    public fun addToHandler(): Unit = MeowddingWaypointHandler.addWaypoint(this)

    public fun distanceToSqr(other: Vec3): Double {
        return if (this.ignoreY) {
            other.let { it.distanceToSqr(this.position.x, it.y, this.position.z) }
        } else {
            other.distanceToSqr(this.position)
        }
    }

    private fun lerpedY(partialTicks: Float) = Mth.lerp(partialTicks.toDouble(), McPlayer.self!!.oldPosition().y, McPlayer.position!!.y)
    public fun getPosition(partialTicks: Float = 1f): Vec3 = if (ignoreY) {
        Vec3(position.x(), lerpedY(partialTicks), position.z())
    } else {
        position
    }

    public fun getBlockPos(partialTicks: Float = 1f): BlockPos = if (ignoreY) {
        BlockPos.containing(position.x(), lerpedY(partialTicks), position.z())
    } else {
        BlockPos.containing(position)
    }

    internal fun render(event: RenderWorldEvent, translucent: Boolean) {
        if (renderTypes.isEmpty()) return

        val position = getPosition(event.partialTicks)
        event.poseStack.translated(-0.5, 0.0, -0.5) {
            for (type in renderTypes) {
                when (type.takeUnless { it.translucent != translucent }) {
                    WaypointRenderType.TEXT -> event.renderTextInWorld(position, name)
                    WaypointRenderType.DISTANCE -> event.renderDistance(position)
                    WaypointRenderType.BOX -> event.renderBox(position, color)
                    WaypointRenderType.BEAM -> event.renderBeaconBeam(position, color)
                    WaypointRenderType.TRACER -> event.poseStack.translated(0.5, 0, 0.5) { event.renderLineFromCursor(position.add(0.0, 0.5, 0.0), color) }
                    else -> {}
                }
            }
        }
    }

    private fun RenderWorldEvent.renderDistance(position: Vec3) {
        val text = Text.of("Distance: ") {
            this.color = TextColor.GRAY
            append(McPlayer.position!!.distanceTo(position).round()) {
                this.color = TextColor.YELLOW
                append("m")
            }
        }
        this.renderTextInWorld(position.add(0.0, -0.5, 0.0), text, yOffset = if (WaypointRenderType.TEXT in renderTypes) McFont.height.toFloat() else 0f)
    }

    override fun hashCode(): Int = uuid.hashCode()
    override fun equals(other: Any?): Boolean = this === other || uuid == (other as? MeowddingWaypoint)?.uuid
    override fun toString(): String = "Waypoint(position=$position, uuid=$uuid, name=$name)"
}

public enum class WaypointRenderType(internal val translucent: Boolean = true) {
    BEAM(false),
    BOX,
    TEXT,
    DISTANCE,
    TRACER,
}
