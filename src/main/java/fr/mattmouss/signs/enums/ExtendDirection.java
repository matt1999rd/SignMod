package fr.mattmouss.signs.enums;

import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupport;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

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

    public ExtendDirection getOpposite(){
        return getExtendedDirection(this.direction.getOpposite(),this.rotated);
    }

    public static ExtendDirection getExtendedDirection(Direction dir1,Direction dir2){
        switch (dir1){
            case DOWN:
            case UP:
            default:
                return null;
            case NORTH:
                if (dir2 == Direction.EAST)return NORTH_EAST;
                if (dir2 == Direction.WEST)return NORTH_WEST;
                return null;
            case SOUTH:
                if (dir2 == Direction.EAST)return SOUTH_EAST;
                if (dir2 == Direction.WEST)return SOUTH_WEST;
                return null;
            case WEST:
                if (dir2 == Direction.NORTH)return NORTH_WEST;
                if (dir2 == Direction.SOUTH)return SOUTH_WEST;
                return null;
            case EAST:
                if (dir2 == Direction.NORTH)return NORTH_EAST;
                if (dir2 == Direction.SOUTH)return SOUTH_EAST;
                return null;
        }
    }

    public BlockPos offset(BlockPos pos){
        if (!this.isRotated()){
            return pos.offset(this.direction);
        }else {
            return pos.offset(this.direction).offset(this.direction.rotateYCCW());
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
