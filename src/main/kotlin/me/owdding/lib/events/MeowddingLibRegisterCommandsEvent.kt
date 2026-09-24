package me.owdding.lib.events

import tech.thatgravyboat.skyblockapi.api.events.misc.AbstractModRegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent

internal class MeowddingLibRegisterCommandsEvent(
    baseEvent: RegisterCommandsEvent
) : AbstractModRegisterCommandsEvent(baseEvent, "meowdding", "mlib")
