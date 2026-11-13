package me.owdding.lib.overlays

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

public data class Rect(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
) {

    public constructor(pos: Position, width: Int, height: Int) : this(pos.component1(), pos.component2(), width, height)

    val right: Int
        get() = x + width

    val bottom: Int
        get() = y + height

    public fun contains(x: Number, y: Number): Boolean {
        return x.toInt() in this.x until right && y.toInt() in this.y until bottom
    }

    public operator fun times(scale: Float): Rect {
        return Rect(x, y, (width * scale).toInt(), (height * scale).toInt())
    }

    public companion object {

        public val CODEC: Codec<Rect> = RecordCodecBuilder.create {
            it.group(
                Codec.INT.fieldOf("x").forGetter(Rect::x),
                Codec.INT.fieldOf("y").forGetter(Rect::y),
                Codec.INT.fieldOf("width").forGetter(Rect::width),
                Codec.INT.fieldOf("height").forGetter(Rect::height),
            ).apply(it, ::Rect)
        }

        public fun codec(width: Int, height: Int): Codec<Rect> = RecordCodecBuilder.create {
            it.group(
                Codec.INT.fieldOf("x").forGetter(Rect::x),
                Codec.INT.fieldOf("y").forGetter(Rect::y),
            ).apply(it) { x, y -> Rect(x, y, width, height) }
        }
    }
}
