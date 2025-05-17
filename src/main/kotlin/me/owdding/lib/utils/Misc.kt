package me.owdding.lib.utils

import net.minecraft.world.entity.player.Player
import java.util.*

private val DEVS = setOf(
    "503450fc-72c2-4e87-8243-94e264977437",
    "e90ea9ec-080a-401b-8d10-6a53c407ac53",
    "b75d7e0a-03d0-4c2a-ae47-809b6b808246",
    "a1732122-e22e-4edf-883c-09673eb55de8",
)

fun Player.isMeowddingDev(): Boolean = this.stringUUID in DEVS
fun UUID.isMeowddingDev(): Boolean = this.toString() in DEVS
