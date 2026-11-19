#version 330

//!moj_import <minecraft:globals.glsl>
//!moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

const vec4 colors[] = COLORS;
const vec2 direction = DIRECTION;
const float speed = SPEED;

in vec4 vertexColor;
out vec4 fragColor;

vec4 SMOOTHY(float x) {
    x *= (colors.length() - 1);
    return mix(colors[int(x)], colors[int(x) + 1], smoothstep(0.0, 1.0, fract(x)));
}

void main() {
    if (vertexColor.a < 0.1) {
        discard;
    }

    vec4 color = vertexColor * ColorModulator;

    if (length(color.rgb) != 0.0) {
        vec2 coords = gl_FragCoord.xy;
        color = vec4(SMOOTHY(float(int(length(coords + (direction * GameTime * 24000 * speed) * 2)) % 500) / 500.0).rgb, 1) * color;
    }

    fragColor = color;
}
