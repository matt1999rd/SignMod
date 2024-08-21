package fr.matt1999rd.signs.enums;

import fr.matt1999rd.signs.util.QuadPSPositions;
import fr.matt1999rd.signs.util.Vector2i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec2;


public enum PSDisplayMode implements StringRepresentable {
    EXIT(0,"exit",
            new Vec2(25.0F,1.0F), new Vec2(6.0F,6.0F),
            new Vec2(0.0F,14.0F),new Vec2(12.0F,12.0F),
            new QuadPSPositions(
                    new QuadPSPositions.QuadPSPosition(7, 8,47,1),
                    new QuadPSPositions.QuadPSPosition(7,19,60,4))),
    DIRECTION(1,"direction",
            new Vec2(2.0F,24.0F), new Vec2(10.0F,7.0F),
            new Vec2(20.0F,0.0F), new Vec2(20.0F,14.0F),
            new QuadPSPositions(
                    new QuadPSPositions.QuadPSPosition(3,4,88,4)
            )),
    SCH_EXIT(2,"scheme_exit",
            new Vec2(12.0F,12.5F), new Vec2(10.0F,19.0F),
            new Vec2(12.0F,14.0F), new Vec2(20.0F,38.0F),
            new QuadPSPositions(
                    new QuadPSPositions.QuadPSPosition(7,  4,39,2),
                    new QuadPSPositions.QuadPSPosition(51,19,39,4)
            )),
    SCH_MUL_EXIT(3,"scheme_round_about",
            new Vec2(11.0F,8.0F),  new Vec2(26.0F,23.5F),
            new Vec2(32.0F,14.0F), new Vec2(52.0F,47.0F),
            new QuadPSPositions(
                    new QuadPSPositions.QuadPSPosition(28, 4,39,1),
                    new QuadPSPositions.QuadPSPosition(3, 51,39,1),
                    new QuadPSPositions.QuadPSPosition(54,51,39,1)
            ));


    private final int meta;
    private final String name;
    private final Vec2 texOrigin; //texture origin for the first texture to display (arrow in the left for direction)
    private final Vec2 texDimension; //dimension of the texture displayed
    private final Vec2 uvOrigin; //uv mapping value (u1,v1)
    private final Vec2 uvDimension; // uv mapping value (u2,v2)
    private final QuadPSPositions textPositions; //text position in the GUI

    //texture origin is taken with an origin in up left and with x-axis horizontally and y-axis vertically
    PSDisplayMode(int mode,String name,Vec2 texOrigin, Vec2 texDimension, Vec2 uvOrigin, Vec2 uvDimension,QuadPSPositions positions){
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
        return this.textPositions.getTotalText();
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

    public Vec2 getUVOrigin(int arrowId){
        //arrowId = 0 -> left arrow / 1 -> center arrow / 2 -> right arrow (not used if it is not the direction mode)
        if (this == DIRECTION){
            return new Vec2(uvOrigin.x*arrowId,uvOrigin.y);
        }
        return uvOrigin;
    }

    public Vec2 getUVDimension(){
        return uvDimension;
    }


}
