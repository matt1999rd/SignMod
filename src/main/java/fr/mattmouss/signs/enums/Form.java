package fr.mattmouss.signs.enums;

import fr.mattmouss.signs.util.Functions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import sun.nio.cs.ext.MacHebrew;

import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum Form {
    UPSIDE_TRIANGLE(0,"let_way_panel",vec2f -> {
        return (vec2f.y>0 && vec2f.x>vec2f.y/2 && vec2f.x<1-vec2f.y/2);
    },h->{
        return h/2; //start text when you can fit a rectangle of height h
    },h->{
        return 127-h; // a max length of 128-h/2-h/2
    },h->{
        return 0; // the y needed to get max length is 0
    }),
    TRIANGLE(1,"triangle_panel",vec2f -> {
        return (vec2f.y<=1 && vec2f.y>=MathHelper.abs(2*vec2f.x-1));
    },h->{
        return h/2;
    },h->{
        return 127-h;
    },h->{
        return 127-h;
    }),
    OCTOGONE(2,"stop_panel",vec2f -> {
        return (1-3*vec2f.y<3*vec2f.x && 3*vec2f.x<5-3*vec2f.y) &&
                (0<vec2f.x && vec2f.x<1) &&
                (0<vec2f.y && vec2f.y<1) &&
                (3*vec2f.y-2<3*vec2f.x && 3*vec2f.x<3*vec2f.y+2);
    },h->{
        return 0;
    },h->{
        return 127;
    },h->{
        return 50;
    }),
    CIRCLE(3,"circle_panel",(vec2f -> {
        return (vec2f.x-0.5)*(vec2f.x-0.5)+(vec2f.y-0.5)*(vec2f.y-0.5)<=0.5*0.5;
    }),h->{
        int k = MathHelper.ceil(64+MathHelper.sqrt(64.0F-h*h/2.0F));
        return k;
    },h->{
        int k = MathHelper.ceil(64+MathHelper.sqrt(64.0F-h*h/2.0F));
        return 127-2*k;
    },h->{
        return 64-h/2;
    }),
    SQUARE(4,"square_panel",(vec2f -> {
        return (vec2f.x<=1 && vec2f.x>=0 && vec2f.y<=1 && vec2f.y>=0);
    }),h->{
        return 0;
    },h->{
        return 127;
    },h->{
        return 0;
    }),
    RECTANGLE(5,"rectangle_panel",vec2f -> {
        return (vec2f.y>0.4 && vec2f.y<0.6 && vec2f.x>0 && vec2f.x<1);
    },h->{
        return 0;
    },h->{
        return 127;
    },h->{
        return 0;
    }),
    ARROW(6,"direction_panel",vec2f -> {
        return (vec2f.y > 0.4 && vec2f.y < 0.6 && vec2f.x > 0 && vec2f.x < 1);
    },h->{
        return 0;
    },h->{
        return 127;
    },h->{
        return 0;
    }),
    PLAIN_SQUARE(7,"huge_direction_panel",vec2f -> {
        return (vec2f.x<1 && vec2f.x>0 && vec2f.y<1 && vec2f.y>0);
    },h->{
        return 0;
    },h->{
        return 127;
    },h->{
        return 0;
    }),
    DIAMOND(8,"diamond_panel",vec2f -> {
        return 2*vec2f.x>= MathHelper.abs(2*vec2f.y-1) && 2*vec2f.x<=2-MathHelper.abs(1-2*vec2f.y);
    },h->{
        return h/2;
    },h->{
        return 127-h;
    },h->{
        return 64-h/2;
    });

    private final String block_name;
    private final int meta;
    private final Predicate<Vec2f> isIn;
    private final IntFunction<Integer> xBegText;
    private final IntFunction<Integer> maxLength;
    private final IntFunction<Integer> yBegText;

    Form(int meta, String block_name, Predicate<Vec2f> isIn,IntFunction<Integer> xBeg,IntFunction<Integer> maxLength,IntFunction<Integer> yBegText){
        this.meta = meta;
        this.block_name = block_name;
        this.isIn = isIn;
        this.xBegText = xBeg;
        this.maxLength = maxLength;
        this.yBegText = yBegText;
    }

    public String getRegistryName(){
        return block_name;
    }

    public static Form byIndex(int meta){
        Form[] forms = Form.values();
        if (meta>8 || meta<0){
            return null;
        }
        return forms[meta];
    }

    public int getMeta(){
        return meta;
    }

    //return true if i and j coordinates that are between 0 and 128 are in the form figure
    public boolean isIn(int i,int j){
        float completeLength = 128;
        float x = (i+0.5F)/completeLength;
        float y = (j+0.5F)/completeLength;
        Vec2f vec2f = new Vec2f(x,y);
        return isIn.test(vec2f);
    }

    public boolean rectangleIsIn(int i_min,int i_max,int j_min,int j_max){
        for (int i=i_min;i<i_max+1;i++){
            for (int j=j_min;j<j_max+1;j++){
                if (!isIn(i,j)){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isOnWholeCube(){
        return (this == PLAIN_SQUARE || this == ARROW || this == RECTANGLE);
    }

    public int getCompleteLength(){
        return (isOnWholeCube())? 16 : 10;
    }

    public boolean isForDrawing() {
        return (this == TRIANGLE || this == CIRCLE || this == SQUARE || this == DIAMOND);
    }

    public int getMaxLength(int textHeight){
        return maxLength.apply(textHeight*7);
    }

    public int getXBegining(int textHeight){
        return xBegText.apply(textHeight);
    }

    public int getYBegining(int textHeight){
        return yBegText.apply(textHeight);
    }

}
