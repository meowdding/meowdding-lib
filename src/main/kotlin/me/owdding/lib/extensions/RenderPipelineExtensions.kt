package me.owdding.lib.extensions

import com.mojang.blaze3d.pipeline.RenderPipeline
import me.owdding.lib.accessor.RenderPipelineBuilderAccessor
import org.joml.*

fun RenderPipeline.Builder.withShaderDefine(name: String, array: IntArray): RenderPipeline.Builder {
    val accessor = this as RenderPipelineBuilderAccessor
    accessor.`meowddinglib$define`(name, "int[](${array.joinToString(", ") { it.toString() }})")
    return this
}

fun RenderPipeline.Builder.withShaderDefine(name: String, array: FloatArray): RenderPipeline.Builder {
    val accessor = this as RenderPipelineBuilderAccessor
    accessor.`meowddinglib$define`(name, "float[](${array.joinToString(", ") { it.toString() }})")
    return this
}

fun RenderPipeline.Builder.withShaderDefine(name: String, vector2f: Vector2f): RenderPipeline.Builder {
    val accessor = this as RenderPipelineBuilderAccessor
    accessor.`meowddinglib$define`(name, "vec2(${vector2f.x}, ${vector2f.y})")
    return this
}

fun RenderPipeline.Builder.withShaderDefine(name: String, array: Array<Vector2f>): RenderPipeline.Builder {
    val accessor = this as RenderPipelineBuilderAccessor
    accessor.`meowddinglib$define`(name, "vec2[](${array.joinToString(", ") { (x, y) -> "vec2($x, $y)" }})")
    return this
}

fun RenderPipeline.Builder.withShaderDefine(name: String, array: Array<Vector3f>): RenderPipeline.Builder {
    val accessor = this as RenderPipelineBuilderAccessor
    accessor.`meowddinglib$define`(name, "vec3[](${array.joinToString(", ") { (x, y, z) -> "vec3($x, $y, $z)" }})")
    return this
}

fun RenderPipeline.Builder.withShaderDefine(name: String, array: Array<Vector4f>): RenderPipeline.Builder {
    val accessor = this as RenderPipelineBuilderAccessor
    accessor.`meowddinglib$define`(name, "vec4[](${array.joinToString(", ") { (x, y, z, w) -> "vec4($x, $y, $z, $w)" }})")
    return this
}
