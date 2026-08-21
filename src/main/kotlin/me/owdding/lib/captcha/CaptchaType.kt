package me.owdding.lib.captcha

import me.owdding.ktmodules.Module
import me.owdding.lib.compat.meowdding.MeowddingModsScreen
import me.owdding.lib.utils.type.EnumArgumentType
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient

enum class CaptchaType(val captchaHandler: CaptchaHandler, val verifyManually: Boolean) {
    SELECT_SQUARES(SelectSquaresCaptcha, true),
    CHECKBOX(CheckboxCaptcha, false),
    ;

    @Module
    companion object {
        fun openRandom(parent: Screen) {
            val selectedCaptcha = entries.random()
            openRandom(parent, selectedCaptcha)
        }

        fun openRandom(parent: Screen, selectedCaptcha: CaptchaType) {
            val widget = selectedCaptcha.captchaHandler.selectRandom(parent)
            McClient.setScreen(CaptchaScreen(parent, widget))
        }

        @Subscription
        fun onCommand(event: RegisterCommandsEvent) {
            event.register("meowdding captcha") {
                thenCallback("type", EnumArgumentType(CaptchaType::class)) {
                    val type = getArgument("type", CaptchaType::class.java)
                    openRandom(MeowddingModsScreen(), type)
                }

                callback {
                    openRandom(MeowddingModsScreen())
                }
            }
        }
    }
}
