package fr.matt1999rd.signs.enums;


import fr.matt1999rd.signs.util.Functions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public enum ExtendDirection {
    SOUTH(0,Direction.SOUTH,false,Direction.AxisDirection.POSITIVE, BlockStateProperties.SOUTH),
    WEST(1,Direction.WEST,false, Direction.AxisDirection.NEGATIVE, BlockStateProperties.WEST),
    NORTH(2,Direction.NORTH,false, Direction.AxisDirection.NEGATIVE, BlockStateProperties.NORTH),
    EAST(3,Direction.EAST,false, Direction.AxisDirection.POSITIVE, BlockStateProperties.EAST),
    SOUTH_WEST(4,Direction.WEST,true, Direction.AxisDirection.NEGATIVE, Functions.SOUTH_WEST),
    NORTH_WEST(5,Direction.NORTH,true, Direction.AxisDirection.NEGATIVE, Functions.NORTH_WEST),
    NORTH_EAST(6,Direction.EAST,true, Direction.AxisDirection.POSITIVE, Functions.NORTH_EAST),
    SOUTH_EAST(7,Direction.SOUTH,true, Direction.AxisDirection.POSITIVE, Functions.SOUTH_EAST);

    private final int meta;
    private final Direction direction;
    private final Direction.AxisDirection axisDirection;
    private final boolean rotated;
    private final BooleanProperty property;

    ExtendDirection(int meta, Direction direction, boolean rotated, Direction.AxisDirection axisDirection, BooleanProperty property){
        this.meta = meta;
        this.direction = direction;
        this.axisDirection = axisDirection;
        this.rotated = rotated;
        this.property = property;
    }

    public static ExtendDirection getExtendedDirection(Direction dir,boolean isRotated){
        return switch (dir) {
            default -> null;
            case NORTH -> (isRotated) ? ExtendDirection.NORTH_WEST : ExtendDirection.NORTH;
            case SOUTH -> (isRotated) ? ExtendDirection.SOUTH_EAST : ExtendDirection.SOUTH;
            case WEST -> (isRotated) ? ExtendDirection.SOUTH_WEST : ExtendDirection.WEST;
            case EAST -> (isRotated) ? ExtendDirection.NORTH_EAST : ExtendDirection.EAST;
        };
    }

    //return the direction that leads from the support pos to the pos of the block
    public static ExtendDirection getDirectionFromPos(BlockPos supportPos, BlockPos pos) {
        int x = pos.getX()-supportPos.getX();
        int z = pos.getZ()-supportPos.getZ();
        if (Math.abs(x)>1 || Math.abs(z)>1 || (x==0 && z==0))return null;
        if (x==-1){
            return (z == -1)? NORTH_WEST : (z == 0) ? WEST : SOUTH_WEST;
        }else if (x == 0){
            return (z == -1)? NORTH : SOUTH;
        }else {
            return (z == -1)? NORTH_EAST : (z == 0) ? EAST : SOUTH_EAST;
        }
    }

    public static void addAllBooleanProperty(StateDefinition.Builder<Block, BlockState> builder) {
        for (ExtendDirection direction : ExtendDirection.values()){
            builder.add(direction.property);
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

    public BlockPos relative(BlockPos pos){
        if (!this.isRotated()){
            return pos.relative(this.direction);
        }else {
            return pos.relative(this.direction).relative(this.direction.getCounterClockWise());
        }
    }

    public ExtendDirection getCounterClockWise(){
        if (isRotated()){
            return ExtendDirection.getExtendedDirection(direction.getCounterClockWise(),false);
        }else {
            return ExtendDirection.getExtendedDirection(direction,true);
        }
    }

    public ExtendDirection getClockWise(){
        if (isRotated()){
            return ExtendDirection.getExtendedDirection(direction,false);
        }else {
            return ExtendDirection.getExtendedDirection(direction.getClockWise(),true);
        }
    }

    public static ExtendDirection byIndex(int meta){
        ExtendDirection[] directions = ExtendDirection.values();
        if (meta<0 || meta>7)return null;
        return directions[meta];
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

    public Direction.Axis getAxis(){
        return direction.getAxis();
    }

    public static ExtendDirection getFacingFromPlayer(Player player,BlockPos pos){
        //for the purpose of getting the state of the panel we divide space around the center of the support in 8 part
        //division are for angle 22.5/67.5/112.5/157.5/202.5/247.5/292.5/337.5 (22.5+45*i for 0<=i<=7)
        Vec3 support_center = Functions.getVecFromBlockPos(pos,0.5F);
        Vec3 offsetPlayerPos = player.position().subtract(support_center);
        //we compare our player position to the position of the support's center
        //we get angle using arc tan function
        int index = getIndex(offsetPlayerPos);
        //we get facing using a special index that is for i : 0->7 --> 0 0 3 3 2 2 1 1
        //we have translated 0 to 8 and 1 to 9 to get a decreasing linear function -->
        // 2->3 3->3 4->2 5->2 6->1 7->1 8->0 9->0
        Direction facing = Direction.from2DDataValue((9-index)/2);
        boolean isRotated = (index%2 == 1);
        return ExtendDirection.getExtendedDirection(facing,isRotated);
    }

    private static int getIndex(Vec3 offsetPlayerPos) {
        double angle = Mth.atan2(offsetPlayerPos.x, offsetPlayerPos.z);
        //we convert to degree and make it positive
        double degreeAngle =Functions.toDegree(angle);
        //then to index from 2 to 9 corresponding to the part of stage where the player is
        int index = Mth.ceil((degreeAngle-22.5D)/45.0D);
        //we consider the part split by angle origin which correspond to 0
        // that we move of a complete circle for math simplification
        if (index == 0){
            index =8;
        }else if (index == 1){
            index =9;
        }
        return index;
    }

    //we search first for the horizontal direction 0 : S ; 1 : W ; 2 : N ; 3 : E
    //then we search the diagonal direction 4 : (0 1) = SW ; 5 : (1 2) = NW ; 6 : (2 3) = NE ; 7 : (3 0) = SE
    public static int makeFlagsFromFunction(Predicate<ExtendDirection> predicate){
        int flags = 0;
        for (ExtendDirection extendDirection : ExtendDirection.values()){
            if (predicate.test(extendDirection)){
                flags += 128;
            }
            if (extendDirection != SOUTH_EAST){
                flags = flags >> 1;
            }
        }
        return flags;
    }


    public Direction.AxisDirection getAxisDirection() {
        return axisDirection;
    }
}
