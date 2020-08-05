package fr.mattmouss.signs.util;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Letter {
    char letter;
    Vec2f beg;
    int length;
    int charPlace;
    static final char[][] lengthLetterMatrix = new char[][]{
            new char[]{'i','l','.'},
            new char[]{'j'},
            new char[]{'-','1','I','f','t'},
            buildFourLengthTab(),
            buildFiveLengthTab()
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


    public Letter(char letter,int x,int y){
        if (!isIn(letter)){
            throw new IllegalArgumentException("letter here is not implemented within glyph provider : "+letter);
        }
        this.letter = letter;
        Vec2f v = lowGetLengthAndPlace();
        this.length = (int) v.x;
        this.charPlace = (int) v.y;
        beg = new Vec2f(x,y);
    }

    public void render(BufferBuilder builder,int red,int green,int blue,int alpha){
        float completeLength = 10.0F/16;
        float pixelLength = completeLength/128;
        float x1 = completeLength-beg.x*pixelLength;
        float y1 = completeLength-beg.y*pixelLength;
        float x2 = completeLength-(beg.x+length)*pixelLength;
        float y2 = completeLength-(beg.y+7)*pixelLength;
        float u = getUMapping()/256.0F;
        float v = getVMapping()/256.0F;
        float du = length/256.0F;
        float dv = 7/256.0F;
        float z = -0.003F;
        builder.pos(x1,y1,z).tex(u,v).color(red, green, blue, alpha).endVertex();
        builder.pos(x1,y2,z).tex(u,v+dv).color(red, green, blue, alpha).endVertex();
        builder.pos(x2,y2,z).tex(u+du,v+dv).color(red, green, blue, alpha).endVertex();
        builder.pos(x2,y1,z).tex(u+du,v).color(red, green, blue, alpha).endVertex();
    }

    public void renderOnScreen(BufferBuilder builder,int red,int green,int blue,int alpha,int guiLeft,int guiTop){
        float u = getUMapping()/256.0F;
        float v = getVMapping()/256.0F;
        int x1 = (int)beg.x+guiLeft;
        int y1 = (int)beg.y+guiTop;
        int x2 = x1+length;
        int y2 = y1+7;
        builder.pos(x1,y1,0).tex(u,v).color(red,green,blue,alpha).endVertex();
        builder.pos(x1,y2,0).tex(u,v+7/256.0F).color(red,green,blue,alpha).endVertex();
        builder.pos(x2,y2,0).tex(u+length/256.0F,v+7/256.0F).color(red,green,blue,alpha).endVertex();
        builder.pos(x2,y1,0).tex(u+length/256.0F,v).color(red,green,blue,alpha).endVertex();
    }

    private int getUMapping(){
        return charPlace*length;
    }

    private int getVMapping(){
        if (length == 0)throw new IllegalArgumentException("unexpected error skip the isIn test or a isIn test is not working !");
        return (length-1)*7;
    }


    private Vec2f lowGetLengthAndPlace(){
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
        return new Vec2f(length,place.get());
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
