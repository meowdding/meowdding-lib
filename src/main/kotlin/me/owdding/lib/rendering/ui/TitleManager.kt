package me.owdding.lib.rendering.ui

import me.owdding.ktmodules.Module
import me.owdding.lib.displays.Display
import java.util.*
import kotlin.time.Duration

@Module
object TitleManager {

    private val titles: Queue<Title> = LinkedList()


    data class Title(
        val title: Display,
        val stayTime: Duration,
        val fadeInTime: Duration,
        val fadeOutTime: Duration,
    )
}
