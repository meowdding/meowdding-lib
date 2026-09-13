#version 330

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
//!moj_import <minecraft:fog.glsl>
#endif

//!moj_import <minecraft:globals.glsl>
//!moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

const vec4 colors[] = COLORS;
const vec2 direction = DIRECTION;
const float speed = SPEED;

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
in float sphericalVertexDistance;
in float cylindricalVertexDistance;
#endif

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;


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


    #ifdef IS_SEE_THROUGH
    vec4 color = texColor * vertexColor;
    #else
    vec4 color = texColor * vertexColor * ColorModulator;
    #endif


    vec4 finalColor = color;

    if (length(finalColor.rgb) != 0.0) {
        vec2 coords = gl_FragCoord.xy;
        finalColor = vec4(SMOOTHY(float(int(length(coords + (direction * GameTime * 24000 * speed) * 2)) % 500) / 500.0).rgb, 1) * vertexColor;
    }

    #ifdef IS_SEE_THROUGH
    fragColor = finalColor * ColorModulator;
    #elif defined(IS_GUI)
    fragColor = finalColor;
    #else
    fragColor = apply_fog(finalColor, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
    #endif
}
