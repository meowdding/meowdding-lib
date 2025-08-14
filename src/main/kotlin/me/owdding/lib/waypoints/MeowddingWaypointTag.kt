package me.owdding.lib.waypoints

fun interface MeowddingWaypointTag {
    val name get() = toString()

    override fun toString(): String

    companion object {
        /** Usable in other mods like SkyCubed Minimap */
        val SHARABLE: MeowddingWaypointTag = MeowddingWaypointTag { "SHARABLE" }
    }
}

