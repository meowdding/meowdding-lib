package me.owdding.lib.rendering.text

import net.minecraft.client.gui.GuiGraphics

object TextShaders {

    var activeShader: TextShader? = null
        @JvmStatic get
        private set

    fun GuiGraphics.withTextShader(shader: TextShader?, action: () -> Unit) = pushPop(shader, action)

    fun pushPop(shader: TextShader?, action: () -> Unit) {
        activeShader = shader
        action()
        activeShader = null
    }
}
