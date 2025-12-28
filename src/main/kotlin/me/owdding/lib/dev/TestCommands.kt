package me.owdding.lib.dev

import me.owdding.ktmodules.Module
import me.owdding.lib.rendering.text.TextShaders
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import me.owdding.lib.rendering.text.textShader
import me.owdding.lib.utils.OutlineType
import me.owdding.lib.utils.RenderUtils.renderBox
import me.owdding.lib.utils.type.EnumArgumentType
import net.minecraft.ChatFormatting
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.util.ARGB
import net.minecraft.world.phys.AABB
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McClient.clipboard
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.command.EnumArgument
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJsonOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

@Module
object TestCommands {

    private var testRender: Pair<AABB, OutlineType>? = null

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
            thenCallback("boop") {
                Text.of("Meow") {
                    this.textShader = GradientTextShader(listOf(0, 0xFF, 0xFF00, 0xFF0000, 0xFF00FF, 0))
                }.send()
            }
            thenCallback("shader_serialization") {
                Text.of("Click to copy") {
                    clipboard = GradientTextShader(listOf(1, 2, 3, 4, 5, 6, 7, 8)).toJsonOrThrow(TextShaders.CODEC).toPrettyString()
                }.send()
            }
            then("render") {
                thenCallback("type", EnumArgument<OutlineType>()) {
                    val aabb = McPlayer.self?.blockPosition()?.let(::AABB) ?: return@thenCallback
                    testRender = aabb to argument<OutlineType>("type")
                    Text.of("Set test render!").send()
                }
            }
        }
    }

    @Subscription
    fun onRenderWorld(event: RenderWorldEvent) {
        val (aabb, type) = testRender ?: return
        event.renderBox(
            aabb,
            type,
            ARGB.color(
                255,
                TextColor.RED,
            ),
        )
    }

}
