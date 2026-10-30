package me.owdding.lib.rendering.ui

import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.time.currentInstant
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.time.isDistantPast

@Module
object TitleManager {

    private val titles: Queue<Title> = LinkedList()
    private var currentTitleStartTime = Instant.DISTANT_PAST


    fun addTitle(
        title: String,
        stayTime: Duration = 3.seconds,
        fadeInTime: Duration = 1.seconds,
        fadeOutTime: Duration = 1.seconds,
    ) {
        addTitle(Title(Displays.text(title), stayTime, fadeInTime, fadeOutTime))
    }

    fun addTitle(
        component: Component,
        stayTime: Duration = 3.seconds,
        fadeInTime: Duration = 1.seconds,
        fadeOutTime: Duration = 1.seconds,
    ) {
        addTitle(Title(Displays.component(component), stayTime, fadeInTime, fadeOutTime))
    }


    fun addTitle(
        title: Display,
        stayTime: Duration = 3.seconds,
        fadeInTime: Duration = 1.seconds,
        fadeOutTime: Duration = 1.seconds,
    ) {
        addTitle(Title(title, stayTime, fadeInTime, fadeOutTime))
    }

    fun addTitle(builder: Title.() -> Unit) {
        val title = Title(
            title = Displays.empty(),
            stayTime = 3.seconds,
            fadeInTime = 1.seconds,
            fadeOutTime = 1.seconds,
        ).apply(builder)
        addTitle(title)
    }

    fun addTitle(title: Title) {
        titles.add(title)
    }

    @Subscription
    fun onRender(event: RenderHudEvent) {
        val title = titles.peek() ?: return

        if (currentTitleStartTime.isDistantPast) {
            currentTitleStartTime = currentInstant()
        }

        val currentTime = currentInstant()
        val elapsedTime = currentTime - currentTitleStartTime

        val totalDuration = title.fadeInTime + title.stayTime + title.fadeOutTime

        if (elapsedTime >= totalDuration) {
            titles.poll()
            currentTitleStartTime = Instant.DISTANT_PAST
            return
        }

        val alpha = when {
            elapsedTime < title.fadeInTime -> {
                elapsedTime.inWholeMilliseconds.toFloat() / title.fadeInTime.inWholeMilliseconds.toFloat()
            }

            elapsedTime < (title.fadeInTime + title.stayTime) -> {
                1.0f
            }

            else -> {
                val timeIntoFadeOut = elapsedTime - (title.fadeInTime + title.stayTime)
                1.0f - (timeIntoFadeOut.inWholeMilliseconds.toFloat() / title.fadeOutTime.inWholeMilliseconds.toFloat())
            }
        }

        val (width, height) = McClient.window.let { it.guiScaledWidth to it.guiScaledHeight }

        UIRenderUtils.renderWithTransparency(alpha) {
            title.title.render(event.graphics, (width * 0.5).toInt(), (height * 0.4).toInt(), 0.5f)
        }
    }


    data class Title(
        val title: Display,
        val stayTime: Duration,
        val fadeInTime: Duration,
        val fadeOutTime: Duration,
    )

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.register("meowdding dev title") {
            thenCallback("text", StringArgumentType.greedyString()) {
                addTitle(argument<String>("text") ?: "die")
            }
        }
    }
}
