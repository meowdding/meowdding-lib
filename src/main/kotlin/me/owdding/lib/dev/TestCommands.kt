package me.owdding.lib.dev

import me.owdding.ktmodules.Module
import me.owdding.lib.waypoints.MeowddingWaypoint
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import java.util.*

@Module
object TestCommands {

    var waypoint: MeowddingWaypoint? = null

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.register("meowdding test") {
            thenCallback("display") {
                McClient.runNextTick {
                    McClient.setScreen(DisplayTest)
                }
            }

            thenCallback("waypoint") {
                waypoint = MeowddingWaypoint(UUID.randomUUID(), McPlayer.position!!) {
                    withName("Test Waypoint")
                    withColor(0xFF00FF00.toInt())
                    withAllRenderTypes()
                }
            }
        }
    }

    // TODO: REMOVE
    @Subscription
    fun onWorldRender(event: RenderWorldEvent) {
        waypoint?.render(event)
    }

}
