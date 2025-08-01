package me.owdding.lib.waypoints

import net.msrandom.stub.Stub

expect interface MinecraftWaypoint

@Stub
expect object MinecraftWaypointHandler {

    fun getWaypoints(): List<MinecraftWaypoint>
    fun getWaypoint(uuid: String): MinecraftWaypoint?
    fun addWaypoint(waypoint: MeowddingWaypoint): MinecraftWaypoint

}

