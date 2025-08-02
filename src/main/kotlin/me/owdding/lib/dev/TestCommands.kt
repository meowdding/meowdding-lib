package me.owdding.lib.dev

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient

@Module
object TestCommands {

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.register("meowdding test") {
            thenCallback("display") {
                McClient.runNextTick {
                    McClient.setScreen(DisplayTest)
                }
            }
        }
    }

}
