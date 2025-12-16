@file:JvmName("ImplKt")
package me.owdding.lib.rendering.text

import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier

@Deprecated("This method is only here for backwards compatibility", level = DeprecationLevel.HIDDEN)
@JvmName("createTextRenderType")
internal fun oldCreateTextRenderType(
    shader: TextShader,
    location: Identifier,
): RenderType = createTextRenderType(shader, location)

@Deprecated("This method is only here for backwards compatibility", level = DeprecationLevel.HIDDEN)
@JvmName("textShader")
internal fun Style.oldTextShader(): TextShader? = textShader()

@Deprecated("This method is only here for backwards compatibility", level = DeprecationLevel.HIDDEN)
@JvmName("withTextShader")
internal fun Style.oldWithTextShader(shader: TextShader?): Style = withTextShader(shader)
