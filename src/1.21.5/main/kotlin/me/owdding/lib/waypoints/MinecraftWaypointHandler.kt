@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.waypoints

actual interface MinecraftWaypoint

actual object MinecraftWaypointHandler {

    actual fun addWaypoint(waypoint: MeowddingWaypoint): MinecraftWaypoint = error("Minecraft waypoints don't exist in 1.21.5")
    actual fun removeWaypoint(waypoint: MeowddingWaypoint): Boolean = error("Minecraft waypoints don't exist in 1.21.5")

}

