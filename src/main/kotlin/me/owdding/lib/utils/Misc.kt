package me.owdding.lib.utils

import com.google.gson.JsonElement
import com.mojang.serialization.MapCodec
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.PermissionSet
import net.minecraft.world.entity.player.Player
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.util.*

//? < 26.3
//import tech.thatgravyboat.skyblockapi.utils.text.Text

// UUIDs of all maintainers
private val DEVS = setOf(
    "503450fc-72c2-4e87-8243-94e264977437", // Sophie
    "e90ea9ec-080a-401b-8d10-6a53c407ac53", // Mona
    "b75d7e0a-03d0-4c2a-ae47-809b6b808246", // Juna
    "a1732122-e22e-4edf-883c-09673eb55de8", // Maya
    "ecdf4cc9-0487-4d6f-bf09-8497deaf8b33", // Emma
    "e04c1a7f-017d-4de0-b76d-797cad8f8036", // Blox
    "16102479-7162-4ea9-9975-a5059c6a2be3", // Marie
)

//? < 26.3
//fun Player.isMeowddingDev(): Boolean = this.stringUUID in DEVS
fun Player.isMeowddingMaintainer(): Boolean = this.stringUUID in DEVS

//? < 26.3
//fun UUID.isMeowddingDev(): Boolean = this.toString() in DEVS
fun UUID.isMeowddingMaintainer(): Boolean = this.toString() in DEVS

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> Any?.unsafeCast(): T = this as T

fun FabricClientCommandSource.toCommandSourceStack(): CommandSourceStack {
    return CommandSourceStack(
        object : CommandSource {
            override fun sendSystemMessage(component: Component) {
                McPlayer.self?.sendSystemMessage(component)
            }

            override fun acceptsSuccess(): Boolean {
                return true
            }

            override fun acceptsFailure(): Boolean {
                return true
            }

            override fun shouldInformAdmins(): Boolean {
                return true
            }
        },
        McPlayer.position!!,
        this@toCommandSourceStack.rotation,
        null,
        PermissionSet.ALL_PERMISSIONS,
        //? < 26.3 {
        /*"FakeServerCommandSource",
        Text.of("FakeServerCommandSource"),
        *///? }
        null,
        McPlayer.self!!,
    )
}

internal fun <T : Any> JsonElement.toDataOrThrow(codec: MapCodec<T>) = this.toDataOrThrow(codec.codec())

fun <T : Enum<T>> T.next(): T {
    val constants = if (this.javaClass.isEnum) this.javaClass.enumConstants else this.javaClass.superclass.enumConstants
    return constants[(this.ordinal + 1) % constants.size].unsafeCast()
}

