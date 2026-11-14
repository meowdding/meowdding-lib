package me.owdding.lib.utils

import com.mojang.serialization.Codec
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import org.joml.Vector2f

public fun interface GradientProvider {
    public fun getColors(): List<Int>

    public companion object {
        public val CODEC: Codec<GradientProvider> = Codec.INT.listOf(1, MAX_VALUE).xmap(
            { GradientProvider { it } },
            { it.getColors() },
        )
    }
}

public enum class GradientDirection(public val vec: Vector2f) {
    LEFT(Vector2f(1f, 0f)),
    RIGHT(Vector2f(-1f, 0f)),
    UP(Vector2f(0f, -1f)),
    DOWN(Vector2f(0f, 1f)),
    DOWN_LEFT(Vector2f(1f, 1f)),
    DOWN_RIGHT(Vector2f(-1f, 1f)),
    UP_LEFT(Vector2f(1f, -1f)),
    UP_RIGHT(Vector2f(-1f, -1f)),
    ;

    //? if < 26.4
    @Suppress("DEPRECATION")
    public fun toOld(): GradientTextShader.Direction? = GradientTextShader.Direction.entries.find { it.direction == this }
}
