package fr.mattmouss.signs.util;

import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Functions {

    public static BooleanProperty NORTH_WEST,NORTH_EAST,SOUTH_WEST,SOUTH_EAST;

    static {
        NORTH_WEST = BooleanProperty.create("north_west");
        NORTH_EAST = BooleanProperty.create("north_east");
        SOUTH_EAST = BooleanProperty.create("south_east");
        SOUTH_WEST = BooleanProperty.create("south_west");
    }
    public static Direction getDirectionFromEntity(LivingEntity placer, BlockPos pos) {
        Vec3d vec = placer.getPositionVec();
        Direction d = Direction.getFacingFromVector(vec.x-pos.getX(),vec.y-pos.getY(),vec.z-pos.getZ());
        if (d== Direction.DOWN || d== Direction.UP){
            return Direction.NORTH;
        }
        return d;
    }


    public static boolean isSupportOrGrid(Block block){
        return (block instanceof SignSupport || block instanceof GridSupport);
    }

    public static boolean[] getFlagsFromState(BlockState state) {
        boolean[] flags= new boolean[8];
        flags[0] = state.get(BlockStateProperties.SOUTH);
        flags[1] = state.get(BlockStateProperties.WEST);
        flags[2] = state.get(BlockStateProperties.NORTH);
        flags[3] = state.get(BlockStateProperties.EAST);
        flags[4] = state.get(SOUTH_WEST);
        flags[5] = state.get(NORTH_WEST);
        flags[6] = state.get(NORTH_EAST);
        flags[7] = state.get(SOUTH_EAST);
        return flags;
    }

    public static void setBlockState(World world,BlockPos pos, BlockState state, boolean[] flags) {
        world.setBlockState(pos,state
                .with(BlockStateProperties.SOUTH,flags[0])
                .with(BlockStateProperties.WEST ,flags[1])
                .with(BlockStateProperties.NORTH,flags[2])
                .with(BlockStateProperties.EAST ,flags[3])
                .with(SOUTH_WEST,flags[4])
                .with(NORTH_WEST,flags[5])
                .with(NORTH_EAST,flags[6])
                .with(SOUTH_EAST,flags[7])
        );
    }

    public static float distance2DFromTo(Vec3d vec1,Vec3d vec2) {
        double d0 = vec1.x - vec2.x;
        double d1 = vec1.z - vec2.z;
        return MathHelper.sqrt(d0 * d0 + d1 * d1);
    }

    public static Vec3d getVecFromBlockPos (BlockPos pos,float horOffset){
        return new Vec3d(pos.getX()+horOffset,pos.getY(),pos.getZ()+horOffset);
    }

    public static double toDegree(double radianAngle){
        return (180.0/Math.PI)*radianAngle;
    }

    public static boolean isValidCoordinate(int x, int y) {
        return (x>-1 && y>-1 && x<17 && y<17);
    }
}
