package me.owdding.lib.rendering.ui

import com.mojang.blaze3d.systems.RenderSystem

actual object UIRenderUtils {

    actual fun renderWithTransparency(alpha: Float, action: () -> Unit) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)
        action()
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    }

}
