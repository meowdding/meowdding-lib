package me.owdding.lib.waypoints

import net.minecraft.world.waypoints.Waypoint

actual typealias MinecraftWaypoint = Waypoint

actual object MinecraftWaypointHandler {

    actual fun getWaypoints(): List<MinecraftWaypoint> = emptyList()
    actual fun getWaypoint(uuid: String): MinecraftWaypoint? = null
    actual fun addWaypoint(waypoint: MinecraftWaypoint) = Unit

}

