package me.owdding.lib.captcha

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.ui.UIConstants
import me.owdding.lib.MeowddingLib
import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.displays.Alignment
import me.owdding.lib.displays.Displays
import me.owdding.lib.layouts.BackgroundWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

class CaptchaScreen(
    private val parent: Screen,
    private val captchaWidget: CaptchaWidget,
) : Screen(Text.of("Captcha Validation")) {

    override fun init() {
        super.init()
        BackgroundWidget(
            MeowddingLib.id("background"),
            LayoutFactory.vertical(2, 0.5f) {
                string("Captcha") {
                    color = TextColor.WHITE
                    bold = true
                }
                display(Displays.wrappedText(Text.of("Suspicious behavior detected!"), 256, textAlignment = Alignment.CENTER))
                display(Displays.wrappedText(Text.of(captchaWidget.description), 256, textAlignment = Alignment.CENTER))
                widget(captchaWidget)
                widget(
                    Widgets.button().apply {
                        withTexture(UIConstants.PRIMARY_BUTTON)
                        withSize(50, 20)
                        withRenderer(WidgetRenderers.text(Text.of("Verify", TextColor.WHITE)))
                        withCallback {
                            if (captchaWidget.isCorrect()) {
                                McClient.setScreen(parent)
                            } else {
                                CaptchaType.openRandom(parent, captchaWidget.type)
                            }
                        }
                    },
                ) { alignHorizontally(1f) }
            },
            5,
        ).apply {
            FrameLayout.centerInRectangle(this, this@CaptchaScreen.rectangle)
            addRenderableWidget(this)
        }
    }

    override fun onClose() {
        McClient.setScreen(parent)
    }
}
