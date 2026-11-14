#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:globals.glsl>
#include <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

const vec4 colors[] = COLORS;
const vec2 direction = DIRECTION;
const float speed = SPEED;

layout(location = 0) in vec2 texCoord0;
layout(location = 1) in vec4 vertexColor;

layout(location = 0) out vec4 fragColor;

vec4 SMOOTHY(float x) {
    x *= (colors.length() - 1);
    return mix(colors[int(x)], colors[int(x) + 1], smoothstep(0.0, 1.0, fract(x)));
}

void main() {
    #ifdef IS_GRAYSCALE
    vec4 texColor = texture(Sampler0, texCoord0).rrrr;
    #else
    vec4 texColor = texture(Sampler0, texCoord0);
    #endif

    if (texColor.a < 0.1) {
        discard;
    }

    vec4 color = texColor * vertexColor * ColorModulator;

    if (length(color.rgb) != 0.0) {
        vec2 coords = gl_FragCoord.xy;
        color = vec4(SMOOTHY(float(int(length(coords + (direction * GameTime * 24000 * speed) * 2)) % 500) / 500.0).rgb, 1) * color;
    }

    fragColor = color;
}
