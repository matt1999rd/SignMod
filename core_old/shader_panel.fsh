# version 150
in vec4 vertexColor;
in vec2 lightmapUV;
uniform sampler2D Lightmap;

out vec4 fragColor;

void main(){
    vec4 lightmapColor = texture(Lightmap,lightmapUV);
    fragColor = vertexColor * lightmapColor;
}