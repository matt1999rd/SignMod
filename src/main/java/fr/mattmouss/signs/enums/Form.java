package fr.mattmouss.signs.enums;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import sun.nio.cs.ext.MacHebrew;

import java.util.function.Predicate;

public enum Form {
    UPSIDE_TRIANGLE(0,"let_way_panel",vec2f -> {
        return (vec2f.y>0 && vec2f.x>vec2f.y/2 && vec2f.x<1-vec2f.y/2);
    }),
    TRIANGLE(1,"triangle_panel",vec2f -> {
        return (vec2f.y<1 && vec2f.y>MathHelper.abs(2*vec2f.x-1));
    }),
    OCTOGONE(2,"stop_panel",vec2f -> {
        return (1-3*vec2f.y<3*vec2f.x && 3*vec2f.x<5-3*vec2f.y) &&
                (0<vec2f.x && vec2f.x<1) &&
                (0<vec2f.y && vec2f.y<1) &&
                (3*vec2f.y-2<3*vec2f.x && 3*vec2f.x<3*vec2f.y+2);
    }),
    CIRCLE(3,"circle_panel",(vec2f -> {
        return (vec2f.x-0.5)*(vec2f.x-0.5)+(vec2f.y-0.5)*(vec2f.y-0.5)<0.5*0.5;
    })),
    SQUARE(4,"square_panel",(vec2f -> {
        return (vec2f.x<=1 && vec2f.x>=0 && vec2f.y<=1 && vec2f.y>=0);
    })),
    RECTANGLE(5,"rectangle_panel",vec2f -> {
        return (vec2f.y>0.4 && vec2f.y<0.6 && vec2f.x>0 && vec2f.x<1);
    }),
    ARROW(6,"direction_panel",vec2f -> {
        return (vec2f.y > 0.4 && vec2f.y < 0.6 && vec2f.x > 0 && vec2f.x < 1);
    }),
    PLAIN_SQUARE(7,"huge_direction_panel",vec2f -> {
        return (vec2f.x<1 && vec2f.x>0 && vec2f.y<1 && vec2f.y>0);
    }),
    DIAMOND(8,"diamond_panel",vec2f -> {
        return 2*vec2f.x> MathHelper.abs(2*vec2f.y-1) && 2*vec2f.x<2-MathHelper.abs(1-2*vec2f.y);
    });

    private final String block_name;
    private final int meta;
    private final Predicate<Vec2f> isIn;

    Form(int meta, String block_name, Predicate<Vec2f> isIn){
        this.meta = meta;
        this.block_name = block_name;
        this.isIn = isIn;
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
        float x = i/completeLength;
        float y = j/completeLength;
        Vec2f vec2f = new Vec2f(x,y);
        return isIn.test(vec2f);
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
}
