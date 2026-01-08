package me.owdding.lib.waypoints

//? if > 1.21.5 {
import net.minecraft.world.waypoints.TrackedWaypoint
import net.minecraft.world.waypoints.Waypoint
import net.minecraft.world.waypoints.WaypointStyleAssets
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.util.Optional

typealias MinecraftWaypoint = Waypoint
//?} else
//interface MinecraftWaypoint

object MinecraftWaypointHandler {

    fun addWaypoint(meowddingWaypoint: MeowddingWaypoint): MinecraftWaypoint {
        //? if > 1.21.5 {
        val trackedWaypoint = TrackedWaypoint.setPosition(
            meowddingWaypoint.uuid,
            Waypoint.Icon(WaypointStyleAssets.DEFAULT, Optional.of(meowddingWaypoint.color)),
            meowddingWaypoint.getBlockPos(),
        )

        McClient.connection?.waypointManager?.trackWaypoint(trackedWaypoint)

        return trackedWaypoint
        //?} else
        //error()
    }
    fun removeWaypoint(waypoint: MeowddingWaypoint): Boolean {
        //? if > 1.21.5 {
        return waypoint.minecraftWaypoint?.let { McClient.connection?.waypointManager?.untrackWaypoint(it as TrackedWaypoint) } != null
        //?} else
        //error()
    }

    //? if 1.21.5 {
    /*private fun error(): Nothing = error("Minecraft waypoints don't exist in 1.21.5")
    *///?}

}

