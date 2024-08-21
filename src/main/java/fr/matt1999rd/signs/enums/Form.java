package fr.matt1999rd.signs.enums;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import fr.matt1999rd.signs.tileentity.EditingSignTileEntity;
import fr.matt1999rd.signs.tileentity.PanelTileEntity;
import fr.matt1999rd.signs.tileentity.primary.*;
import fr.matt1999rd.signs.util.Functions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

import java.util.function.IntFunction;
import java.util.function.Predicate;


public enum Form {
    // Delimiter function for form
    // UPSIDE TRIANGLE :
    // it must be in the lower part of the rhombus centered on the point (1/2,0) and of x diagonal length 1 and y diagonal length 2



    UPSIDE_TRIANGLE(0,"let_way", EditingSignTileEntity.class,
            vec2f -> Functions.isInRhombus(vec2f,new Vec2(0.5F,0),0.5F,1),
            h->h/2+2,
            h-> 10
    ),

    // TRIANGLE :
    // it must be in the upper part of the rhombus centered on the point (1/2,1) and of x diagonal length 1 and y diagonal length 2

    TRIANGLE(1,"triangle", DrawingSignTileEntity.class,
            vec2f -> Functions.isInRhombus(vec2f,new Vec2(0.5F,1),0.5F,1),
            h-> h/2+2,
            h-> 127-h
    ),

    // OCTAGON :
    //               ◢■◣
    //               ■■■
    //               ◥■◤
    // horizontal/vertical line : y=0 and y=1 and x=0 and x=1
    // it must be in the rhombus centered on (1/2,1/2) but with a 1/3 more distance (in total) from the origin and with equal diagonal

    OCTAGON(2,"stop", EditingSignTileEntity.class,
            vec2f -> Functions.isInRhombus(vec2f,new Vec2(0.5F,0.5F),2/3F,2/3F),
            h-> 0,
            h-> 50
    ),

    // CIRCLE : ●
    // it must be within 0.5 (radius) distance from the point C=(x0,y0)=(0.5,0.5) (center)

    CIRCLE(3,"circle", DrawingSignTileEntity.class,
            (vec2f -> Functions.distance(vec2f.x-0.5F,vec2f.y-0.5F) <= 0.5F),
            h-> Mth.ceil(64+Mth.sqrt(64.0F-h*h/2.0F)),
            h-> 64-h/2
    ),

    // SQUARE and OTHER FOUR FORMS (RECTANGLE,ARROW,PLAIN_SQUARE) : same limit
    // horizontal/vertical line : y=0 and y=1 and x=0 and x=1

    SQUARE(4,"square",DrawingSignTileEntity.class,
            vec2f -> true,
            h-> 0,
            h-> 0
    ),

    RECTANGLE(5,"rectangle", DirectionSignTileEntity.class,
            vec2f -> true,
            h-> 0,
            h-> 0
    ),
    ARROW(6,"direction", DirectionSignTileEntity.class,
            vec2f -> true,
            h-> 0,
            h-> 0),
    PLAIN_SQUARE(7,"huge_direction", PlainSquareSignTileEntity.class,
            vec2f -> true,
            h-> 0,
            h-> 0
    ),

    // DIAMOND   ◢◣
    //           ◥◤
    // it must be in the rhombus centered on (1/2,1/2) but with a distance (in total) of 1/2 from the origin and with equal diagonal
    DIAMOND(8,"diamond", DrawingSignTileEntity.class,
            vec2f -> Functions.isInRhombus(vec2f,new Vec2(0.5F,0.5F),0.5F,0.5F),
            h-> h/2+2,
            h-> 64-h/2-2
    );

    private static final Predicate<Vec2> isInSquare = vector2f ->
            Mth.abs(vector2f.x - 0.5F)<=0.5F &&
                    Mth.abs(vector2f.y - 0.5F) <= 0.5F;

    public static final int offsetLetWay = 10;

    private final int meta;
    private final String objName;
    private final Predicate<Vec2> isIn;
    private final IntFunction<Integer> xBegText;  //start text when you can fit a rectangle of height h,
    private final IntFunction<Integer> yBegText;  // the y needed to get max length
    private final Class<? extends PanelTileEntity> tileEntityClass;
    private static final ResourceLocation STOP_BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/stop.png");
    private static final ResourceLocation LW_BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/let_way.png");

    Form(int meta,String objectName, Class<? extends PanelTileEntity> teClass, Predicate<Vec2> isIn, IntFunction<Integer> xBeg, IntFunction<Integer> yBegText){
        this.meta = meta;
        objName = objectName;
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

    public String getObjName() {
        return objName;
    }

    public ScreenType getScreenType(){
        if (isForDrawing()){
            return ScreenType.DRAWING_SCREEN;
        }else if (isForDirection()){
            return ScreenType.DIRECTION_SCREEN;
        }else if (isNotForEditing()){
            return ScreenType.PLAIN_SQUARE_SCREEN;
        }else {
            return ScreenType.EDITING_SCREEN;
        }
    }

    public int getMeta(){
        return meta;
    }

    //return true if i and j coordinates that are between 0 and 128 are in the form figure
    public boolean isIn(int i,int j){
        float completeLength = 128;
        float x = (i+0.5F)/completeLength;
        float y = (j+0.5F)/completeLength;
        Vec2 vec2f = new Vec2(x,y);
        return isIn.test(vec2f) && isInSquare.test(vec2f);
    }

    public boolean rectangleIsIn(int i_min,int i_max,int j_min,int j_max){
        //we can ensure that it is going to check all place because form are all convex -> check only the limit
        for (int i=i_min;i<i_max+1;i++){
            if (!isIn(i,j_min) || !isIn(i,j_max)){
                return false;
            }
        }
        for (int j=j_min;j<j_max+1;j++){
            if (!isIn(i_min,j) || !isIn(i_max,j)){
                return false;
            }
        }
        return true;
    }

    public boolean rectangleIsIn(float iMin,float iMax,float jMin,float jMax){
        return rectangleIsIn(
                Mth.ceil(iMin),Mth.floor(iMax),
                Mth.ceil(jMin),Mth.floor(jMax)
                );
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

    public BlockEntity createTileEntity(BlockPos pos, BlockState state){
        return switch (this) {
            case ARROW -> new ArrowSignTileEntity(pos, state);
            case CIRCLE -> new CircleSignTileEntity(pos, state);
            case SQUARE -> new SquareSignTileEntity(pos, state);
            case DIAMOND -> new DiamondSignTileEntity(pos, state);
            case OCTAGON -> new OctagonSignTileEntity(pos, state);
            case TRIANGLE -> new TriangleSignTileEntity(pos, state);
            case RECTANGLE -> new RectangleSignTileEntity(pos, state);
            case PLAIN_SQUARE -> new PlainSquareSignTileEntity(pos, state);
            case UPSIDE_TRIANGLE -> new UpsideTriangleSignTileEntity(pos, state);
        };
    }
}
