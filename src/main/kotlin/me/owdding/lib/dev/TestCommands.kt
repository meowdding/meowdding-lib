package me.owdding.lib.dev

import me.owdding.ktmodules.Module
import me.owdding.lib.utils.type.EnumArgumentType
import net.minecraft.ChatFormatting
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send

@Module
object TestCommands {

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.register("meowdding test") {
            thenCallback("display") {
                McClient.setScreenAsync { DisplayTest }
            }
            then("enum enum", EnumArgumentType(ChatFormatting::class)) {
                callback {
                    val enum = getArgument("enum", ChatFormatting::class.java)
                    Text.of(enum.name).send()
                }
            }
        }
    }

}
