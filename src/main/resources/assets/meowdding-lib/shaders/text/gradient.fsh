#version 150
precision highp int;

//? if > 1.21.5 {
!moj_import <minecraft:dynamictransforms.glsl>
!moj_import <minecraft:projection.glsl>
!moj_import <minecraft:globals.glsl>
!moj_import <minecraft:fog.glsl>
//?}

uniform sampler2D Sampler0;

const vec4 colors[] = COLORS;

//? if 1.21.5 {
/*uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
*///?} else {


in float sphericalVertexDistance;
in float cylindricalVertexDistance;
//?}
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

float from8Bit(int color) {
    return float(color) / 255.0;
}

vec4 SMOOTHY(float x) {
    x *= (colors.length() - 1);
    return mix(colors[int(x)], colors[int(x) + 1], smoothstep(0.0, 1.0, fract(x)));
}

float clampZeroOne(float value) {
    return min(max(value, 0), 1);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.1) {
        discard;
    }
    vec4 finalColor = vertexColor;

    if (length(finalColor.rgb) != 0.0) {
        vec2 coords = gl_FragCoord.xy;
        finalColor = vec4(SMOOTHY(float(int(coords.x + (GameTime * 24000) * 2) % 500) / 500.0).rgb, 1) * vertexColor;
    }

    //? if > 1.21.5 {
    fragColor = apply_fog(
        finalColor,
        sphericalVertexDistance, cylindricalVertexDistance,
        FogEnvironmentalStart, FogEnvironmentalEnd,
        FogRenderDistanceStart, FogRenderDistanceEnd,
        FogColor
    );
    //?} else {
    /*vec2 coords = gl_FragCoord.xy;

    fragColor = vec4(SMOOTHY(float(int(coords.x + (GameTime * 24000) * 2) % 500) / 500.0).rgb, 1) * vertexColor;
    *///?}
}
