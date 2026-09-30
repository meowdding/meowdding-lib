package me.owdding.lib.rendering.uiui

actual object UIRenderUtils {

    actual fun renderWithTransparency(alpha: Float, action: () -> Unit) {
        // die
        action()
    }

}
