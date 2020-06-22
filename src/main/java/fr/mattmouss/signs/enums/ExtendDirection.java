package fr.mattmouss.signs.enums;

import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupport;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public enum ExtendDirection {
    SOUTH(0,Direction.SOUTH,false, BlockStateProperties.SOUTH),
    WEST(1,Direction.WEST,false,BlockStateProperties.WEST),
    NORTH(2,Direction.NORTH,false,BlockStateProperties.NORTH),
    EAST(3,Direction.EAST,false,BlockStateProperties.EAST),
    SOUTH_WEST(4,Direction.WEST,true, Functions.SOUTH_WEST),
    NORTH_WEST(5,Direction.NORTH,true,Functions.NORTH_WEST),
    NORTH_EAST(6,Direction.EAST,true,Functions.NORTH_EAST),
    SOUTH_EAST(7,Direction.SOUTH,true,Functions.SOUTH_EAST);

    private final int meta;
    private final Direction direction;
    private final boolean rotated;
    private final BooleanProperty property;

    ExtendDirection(int meta, Direction direction,boolean rotated,BooleanProperty property){
        this.meta = meta;
        this.direction = direction;
        this.rotated = rotated;
        this.property = property;
    }

    public static ExtendDirection getExtendedDirection(Direction dir,boolean isRotated){
        switch (dir){
            case DOWN:
            case UP:
            default:
                return null;
            case NORTH:
                return (isRotated)?ExtendDirection.NORTH_WEST:ExtendDirection.NORTH;
            case SOUTH:
                return (isRotated)?ExtendDirection.SOUTH_EAST:ExtendDirection.SOUTH;
            case WEST:
                return (isRotated)?ExtendDirection.SOUTH_WEST:ExtendDirection.WEST;
            case EAST:
                return (isRotated)?ExtendDirection.NORTH_EAST:ExtendDirection.EAST;
        }
    }

    public ExtendDirection rotateYCCW(){
        if (isRotated()){
            return ExtendDirection.getExtendedDirection(direction.rotateYCCW(),false);
        }else {
            return ExtendDirection.getExtendedDirection(direction,true);
        }
    }

    public ExtendDirection rotateY(){
        if (isRotated()){
            return ExtendDirection.getExtendedDirection(direction,false);
        }else {
            return ExtendDirection.getExtendedDirection(direction.rotateY(),true);
        }
    }


    public BooleanProperty getSupportProperty() {
        return property;
    }

    public int getMeta() {
        return meta;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isRotated() {
        return rotated;
    }
}
