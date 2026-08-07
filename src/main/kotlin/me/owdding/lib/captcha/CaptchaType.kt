package me.owdding.lib.captcha

import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.helpers.McClient

enum class CaptchaType(val captchaHandler: CaptchaHandler) {
    SELECT_SQUARES(SelectSquaresCaptcha),
    ;

    companion object {
        fun openRandom(parent: Screen) {
            val selectedCaptcha = entries.random()
            openRandom(parent, selectedCaptcha)
        }

        fun openRandom(parent: Screen, selectedCaptcha: CaptchaType) {
            val widget = selectedCaptcha.captchaHandler.selectRandom()
            McClient.setScreen(CaptchaScreen(parent, widget))
        }
    }
}
