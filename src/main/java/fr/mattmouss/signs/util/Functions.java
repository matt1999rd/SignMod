package fr.mattmouss.signs.util;

import fr.mattmouss.signs.enums.ExtendDirection;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Functions {

    public static BooleanProperty NORTH_WEST,NORTH_EAST,SOUTH_WEST,SOUTH_EAST;

    static {
        NORTH_WEST = BooleanProperty.create("north_west");
        NORTH_EAST = BooleanProperty.create("north_east");
        SOUTH_EAST = BooleanProperty.create("south_east");
        SOUTH_WEST = BooleanProperty.create("south_west");
    }

    //give the direction after placement for updating blockstate

    public static Direction getDirectionFromEntity(LivingEntity placer, BlockPos pos) {
        Vec3d vec = placer.getPositionVec();
        Direction d = Direction.getFacingFromVector(vec.x-pos.getX(),vec.y-pos.getY(),vec.z-pos.getZ());
        if (d== Direction.DOWN || d== Direction.UP){
            return Direction.NORTH;
        }
        return d;
    }

    //notify if the block is a support or a grid for checking before placement of grid or support in world

    public static boolean isSupportOrGrid(Block block){
        return (block instanceof SignSupport || block instanceof GridSupport || block instanceof AbstractPanelBlock);
    }

    //give a boolean table corresponding to the position of grid in blockstate state of a support-like block

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

    //usefull in support-like block to update the blockstate given in the position of grids into the flags table

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

    //convert a blockpos into vec3d and adding an offset on x and z coordinate
    // (use in the convertion to vec3d of support and grid center blockpos)

    public static Vec3d getVecFromBlockPos (BlockPos pos,float horOffset){
        return new Vec3d(pos.getX()+horOffset,pos.getY(),pos.getZ()+horOffset);
    }

    //usefull if we need to convert to degree (use for openGL rotation)

    public static double toDegree(double radianAngle){
        double degreeAngle = (180.0/Math.PI)*radianAngle;
        if (degreeAngle < 0){
            degreeAngle +=360.0D;
        }
        return degreeAngle;
    }

    //usefull if we need to convert to radian

    public static double toRadian(double degreeAngle){
        double radianAngle = (Math.PI/180.0)*degreeAngle;
        if (radianAngle < 0){
            radianAngle += 2*Math.PI;
        }
        return radianAngle;
    }

    //usefull for text coordinate will be changed in the future

    public static boolean isValidCoordinate(int x, int y) {
        return (x>-1 && y>-1 && x<17 && y<17);
    }

    //notify if the block is a grid support or a panel with grid support background

    public static boolean isGridSupport(BlockState state){
        if (state.getBlock() instanceof GridSupport){
            return true;
        }
        if (state.getBlock() instanceof AbstractPanelBlock){
            return state.get(AbstractPanelBlock.GRID);
        }
        return false;
    }

    //notify if the block is a sign support or a panel with sign support background

    public static boolean isSignSupport(BlockState state){
        if (state.getBlock() instanceof SignSupport){
            return true;
        }
        if (state.getBlock() instanceof AbstractPanelBlock){
            return (!state.get(AbstractPanelBlock.GRID));
        }
        return false;
    }

    //delete Grid Row following direction dir from the grid at basePos in worldIn by player

    public static void deleteGridRow(ExtendDirection dir, BlockPos basePos, World worldIn, PlayerEntity player){
        BlockPos offset_pos = dir.offset(basePos);
        //check all the row of grid in the direction dir1 and stop when it is not a grid.
        while (isGridSupport(worldIn.getBlockState(offset_pos))){
            offset_pos = dir.offset(offset_pos);
        }
        if (isSignSupport(worldIn.getBlockState(offset_pos))) {
            //if it is stable do not bother with this
            return;
        }
        ExtendDirection oppositeDir = dir.getOpposite();
        //else we need to get back to the initial block and delete block in-between
        offset_pos = oppositeDir.offset(offset_pos);
        while (!offset_pos.equals(basePos)) {
            BlockState state = worldIn.getBlockState(offset_pos);
            if (isGridSupport(state)){
                deleteBlock(offset_pos,worldIn,player);
            }
            offset_pos = oppositeDir.offset(offset_pos);
        }
    }

    //delete connecting grid of a sign support (with or without panel) at position pos in worldIn by player

    public static void deleteConnectingGrid(BlockPos pos, World worldIn, PlayerEntity player,BlockState state) {
        boolean[] flags = getFlagsFromState(state);
        for (int i=0;i<8;i++){
            if (flags[i]){
                BlockPos gridPos;
                if (i<4){
                    Direction dir1 = Direction.byHorizontalIndex(i);
                    gridPos = pos.offset(dir1);
                }else {
                    Direction dir1 = Direction.byHorizontalIndex(i-4);
                    Direction dir2 = Direction.byHorizontalIndex((i-3)%4); //todo : change this part to match with ExtendDirection
                    gridPos = pos.offset(dir1).offset(dir2);
                }
                BlockState gridState = worldIn.getBlockState(gridPos);
                Functions.deleteOtherGrid(gridPos,worldIn,player,gridState);
                Functions.deleteBlock(gridPos,worldIn,player);
            }
        }
    }

    //delete block at position pos by playerEntity in world

    public static void deleteBlock(BlockPos pos, World world, PlayerEntity playerEntity){
        ItemStack stack = playerEntity.getHeldItemMainhand();
        BlockState state1 = world.getBlockState(pos);
        world.playEvent(playerEntity,2001,pos,Block.getStateId(state1));
        if (!world.isRemote && !playerEntity.isCreative()) {
            Block.spawnDrops(state1, world, pos, null, playerEntity, stack);
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(),35);
    }

    //usefull to delete the other grid of a grid or a

    public static void deleteOtherGrid(BlockPos pos, World worldIn, PlayerEntity player,BlockState state) {
        boolean isRotated = state.get(GridSupport.ROTATED);
        Direction.Axis axis;
        if (state.has(BlockStateProperties.HORIZONTAL_AXIS)){
            axis = state.get(BlockStateProperties.HORIZONTAL_AXIS);
        }else {
            axis = state.get(BlockStateProperties.HORIZONTAL_FACING).rotateY().getAxis();
        }

        if (!isRotated) {
            ExtendDirection posDir = ExtendDirection.getExtendedDirection(
                    Direction.getFacingFromAxisDirection(axis, Direction.AxisDirection.POSITIVE),
                    false);
            ExtendDirection negDir = ExtendDirection.getExtendedDirection(
                    Direction.getFacingFromAxisDirection(axis, Direction.AxisDirection.NEGATIVE),
                    false);
            Functions.deleteGridRow(posDir,pos,worldIn,player);
            Functions.deleteGridRow(negDir,pos,worldIn,player);
        }else {
            Direction posDir1 = Direction.getFacingFromAxisDirection(axis, Direction.AxisDirection.POSITIVE);
            Direction posDir2 = posDir1.rotateYCCW();
            ExtendDirection posExtDir = ExtendDirection.getExtendedDirection(posDir1,posDir2);
            Direction negDir1 = Direction.getFacingFromAxisDirection(axis, Direction.AxisDirection.NEGATIVE);
            Direction negDir2 = negDir1.rotateYCCW();
            ExtendDirection negExtDir = ExtendDirection.getExtendedDirection(negDir1,negDir2);
            Functions.deleteGridRow(posExtDir,pos,worldIn,player);
            Functions.deleteGridRow(negExtDir,pos,worldIn,player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static int getRedValue(int color){
        return (color & 16711680) >> 16;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getGreenValue(int color){
        return (color & '\uff00') >> 8;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getBlueValue(int color){
        return (color & 255);
    }

}
