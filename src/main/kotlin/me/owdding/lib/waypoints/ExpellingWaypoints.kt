package me.owdding.lib.waypoints

class ExpellingWaypoint {

    private var waypoint: MeowddingWaypoint? = null

    operator fun getValue(thisRef: Any?, property: Any?): MeowddingWaypoint? = waypoint
    operator fun invoke() = waypoint

    operator fun setValue(thisRef: Any?, property: Any?, value: MeowddingWaypoint?) {
        waypoint?.let(MeowddingWaypointHandler::removeWaypoint)
        waypoint = value
        value?.let(MeowddingWaypointHandler::addWaypoint)
    }

}

class ExpellingWaypointList {

    private val waypoints: MutableList<MeowddingWaypoint> = mutableListOf()

    operator fun getValue(thisRef: Any?, property: Any?): MutableList<MeowddingWaypoint> = waypoints
    operator fun invoke() = waypoints

    operator fun setValue(thisRef: Any?, property: Any?, value: Iterable<MeowddingWaypoint>) {
        val copy = value.toSet()
        waypoints.forEach(MeowddingWaypointHandler::removeWaypoint)
        waypoints.clear()
        waypoints.addAll(copy)
        copy.forEach(MeowddingWaypointHandler::addWaypoint)
    }

}
