package me.owdding.lib.dev

import me.owdding.ktmodules.Module
import me.owdding.lib.waypoints.Waypoint
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer

@Module
object TestCommands {

    var waypoint: Waypoint? = null

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.register("meowdding test") {
            thenCallback("display") {
                McClient.runNextTick {
                    McClient.setScreen(DisplayTest)
                }
            }

            thenCallback("waypoint") {
                waypoint = Waypoint(-1, McPlayer.position!!) {
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
