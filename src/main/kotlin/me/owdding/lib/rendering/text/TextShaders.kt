package me.owdding.lib.rendering.text

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Style
import org.jetbrains.annotations.ApiStatus

object TextShaders {

    var activeShader: TextShader? = null
        @JvmStatic get
        @JvmStatic @ApiStatus.Internal set

    fun GuiGraphics.withTextShader(shader: TextShader?, action: () -> Unit) = pushPop(shader, action)

    fun pushPop(shader: TextShader?, action: () -> Unit) {
        activeShader = shader
        action()
        activeShader = null
    }
}
