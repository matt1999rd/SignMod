package fr.matt1999rd.signs.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;


public class Letter {
    char letter;
    Vector2f beg;
    int length;
    int charPlace;
    static final char[][] lengthLetterMatrix = new char[][]{
            new char[]{'i','l','.'},
            new char[]{'j'},
            new char[]{'-','1','I','f','t'},
            buildFourLengthTab(),
            buildFiveLengthTab()
    };

    public static final Predicate<String> VALIDATOR_FOR_TEXT_DISPLAY = s -> {
        int n=s.length();
        for (int i=0;i<n;i++){
            char c0 = s.charAt(i);
            if (!Letter.isIn(c0) && c0 != ' '){
                return false;
            }
        }
        return true;
    };

    private static char[] buildFourLengthTab() {
        List<Character> list = new ArrayList<>();
        for (int i=97;i<123 ;i++){
            if (i!='f' && i!='i' && i!='j' && i!='l' && i!='m' && i!='t' && i!='w') list.add((char) i);
        }
        return Functions.toCharArray(list);
    }

    private static char[] buildFiveLengthTab() {
        List<Character> list = new ArrayList<>();
        for (int i=48;i<91 ;i++){
            if (i!='1' && i!='I' && (i<58 || i>64))list.add((char) i);
        }
        list.add('m');
        list.add('w');
        return Functions.toCharArray(list);
    }


    public Letter(char letter,float x,float y){
        if (!isIn(letter)){
            throw new IllegalArgumentException("letter here is not implemented within glyph provider : "+letter);
        }
        this.letter = letter;
        Vector2f v = lowGetLengthAndPlace();
        this.length = (int) v.x;
        this.charPlace = (int) v.y;
        beg = new Vector2f(x,y);
    }

    public void render(Matrix4f matrix4f, IVertexBuilder builder, int red, int green, int blue, int alpha, int scale, float xOrigin, float yOrigin, float zOrigin, float pixelLength, float pixelHeight,int light){
        float x1 = xOrigin-beg.x*pixelLength;
        float y1 = yOrigin-beg.y*pixelHeight;
        float x2 = xOrigin-(beg.x+length*scale)*pixelLength;
        float y2 = yOrigin-(beg.y+7*scale)*pixelHeight;
        float u = getUMapping()/256.0F;
        float v = getVMapping()/256.0F;
        float du = length/256.0F;
        float dv = 7/256.0F;
        builder.vertex(matrix4f,x1,y1,zOrigin).color(red, green, blue, alpha).uv(u,v).uv2(light).endVertex();
        builder.vertex(matrix4f,x1,y2,zOrigin).color(red, green, blue, alpha).uv(u,v+dv).uv2(light).endVertex();
        builder.vertex(matrix4f,x2,y2,zOrigin).color(red, green, blue, alpha).uv(u+du,v+dv).uv2(light).endVertex();
        builder.vertex(matrix4f,x2,y1,zOrigin).color(red, green, blue, alpha).uv(u+du,v).uv2(light).endVertex();
    }

    public void renderOnScreen(Matrix4f matrix4f,BufferBuilder builder, int guiLeft, int guiTop,Color color, float scaleX, float scaleY, boolean positionUnchangedByScale){
        RenderSystem.enableBlend();
        float u = getUMapping()/256.0F;
        float v = getVMapping()/256.0F;
        float x1,y1;
        if (positionUnchangedByScale){
            x1 = guiLeft+ beg.x;
            y1 = guiTop + beg.y;
        } else {
            x1 = beg.x * scaleX + guiLeft;
            y1 = beg.y * scaleY + guiTop;
        }
        float x2 = x1+length*scaleX;
        float y2 = y1+7*scaleY;
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();
        builder.vertex(matrix4f,x1,y1,0).color(red,green,blue,alpha).uv(u,v).endVertex();
        builder.vertex(matrix4f,x1,y2,0).color(red,green,blue,alpha).uv(u,v+7/256.0F).endVertex();
        builder.vertex(matrix4f,x2,y2,0).color(red,green,blue,alpha).uv(u+length/256.0F,v+7/256.0F).endVertex();
        builder.vertex(matrix4f,x2,y1,0).color(red,green,blue,alpha).uv(u+length/256.0F,v).endVertex();
    }

    private int getUMapping(){
        return charPlace*length;
    }

    private int getVMapping(){
        if (length == 0)throw new IllegalArgumentException("unexpected error skip the isIn test or a isIn test is not working !");
        return (length-1)*7;
    }


    private Vector2f lowGetLengthAndPlace(){
        int length = 0;
        boolean isFound = false;
        AtomicInteger place = new AtomicInteger(0);
        for (int i=0;i<5 && !isFound;i++){
            place.set(0);
            if (isIn(i,this.letter,place)){
                length=i+1;
                isFound = true;
            }
        }
        return new Vector2f(length,place.get());
    }

    private boolean isIn(int letterLength, char letter,AtomicInteger i) {
        char[] charTab =lengthLetterMatrix[letterLength];
        for (char c : charTab){
            if (c == letter){
                return true;
            }
            i.incrementAndGet();
        }
        return false;
    }

    public static boolean isNumber(char letter){
        return (letter>='0' && letter<='9');
    }



    public static boolean isIn(char letter){
        for (char[] c : lengthLetterMatrix){
            for (char c0 : c){
                if (c0 == letter){
                    return true;
                }
            }
        }
        return false;
    }
}
