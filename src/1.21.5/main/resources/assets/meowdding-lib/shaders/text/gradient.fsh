#version 150
precision highp int;

uniform sampler2D Sampler0;

const vec4 colors[] = COLORS;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
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
    vec2 coords = gl_FragCoord.xy;

    fragColor = vec4(SMOOTHY(float(int(coords.x + (GameTime * 24000) * 2) % 500) / 500.0).rgb, 1) * vec4(vertexColor.rgb, 1.0);
}
