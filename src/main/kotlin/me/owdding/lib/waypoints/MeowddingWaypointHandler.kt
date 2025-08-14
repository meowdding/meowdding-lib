package me.owdding.lib.waypoints

import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import me.owdding.lib.utils.suggestions.MeowddingSuggestionProviders
import me.owdding.lib.utils.toCommandSourceStack
import net.minecraft.commands.arguments.coordinates.BlockPosArgument
import net.minecraft.commands.arguments.coordinates.Coordinates
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription.Companion.HIGHEST
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.McVersionGroup
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.*
import kotlin.math.pow

@Module
object MeowddingWaypointHandler {

    private var _waypoints: MutableList<MeowddingWaypoint> = mutableListOf()
    val waypoints: List<MeowddingWaypoint> get() = _waypoints

    fun getWaypointsWithAnyTags(vararg tags: MeowddingWaypointTag): List<MeowddingWaypoint> = getWaypointsWithAnyTags(tags.toList())
    fun getWaypointsWithAnyTags(tags: Collection<MeowddingWaypointTag>): List<MeowddingWaypoint> = _waypoints.filter { it.tags.any { t -> tags.contains(t) } }
    fun getWaypointsWithAllTags(vararg tags: MeowddingWaypointTag): List<MeowddingWaypoint> = getWaypointsWithAllTags(tags.toList())
    fun getWaypointsWithAllTags(tags: Collection<MeowddingWaypointTag>): List<MeowddingWaypoint> = _waypoints.filter { it.tags.containsAll(tags) }

    fun addWaypoint(waypoint: MeowddingWaypoint) {
        _waypoints.add(waypoint)

        if (waypoint.inLocatorBar && !McVersionGroup.MC_1_21_5.isActive) {
            waypoint.minecraftWaypoint = MinecraftWaypointHandler.addWaypoint(waypoint)
        }
    }

    fun removeWaypoint(uuid: UUID) {
        _waypoints.find { it.uuid == uuid }?.let(::removeWaypoint)
    }

    fun removeWaypoint(waypoint: MeowddingWaypoint) {
        _waypoints.remove(waypoint)
        if (waypoint.inLocatorBar && !McVersionGroup.MC_1_21_5.isActive) {
            MinecraftWaypointHandler.removeWaypoint(waypoint)
        }
    }

    @Subscription(ServerChangeEvent::class, ServerDisconnectEvent::class, priority = HIGHEST)
    fun clearWaypoints() {
        _waypoints.toSet().forEach(::removeWaypoint)
    }

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.register("meowdding waypoint") {
            then("add") {
                thenCallback("coords", BlockPosArgument.blockPos()) {
                    val pos = this.getArgument("coords", Coordinates::class.java).getBlockPos(this.source.toCommandSourceStack())
                    MeowddingWaypoint(pos) {
                        withName("Waypoint $uuid")
                        withRandomColor()
                        withAllRenderTypes()
                        inLocatorBar()
                    }
                }
                callback {
                    MeowddingWaypoint(McPlayer.position!!) {
                        withName("Waypoint $uuid")
                        withRandomColor()
                        withAllRenderTypes()
                        inLocatorBar()
                    }
                }
            }
            then("remove") {
                thenCallback("all") { clearWaypoints() }
                thenCallback("nearest") {
                    val position = McPlayer.position ?: return@thenCallback
                    _waypoints.minByOrNull { it.distanceToSqr(position) }?.let(::removeWaypoint)
                }
                thenCallback("uuid uuid", StringArgumentType.string(), MeowddingSuggestionProviders.iterable(_waypoints) { it.uuid.toString() }) {
                    val uuid = this.getArgument("uuid", String::class.java)
                    _waypoints.find { it.uuid.toString() == uuid }?.let(::removeWaypoint)
                }
                thenCallback("name name", StringArgumentType.greedyString(), MeowddingSuggestionProviders.iterable(_waypoints) { it.name.stripped }) {
                    val name = this.getArgument("name", String::class.java)
                    _waypoints.find { it.name.stripped.equals(name, ignoreCase = true) }?.let(::removeWaypoint)
                }
            }
        }
    }

    @Subscription
    fun onTick(event: TickEvent) {
        val position = McPlayer.position ?: return
        _waypoints
            .filter { it.removalDistance != null && it.distanceToSqr(position) < (it.removalDistance?.pow(2)?.toInt() ?: Int.MIN_VALUE) }
            .forEach(::removeWaypoint)
    }

    @Subscription
    fun onRender(event: RenderWorldEvent.AfterTranslucent) {
        val position = McPlayer.position ?: return
        _waypoints
            .filter { it.renderCondition(event) }
            .sortedByDescending { it.distanceToSqr(position) }
            .forEach { it.render(event) }
    }

}
