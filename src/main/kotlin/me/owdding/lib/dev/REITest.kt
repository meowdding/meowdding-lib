package me.owdding.lib.dev

import me.owdding.ktmodules.Module
import me.owdding.lib.compat.REIRuntimeCompatability
import me.owdding.lib.utils.KnownMods
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

@Module
object REITest {

    private var hoveredToggle = false

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        if (!KnownMods.REI.installed) return
        event.register("meowdding test rei") {
            thenCallback("searchbar") {
                val text = REIRuntimeCompatability.getCurrentSearchBar()
                val focused = REIRuntimeCompatability.isSearchBarHighlighting()

                if (text == null) {
                    Text.of("REI search bar is null!").send()
                } else {
                    Text.of("REI search bar has text: \"") {
                        append(text, TextColor.GOLD)
                        append("\", and focus is: ")
                        append(focused.toString(), TextColor.AQUA)
                    }.send()
                }
            }
            thenCallback("hovered") {
                hoveredToggle = !hoveredToggle
                Text.of("Debug Hovered toggle: $hoveredToggle").send()
            }
        }
    }

    @Subscription(TickEvent::class)
    @TimePassed("1s")
    fun onTick() {
        if (!hoveredToggle || !KnownMods.REI.installed) return
        val stack = REIRuntimeCompatability.getReiHoveredItemStack()
        val message = if (stack == null) Text.of("Not hovering any REI itemstack!")
        else Text.of("Hovering over: ").append(stack.hoverName)
        message.send("MLIB_REI_HOVER_TEST")
    }


}
