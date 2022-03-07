package fr.matt1999rd.signs.enums;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import fr.matt1999rd.signs.tileentity.EditingSignTileEntity;
import fr.matt1999rd.signs.tileentity.PanelTileEntity;
import fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity;
import fr.matt1999rd.signs.util.Functions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;

import java.util.function.IntFunction;
import java.util.function.Predicate;


public enum Form {
    // Delimiter function for form
    // UPSIDE TRIANGLE :
    // it must be in the lower part of the rhombus centered on the point (1/2,0) and of x diagonal length 1 and y diagonal length 2



    UPSIDE_TRIANGLE(0, EditingSignTileEntity.class,
            vec2f -> Functions.isInRhombus(vec2f,new Vector2f(0.5F,0),0.5F,1),
            h->h/2+2,
            h-> 10
    ),

    // TRIANGLE :
    // it must be in the upper part of the rhombus centered on the point (1/2,1) and of x diagonal length 1 and y diagonal length 2

    TRIANGLE(1, DrawingSignTileEntity.class,
            vec2f -> Functions.isInRhombus(vec2f,new Vector2f(0.5F,1),0.5F,1),
            h-> h/2+2,
            h-> 127-h
    ),

    // OCTAGON :
    //               ◢■◣
    //               ■■■
    //               ◥■◤
    // horizontal/vertical line : y=0 and y=1 and x=0 and x=1
    // it must be in the rhombus centered on (1/2,1/2) but with a 1/3 more distance (in total) from the origin and with equal diagonal

    OCTAGON(2, EditingSignTileEntity.class,
            vec2f -> Functions.isInRhombus(vec2f,new Vector2f(0.5F,0.5F),2/3F,2/3F),
            h-> 0,
            h-> 50
    ),

    // CIRCLE : ●
    // it must be within 0.5 (radius) distance from the point C=(x0,y0)=(0.5,0.5) (center)

    CIRCLE(3, DrawingSignTileEntity.class,
            (vec2f -> Functions.distance(vec2f.x-0.5F,vec2f.y-0.5F) <= 0.5F),
            h-> MathHelper.ceil(64+MathHelper.sqrt(64.0F-h*h/2.0F)),
            h-> 64-h/2
    ),

    // SQUARE and OTHER FOUR FORMS (RECTANGLE,ARROW,PLAIN_SQUARE) : same limit
    // horizontal/vertical line : y=0 and y=1 and x=0 and x=1

    SQUARE(4, DrawingSignTileEntity.class,
            vec2f -> true,
            h-> 0,
            h-> 0
    ),

    RECTANGLE(5, DirectionSignTileEntity.class,
            vec2f -> true,
            h-> 0,
            h-> 0
    ),
    ARROW(6, DirectionSignTileEntity.class,
            vec2f -> true,
            h-> 0,
            h-> 0),
    PLAIN_SQUARE(7, PlainSquareSignTileEntity.class,
            vec2f -> true,
            h-> 0,
            h-> 0
    ),

    // DIAMOND   ◢◣
    //           ◥◤
    // it must be in the rhombus centered on (1/2,1/2) but with a distance (in total) of 1/2 from the origin and with equal diagonal
    DIAMOND(8, DrawingSignTileEntity.class,
            vec2f -> Functions.isInRhombus(vec2f,new Vector2f(0.5F,0.5F),0.5F,0.5F),
            h-> h/2+2,
            h-> 64-h/2-2
    );

    private static final Predicate<Vector2f> isInSquare = vector2f ->
            MathHelper.abs(vector2f.x - 0.5F)<=0.5F &&
                    MathHelper.abs(vector2f.y - 0.5F) <= 0.5F;

    public static final int offsetLetWay = 10;

    private final int meta;
    private final Predicate<Vector2f> isIn;
    private final IntFunction<Integer> xBegText;  //start text when you can fit a rectangle of height h,
    private final IntFunction<Integer> yBegText;  // the y needed to get max length
    private final Class<? extends PanelTileEntity> tileEntityClass;
    private static final ResourceLocation STOP_BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/stop.png");
    private static final ResourceLocation LW_BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/let_way.png");

    Form(int meta, Class<? extends PanelTileEntity> teClass, Predicate<Vector2f> isIn, IntFunction<Integer> xBeg, IntFunction<Integer> yBegText){
        this.meta = meta;
        this.isIn = isIn;
        this.xBegText = xBeg;
        this.yBegText = yBegText;
        this.tileEntityClass = teClass;
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
        Vector2f vec2f = new Vector2f(x,y);
        return isIn.test(vec2f) && isInSquare.test(vec2f);
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

    public boolean isForDrawing() {return (this.tileEntityClass == DrawingSignTileEntity.class);}

    public int getXBeginning(int textHeight){
        return xBegText.apply(textHeight);
    }

    public int getYBeginning(int textHeight){
        return yBegText.apply(textHeight);
    }

    public boolean isNotForEditing() {return (this.tileEntityClass != EditingSignTileEntity.class);}

    public boolean needNotUpdateTextPosition() { return isNotForEditing() || this != PLAIN_SQUARE; }

    public boolean isForDirection() {
        return (this.tileEntityClass == DirectionSignTileEntity.class);
    }

    public boolean hasLengthPredefinedLimit(){
        return isForDirection() || this == PLAIN_SQUARE;
    }

    public ResourceLocation getTexture(){
        if (this == UPSIDE_TRIANGLE)return LW_BACKGROUND;
        if (this == OCTAGON)return STOP_BACKGROUND;
        return Functions.SIGN_BACKGROUND;
    }
}
