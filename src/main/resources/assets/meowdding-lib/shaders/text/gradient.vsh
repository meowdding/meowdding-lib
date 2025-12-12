#version 150

//!moj_import <minecraft:dynamictransforms.glsl>
//!moj_import <minecraft:projection.glsl>
//!moj_import <minecraft:fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

uniform sampler2D Sampler0;

//? if > 1.21.5 {
out float sphericalVertexDistance;
out float cylindricalVertexDistance;
//?} else
/*out float vertexDistance;*/
out vec4 vertexColor;
out vec2 texCoord0;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);


    //? if > 1.21.5 {
    sphericalVertexDistance = fog_spherical_distance(Position);
    cylindricalVertexDistance = fog_cylindrical_distance(Position);
    //?} else
    /*vertexDistance = fog_distance(Position, FogShape);*/

    vertexColor = Color;
    texCoord0 = UV0;
}
