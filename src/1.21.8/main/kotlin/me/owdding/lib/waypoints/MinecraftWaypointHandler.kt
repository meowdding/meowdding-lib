@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.waypoints

import net.minecraft.world.waypoints.TrackedWaypoint
import net.minecraft.world.waypoints.Waypoint
import net.minecraft.world.waypoints.WaypointStyleAssets
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.util.*

actual typealias MinecraftWaypoint = Waypoint

actual object MinecraftWaypointHandler {

    actual fun getWaypoints(): List<MinecraftWaypoint> = emptyList()
    actual fun getWaypoint(uuid: String): MinecraftWaypoint? = null
    actual fun addWaypoint(waypoint: MeowddingWaypoint): MinecraftWaypoint {
        val waypoint = TrackedWaypoint.setPosition(
            waypoint.uuid,
            Waypoint.Icon(WaypointStyleAssets.DEFAULT, Optional.of(waypoint.color)),
            waypoint.blockPos,
        )

        McClient.connection?.waypointManager?.trackWaypoint(waypoint)

        return waypoint
    }

}

