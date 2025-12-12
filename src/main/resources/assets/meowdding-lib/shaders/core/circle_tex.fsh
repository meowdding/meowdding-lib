#version 150

//!moj_import <minecraft:dynamictransforms.glsl>
//!moj_import <minecraft:projection.glsl>

uniform sampler2D Sampler0;

//? if > 1.21.5 {
layout (std140) uniform MLibTexturedCircleUniform {
    vec4 uvs;
};
//?} else {
/*uniform vec4 ColorModulator;
uniform vec2 ScreenSize;

uniform vec4 uvs;
*///?}

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

// 2x PI
const float TAU = 6.2831853072;

void main() {
    float centerDist = length(texCoord0);
    float angle = (atan(texCoord0.y, texCoord0.x) / TAU) + 1.0;

    vec2 texPos = vec2(centerDist, mod(angle, 1.0));

    // Move to the correct uv position
    texPos.x = (uvs.y - uvs.x) * texPos.x + uvs.x;
    texPos.y = (uvs.w - uvs.z) * texPos.y + uvs.z;

    vec4 color = texture(Sampler0, texPos);

    if (color.a == 0 || centerDist > 1.0) {
        discard;
    }

    fragColor = color * ColorModulator;
}
