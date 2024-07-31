package fr.matt1999rd.signs.util;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;


public class Letter {
    char letter;
    float shift;
    int length;
    int charPlace;
    float scale;
    private final TextStyles styles;
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


    public Letter(char letter,float shift,TextStyles styles,float scale){
        if (!isIn(letter)){
            throw new IllegalArgumentException("letter here is not implemented within glyph provider : "+letter);
        }
        this.letter = letter;
        Vector2f v = lowGetLengthAndPlace();
        this.length = (int) v.x;
        this.charPlace = (int) v.y;
        this.shift = shift;
        this.styles = styles;
        this.scale = scale;
    }

    public int getLength(){
        int length = this.length;
        if (styles.isBold()){
            length++;
        }
        return length;
    }

    public void render(Matrix4f matrix4f, IVertexBuilder builder, Color color, int light){
        float x1 = shift;
        float y1 = 0;
        float x2 = x1 + length * scale;
        float y2 = y1 + Text.defaultHeight * scale;
        float italicOffset = scale * (styles.isItalic() ? TextStyles.italicOffset : 0.0F);
        if (styles.isBold()){
            drawBuffer(builder,matrix4f,x1+scale,y1,x2,y2,color,light,italicOffset);
        }
        drawBuffer(builder,matrix4f,x1,y1,x2,y2,color,light,italicOffset);
    }

    private void drawBuffer(IVertexBuilder builder, Matrix4f matrix4f, float x1, float y1, float x2, float y2, Color color, int light, float italicOffset){
        float u = getUMapping()/256.0F;
        float v = getVMapping()/256.0F;
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();
        builder.vertex(matrix4f,x1+italicOffset,y1,0).color(red,green,blue,alpha).uv(u,v).uv2(light).endVertex();
        builder.vertex(matrix4f,x1,y2,0).color(red,green,blue,alpha).uv(u,v+Text.defaultHeight/256.0F).uv2(light).endVertex();
        builder.vertex(matrix4f,x2,y2,0).color(red,green,blue,alpha).uv(u+length/256.0F,v+Text.defaultHeight/256.0F).uv2(light).endVertex();
        builder.vertex(matrix4f,x2+italicOffset,y1,0).color(red,green,blue,alpha).uv(u+length/256.0F,v).uv2(light).endVertex();
    }

    private int getUMapping(){
        return charPlace*length;
    }

    private int getVMapping(){
        if (length == 0)throw new IllegalArgumentException("unexpected error skip the isIn test or a isIn test is not working !");
        return (length-1)*Text.defaultHeight;
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
