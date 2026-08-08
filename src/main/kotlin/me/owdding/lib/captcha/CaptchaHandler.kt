package me.owdding.lib.captcha

import net.minecraft.client.gui.screens.Screen

abstract class CaptchaHandler {
    abstract fun selectRandom(parent: Screen): CaptchaWidget
}
