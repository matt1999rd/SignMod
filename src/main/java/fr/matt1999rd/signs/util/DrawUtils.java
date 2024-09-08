package fr.matt1999rd.signs.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import java.awt.*;

public class DrawUtils {

    public static void renderQuad(Matrix4f matrix4f, VertexConsumer builder, float x1, float y1, float x2, float y2,float z, int color, int combinedLight){
        Color color1 = new Color(color,true);
        int red,green,blue,alpha;
        red = color1.getRed();
        green = color1.getGreen();
        blue = color1.getBlue();
        alpha = color1.getAlpha();
        // UV coordinate used for UV2 rendering UV2 is not given by minecraft code
        float uLighting = (float)(combinedLight & '\uffff');
        float vLighting = (float)(combinedLight >> 16 & '\uffff');
        builder.vertex(matrix4f,x1,y1,z).color(red,green,blue,alpha).uv(uLighting,vLighting).endVertex();
        builder.vertex(matrix4f,x1,y2,z).color(red,green,blue,alpha).uv(uLighting,vLighting).endVertex();
        builder.vertex(matrix4f,x2,y2,z).color(red,green,blue,alpha).uv(uLighting,vLighting).endVertex();
        builder.vertex(matrix4f,x2,y1,z).color(red,green,blue,alpha).uv(uLighting,vLighting).endVertex();
    }


    public static void renderRectangle(Matrix4f matrix4f, VertexConsumer builder, float x1, float y1, float x2, float y2, int color, int combinedLight){
        DrawUtils.renderQuad(matrix4f,builder,x1,y1,x2,y2,-0.001F/16F,color,combinedLight);
    }

    //function to render triangle with a y axes direction as one of its three sides
    // the three point are A(xCommon,yDown) , B(xSolo,ySolo) and C(xCommon,yUp)
    // the point H(xCommon,ySolo) is the intersection of the altitude from B and the opposite side of the triangle
    public static void renderTriangle(Matrix4f matrix4f, VertexConsumer builder, float xCommon, float xSolo, float yDown, float ySolo, float yUp, int color, int combinedLight, boolean isBgColor){
        float z = (isBgColor)? -0.002F/16F :-0.001F/16F;
        //RenderSystem.color4f(1.0F,1.0F,1.0F,1.0F);
        int rColor = Functions.getRedValue(color);
        int gColor = Functions.getGreenValue(color);
        int bColor = Functions.getBlueValue(color);
        int aColor = Functions.getAlphaValue(color);

        // UV coordinate used for UV2 rendering UV2 is not given by minecraft code
        float uLighting = (float)(combinedLight & '\uffff');
        float vLighting = (float)(combinedLight >> 16 & '\uffff');

        //startYUp boolean indicates the first vertex to draw in order to see the triangle (always render clockwise)
        // we always draw A first
        // startYUp = false
        /*
              C
           🡽 🡻
         🡽   🡻
        B     H
         🡼   🡻
           🡼 🡻
              A
         */
        // startYUp = true
        /*
         C
         🡹 🡾
         🡹   🡾
         H     B
         🡹   🡿
         🡹 🡿
         A
         */
        boolean startYUp = (xCommon<xSolo);
        builder.vertex(matrix4f,xCommon,yDown,z).color(rColor,gColor,bColor,aColor).uv(uLighting,vLighting).endVertex(); //draw A
        if (startYUp){
            builder.vertex(matrix4f,xCommon,ySolo,z).color(rColor,gColor,bColor,aColor).uv(uLighting,vLighting).endVertex(); //draw H
            builder.vertex(matrix4f,xCommon,yUp,z).color(rColor,gColor,bColor,aColor).uv(uLighting,vLighting).endVertex(); //draw C
            builder.vertex(matrix4f,xSolo,ySolo,z).color(rColor,gColor,bColor,aColor).uv(uLighting,vLighting).endVertex(); //draw B
        }else {
            builder.vertex(matrix4f,xSolo,ySolo,z).color(rColor,gColor,bColor,aColor).uv(uLighting,vLighting).endVertex(); //draw B
            builder.vertex(matrix4f,xCommon,yUp,z).color(rColor,gColor,bColor,aColor).uv(uLighting,vLighting).endVertex(); //draw C
            builder.vertex(matrix4f,xCommon,ySolo,z).color(rColor,gColor,bColor,aColor).uv(uLighting,vLighting).endVertex(); //draw H
        }

    }
}
