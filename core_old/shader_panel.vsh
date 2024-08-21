# version 150
in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec2 UV2;

out vec4 vertexColor;
out vec2 lightmapUV;

void main() {
    gl_Position = vec4(Position, 1.0);
    vertexColor = Color;
    lightmapUV = UV2;
}