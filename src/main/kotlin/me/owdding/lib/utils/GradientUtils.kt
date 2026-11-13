@file:Suppress("PackageDirectoryMismatch")
//~ if >= 26.4 'rendering.text.builtin' -> 'utils'
package me.owdding.lib.rendering.text.builtin

import com.mojang.serialization.Codec
import org.joml.Vector2f

fun interface GradientProvider {
    fun getColors(): List<Int>

    companion object {
        val CODEC: Codec<GradientProvider> = Codec.INT.listOf(1, MAX_VALUE).xmap(
            { GradientProvider { it } },
            { it.getColors() },
        )
    }
}

//? if >= 26.4 {
/*enum class GradientDirection(val vec: Vector2f) {
    LEFT(Vector2f(1f, 0f)),
    RIGHT(Vector2f(-1f, 0f)),
    UP(Vector2f(0f, -1f)),
    DOWN(Vector2f(0f, 1f)),
    DOWN_LEFT(Vector2f(1f, 1f)),
    DOWN_RIGHT(Vector2f(-1f, 1f)),
    UP_LEFT(Vector2f(1f, -1f)),
    UP_RIGHT(Vector2f(-1f, -1f)),
    ;
}
*///? }
