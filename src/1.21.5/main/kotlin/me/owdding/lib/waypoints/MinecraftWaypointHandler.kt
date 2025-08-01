package me.owdding.lib.waypoints

actual interface MinecraftWaypoint

actual object MinecraftWaypointHandler {

    actual fun getWaypoints(): List<MinecraftWaypoint> = error("Minecraft waypoints don't exist in 1.21.5")
    actual fun getWaypoint(uuid: String): MinecraftWaypoint? = error("Minecraft waypoints don't exist in 1.21.5")
    actual fun addWaypoint(waypoint: MinecraftWaypoint) = run { error("Minecraft waypoints don't exist in 1.21.5") }

}

