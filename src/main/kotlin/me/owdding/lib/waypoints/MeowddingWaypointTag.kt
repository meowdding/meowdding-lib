package me.owdding.lib.waypoints

fun interface MeowddingWaypointTag {
    val name get() = toString()

    override fun toString(): String

    companion object {
        /** Usable in other mods like SkyCubed Minimap, the same mods will be able to edit/remove the waypoints though */
        val SHARABLE: MeowddingWaypointTag = MeowddingWaypointTag { "SHARABLE" }
    }
}

