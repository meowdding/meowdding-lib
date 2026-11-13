package me.owdding.lib.waypoints

public class ExpellingWaypoint {

    private var waypoint: MeowddingWaypoint? = null

    public operator fun getValue(thisRef: Any?, property: Any?): MeowddingWaypoint? = waypoint
    public operator fun invoke(): MeowddingWaypoint? = waypoint

    public operator fun setValue(thisRef: Any?, property: Any?, value: MeowddingWaypoint?) {
        waypoint?.let(MeowddingWaypointHandler::removeWaypoint)
        waypoint = value
        value?.let(MeowddingWaypointHandler::addWaypoint)
    }

}

/**
 * Using this requires doing a SelfAssignment since MutableList operations don't use setValue()
 * and I was too lazy to implement a MutableList wrapper.
 */
public class ExpellingWaypointList {

    private val waypoints: MutableList<MeowddingWaypoint> = mutableListOf()

    public operator fun getValue(thisRef: Any?, property: Any?): MutableList<MeowddingWaypoint> = waypoints
    public operator fun invoke(): MutableList<MeowddingWaypoint> = waypoints

    public operator fun setValue(thisRef: Any?, property: Any?, value: Iterable<MeowddingWaypoint>) {
        val copy = value.toSet()
        waypoints.forEach(MeowddingWaypointHandler::removeWaypoint)
        waypoints.clear()
        waypoints.addAll(copy)
        copy.forEach(MeowddingWaypointHandler::addWaypoint)
    }
}
