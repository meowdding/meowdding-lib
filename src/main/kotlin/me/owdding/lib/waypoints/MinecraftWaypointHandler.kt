package me.owdding.lib.waypoints

import net.minecraft.world.waypoints.TrackedWaypoint
import net.minecraft.world.waypoints.Waypoint
import net.minecraft.world.waypoints.WaypointStyleAssets
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.util.Optional

typealias MinecraftWaypoint = Waypoint

object MinecraftWaypointHandler {

    fun addWaypoint(meowddingWaypoint: MeowddingWaypoint): MinecraftWaypoint {
        val trackedWaypoint = TrackedWaypoint.setPosition(
            meowddingWaypoint.uuid,
            Waypoint.Icon(WaypointStyleAssets.DEFAULT, Optional.of(meowddingWaypoint.color)),
            meowddingWaypoint.getBlockPos(),
        )

        McClient.connection?.waypointManager?.trackWaypoint(trackedWaypoint)

        return trackedWaypoint
    }
    fun removeWaypoint(waypoint: MeowddingWaypoint): Boolean {
        return waypoint.minecraftWaypoint?.let { McClient.connection?.waypointManager?.untrackWaypoint(it as TrackedWaypoint) } != null
    }

}

