#version 330
#extension GL_ARB_separate_shader_objects : require

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
#include <minecraft:fog.glsl>
#endif

#include <minecraft:globals.glsl>
#include <minecraft:dynamictransforms.glsl>
#include <minecraft:oit.glsl>

uniform sampler2D Sampler0;

const vec4 colors[] = COLORS;
const vec2 direction = DIRECTION;
const float speed = SPEED;

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
layout(location = 0) in float sphericalVertexDistance;
layout(location = 1) in float cylindricalVertexDistance;
#endif

layout(location = 2) in vec4 vertexColor;
layout(location = 3) in vec2 texCoord0;

#ifndef OIT_ALPHA_ONLY
layout(location = 0) out vec4 fragColor;
#endif

vec4 SMOOTHY(float x) {
    x *= (colors.length() - 1);
    return mix(colors[int(x)], colors[int(x) + 1], smoothstep(0.0, 1.0, fract(x)));
}

vec4 calculateFinalColor(vec4 color) {
    #ifdef OIT_ACCUMULATE
    color = sampleColorForAccumulation(color);
    #endif

    #if !defined(IS_SEE_THROUGH) && !defined(IS_GUI)

    #ifdef OIT_ACCUMULATE
    vec4 fogColor = vec4(FogColor.rgb * color.a, FogColor.a);
    #else
    vec4 fogColor = FogColor;
    #endif

    color = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, fogColor);
    #endif

    return color;
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


    vec4 finalColor = color;

    if (length(finalColor.rgb) != 0.0) {
        vec2 coords = gl_FragCoord.xy;
        finalColor = vec4(SMOOTHY(float(int(length(coords + (direction * GameTime * 24000 * speed) * 2)) % 500) / 500.0).rgb, 1) * vertexColor;
    }

    #ifdef OIT_ALPHA_ONLY
    executeAlphaOnlyPhase(gl_FragCoord.z, finalColor.a);
    #else
    fragColor = calculateFinalColor(finalColor);
    #endif
}
