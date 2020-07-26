package fr.mattmouss.signs.enums;

import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupport;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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

    public float getAngleFrom(Direction direction){
        float dir_angle = direction.getHorizontalAngle();
        float base_angle = this.rotated ? this.direction.getHorizontalAngle()-45 : this.direction.getHorizontalAngle();
        float angleDiff = dir_angle - base_angle;
        return Functions.toRadian(angleDiff);
    }

    public Direction.Axis getAxis(){
        return direction.getAxis();
    }

    public static ExtendDirection getFacingFromPlayer(PlayerEntity player,BlockPos pos){
        //for the purpose of getting the state of the panel we divide space around the center of the support in 8 part
        //division are for angle 22.5/67.5/112.5/157.5/202.5/247.5/292.5/337.5 (22.5+45*i for 0<=i<=7)
        Vec3d support_center = Functions.getVecFromBlockPos(pos,0.5F);
        Vec3d offsetPlayerPos = player.getPositionVec().subtract(support_center);
        //we compare our player position to the position of the support's center
        //we get angle using arctan function
        double angle = MathHelper.atan2(offsetPlayerPos.x,offsetPlayerPos.z);
        //we convert to degree and make it positive
        double degreeAngle =Functions.toDegree(angle);
        //then to index from 2 to 9 corresponding to the part of stage where the player is
        int index = MathHelper.ceil((degreeAngle-22.5D)/45.0D);
        //we consider the part split by angle origin which correpond to 0
        // that we move of a complete circle for math simplification
        if (index == 0){
            index =8;
        }else if (index == 1){
            index =9;
        }
        //we get facing using a special index that is for i : 0->7 --> 0 0 3 3 2 2 1 1
        //we have translated 0 to 8 and 1 to 9 to get a decreasing linear function -->
        // 2->3 3->3 4->2 5->2 6->1 7->1 8->0 9->0
        Direction facing = Direction.byHorizontalIndex((9-index)/2);
        boolean isRotated = (index%2 == 1);
        ExtendDirection direction = ExtendDirection.getExtendedDirection(facing,isRotated);
        return direction;
    }



}
