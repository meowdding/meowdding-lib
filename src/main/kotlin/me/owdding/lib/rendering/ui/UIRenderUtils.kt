package me.owdding.lib.rendering.ui

expect object UIRenderUtils {

    fun renderWithTransparency(alpha: Float, action: () -> Unit)

}
