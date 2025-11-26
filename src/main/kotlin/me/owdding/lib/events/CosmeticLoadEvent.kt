package me.owdding.lib.events

import me.owdding.lib.MeowddingLib
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

object CosmeticLoadEvent : SkyBlockEvent() {

    override fun post(bus: EventBus): Boolean = bus.post(this, null) {
        MeowddingLib.error("An error occurred while handling CosmeticLoadEvent", it)
    }
}
