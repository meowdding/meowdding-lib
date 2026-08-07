package me.owdding.lib.captcha

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import tech.thatgravyboat.skyblockapi.utils.text.Text

abstract class CaptchaWidget(
    val type: CaptchaType,
    val description: String,
) : AbstractWidget(0, 0, 256, 256, Text.of("Captcha Widget")) {
    abstract fun isCorrect(): Boolean

    override fun updateWidgetNarration(output: NarrationElementOutput?) {}
}
