#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
//in vec2 UV2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform sampler2D Sampler2;

out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    ivec2 light = ivec2(UV0);
    vertexColor = Color * texelFetch(Sampler2, light / 16, 0);
    //vertexColor = Color * texelFetch(Sampler2,test / 16,0);

    //int valMin = 536870907;
    //int valMax = 1073741827;
    /*
    int valMin = 1065353000 + 211;
    int valMax = 1065353000 + 221;
    if (UV2.x > valMin){
        vertexColor = vec4(1.0,0.0,0.0,1.0); //1e case : rouge
    }
    if (UV2.x > valMin + (valMax - valMin) / 10){
        vertexColor = vec4(0.0,1.0,0.0,1.0); //2e case : vert
    }
    if (UV2.x > valMin + 2 * (valMax - valMin) / 10){
        vertexColor = vec4(0.0,0.0,1.0,1.0); //3e case : bleu
    }
    if (UV2.x > valMin + 3 * (valMax - valMin) / 10){
        vertexColor = vec4(1.0,1.0,0.0,1.0); //4e case : rouge + vert = jaune
    }
    if (UV2.x > valMin + 4 * (valMax - valMin) / 10){
        vertexColor = vec4(0.0,1.0,1.0,1.0); //5e case : vert + bleu = cyan
    }
    if (UV2.x > valMin + 5 * (valMax - valMin) / 10){
        vertexColor = vec4(1.0,0.0,1.0,1.0); //6e case : rouge + bleu = magenta
    }
    if (UV2.x > valMin + 6 * (valMax - valMin) / 10){
        vertexColor = vec4(0.0,0.0,0.0,1.0); //7e case : noir
    }
    if (UV2.x > valMin + 7 * (valMax - valMin) / 10){
        vertexColor = vec4(0.5,0.5,0.5,1.0); //8e case : gris
    }
    if (UV2.x > valMin + 8 * (valMax - valMin) / 10){
        vertexColor = vec4(1.0,1.0,1.0,1.0); //9e case : blanc sans variation
    }
    if (UV2.x > valMin + 9 * (valMax - valMin) / 10){
        vertexColor = Color; //* texelFetch(Sampler2, UV2 / 16, 0); // 10e case : couleur sans ombre
    }
    */

}