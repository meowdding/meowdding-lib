package me.owdding.lib.waypoints

import net.msrandom.stub.Stub

@Stub
expect interface MinecraftWaypoint

@Stub
expect object MinecraftWaypointHandler {

    fun addWaypoint(meowddingWaypoint: MeowddingWaypoint): MinecraftWaypoint
    fun removeWaypoint(waypoint: MeowddingWaypoint): Boolean

}

