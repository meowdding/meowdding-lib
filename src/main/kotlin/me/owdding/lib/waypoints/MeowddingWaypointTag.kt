package me.owdding.lib.waypoints

public fun interface MeowddingWaypointTag {
    public val name: String get() = toString()

    override fun toString(): String

    public companion object {
        /** Usable in other mods like SkyCubed Minimap, the same mods will be able to edit/remove the waypoints though */
        public val SHARABLE: MeowddingWaypointTag = MeowddingWaypointTag { "SHARABLE" }
    }
}

