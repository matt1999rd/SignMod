package fr.matt1999rd.signs.enums;

import com.google.common.collect.Lists;
import fr.matt1999rd.signs.util.QuadPSPositions;
import fr.matt1999rd.signs.util.Vector2i;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Vector2f;

import java.util.HashMap;
import java.util.List;

public enum PSDisplayMode implements IStringSerializable {
    EXIT(0,"exit",
            new Vector2f(25.0F,1.0F), new Vector2f(6.0F,6.0F),
            new Vector2f(0.0F,14.0F),new Vector2f(12.0F,12.0F),
            new QuadPSPositions(
                    new QuadPSPositions.QuadPSPosition(3, 6,45,2),
                    new QuadPSPositions.QuadPSPosition(3,24,58,4))),
    DIRECTION(1,"direction",
            new Vector2f(2.0F,24.0F), new Vector2f(10.0F,7.0F),
            new Vector2f(20.0F,0.0F), new Vector2f(20.0F,14.0F),
            new QuadPSPositions(
                    new QuadPSPositions.QuadPSPosition(4,3,88,5)
            )),
    SCH_EXIT(2,"scheme_exit",
            new Vector2f(12.0F,12.5F), new Vector2f(10.0F,19.0F),
            new Vector2f(12.0F,14.0F), new Vector2f(20.0F,38.0F),
            new QuadPSPositions(
                    new QuadPSPositions.QuadPSPosition(7,  5,39,2),
                    new QuadPSPositions.QuadPSPosition(51,22,39,4)
            )),
    SCH_MUL_EXIT(3,"scheme_round_about",
            new Vector2f(11.0F,8.0F),  new Vector2f(26.0F,23.5F),
            new Vector2f(32.0F,14.0F), new Vector2f(52.0F,47.0F),
            new QuadPSPositions(
                    new QuadPSPositions.QuadPSPosition(28, 2,39,1),
                    new QuadPSPositions.QuadPSPosition(3, 50,39,1),
                    new QuadPSPositions.QuadPSPosition(54,50,39,1)
            ));


    private final int meta;
    private final String name;
    private final Vector2f texOrigin; //texture origin for the first texture to display (arrow in the left for direction)
    private final Vector2f texDimension; //dimension of the texture displayed
    private final Vector2f uvOrigin; //uv mapping value (u1,v1)
    private final Vector2f uvDimension; // uv mapping value (u2,v2)
    private final QuadPSPositions textPositions; //text position in the GUI

    //texture origin is taken with an origin in up left and with x-axis horizontally and y-axis vertically
    PSDisplayMode(int mode,String name,Vector2f texOrigin, Vector2f texDimension, Vector2f uvOrigin, Vector2f uvDimension,QuadPSPositions positions){
        this.meta = mode;
        this.name = name;
        this.texOrigin = texOrigin;
        this.texDimension = texDimension;
        this.uvOrigin = uvOrigin;
        this.uvDimension = uvDimension;
        this.textPositions = positions;
    }

    public static PSDisplayMode byIndex(byte meta){
        PSDisplayMode[] modes = PSDisplayMode.values();
        if (meta>3 || meta<0){
            return null;
        }
        return modes[meta];
    }

    public byte getMeta(){
        return (byte) meta;
    }

    public boolean is2by2(){
        return this == EXIT;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public int getTotalText(){
        switch (this){
            case EXIT:
            case SCH_EXIT:
                return 6;
            case DIRECTION:
                return 5;
            case SCH_MUL_EXIT:
                return 3;
        }
        return 0;
    }

    public Vector2i getTextBegPosition(int ind){
        if (ind < this.getTotalText()){
            return this.textPositions.getPosition(ind);
        }else {
            throw new IllegalStateException("Try to get text with indices bigger than the max number of test");
        }
    }

    public int getMaxLength(int ind) {
        if (ind < this.getTotalText()){
            return this.textPositions.getQuadPSPosition(ind).getLengthMax();
        }else {
            throw new IllegalStateException("Try to get text with indices bigger than the max number of test");
        }
    }

    public float getTextureXOrigin(){
        return this.texOrigin.x;
    }

    public float getTextureYOrigin(){
        return this.texOrigin.y;
    }

    public float getTexLength(){
        return this.texDimension.x;
    }

    public float getTexHeight(){
        return this.texDimension.y;
    }

    public Vector2f getUVOrigin(int arrowId){
        //arrowId = 0 -> left arrow / 1 -> center arrow / 2 -> right arrow (not used if it is not the direction mode)
        if (this == DIRECTION){
            return new Vector2f(uvOrigin.x*arrowId,uvOrigin.y);
        }
        return uvOrigin;
    }

    //the text written in here must be centered
    public boolean hasCenteredText(){
        return meta<2;
    }

    public Vector2f getUVDimension(){
        return uvDimension;
    }


}