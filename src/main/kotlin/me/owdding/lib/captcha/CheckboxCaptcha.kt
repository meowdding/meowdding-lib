package me.owdding.lib.captcha

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.extentions.fromNow
import tech.thatgravyboat.skyblockapi.utils.extentions.isInPast
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class CheckboxCaptcha(
    private val parentScreen: Screen
) : CaptchaWidget(CaptchaType.CHECKBOX, "Click the checkbox to verify you are human", 25) {

    private enum class State { IDLE, LOADING, SUCCESS, FAILED }

    private var state = State.IDLE
    private var loadingEndTime = Instant.DISTANT_PAST
    private var willFail = false

    private val text = "I'm not a robot"
    private val boxSize = 24
    private val spacing = 8

    override fun isCorrect(): Boolean = state == State.SUCCESS

    override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val totalWidth = boxSize + spacing + McFont.width(text)

        val startX = this.x + (this.width / 2) - (totalWidth / 2)
        val boxY = this.y + (this.height / 2) - (boxSize / 2)

        val textX = startX + boxSize + spacing
        val textY = this.y + (this.height / 2) - (McFont.height / 2)

        graphics.drawString(text, textX, textY)

        graphics.fill(startX, boxY, startX + boxSize, boxY + boxSize, 0xFFFFFFFF.toInt())
        graphics.fill(startX + 2, boxY + 2, startX + boxSize - 2, boxY + boxSize - 2, 0xFF000000.toInt())

        when (state) {
            State.LOADING -> {
                if (loadingEndTime.isInPast()) {
                    if (willFail) {
                        state = State.FAILED
                    } else {
                        state = State.SUCCESS
                        McClient.setScreen(parentScreen)
                    }
                } else {
                    // TODO: loading animation
                }
            }
            State.SUCCESS -> graphics.fill(startX + 4, boxY + 4, startX + boxSize - 4, boxY + boxSize - 4, 0xFF00FF00.toInt())
            State.FAILED -> graphics.fill(startX + 4, boxY + 4, startX + boxSize - 4, boxY + boxSize - 4, 0xFFFF0000.toInt())
            State.IDLE -> {}
        }
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        val totalWidth = boxSize + spacing + McFont.width(text)

        val startX = this.x + (this.width / 2) - (totalWidth / 2)
        val boxY = this.y + (this.height / 2) - (boxSize / 2)

        if (event.x >= startX && event.x <= startX + totalWidth && event.y >= boxY && event.y <= boxY + boxSize) {
            if (state == State.IDLE || state == State.FAILED) {
                state = State.LOADING

                loadingEndTime = Random.nextLong(1000, 3500).milliseconds.fromNow()

                willFail = Random.nextFloat() < 0.3f
                return true
            }
        }
        return super.mouseClicked(event, doubleClick)
    }

    companion object : CaptchaHandler() {
        override fun selectRandom(parent: Screen): CaptchaWidget = CheckboxCaptcha(parent)
    }
}
