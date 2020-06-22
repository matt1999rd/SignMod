package fr.mattmouss.signs.fixedpanel.support;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static fr.mattmouss.signs.util.Functions.isSupportOrGrid;

public class GridSupport extends Block {

    //this property specified if the grid support is rotated in trigonometric rotation of angle 45°
    public static BooleanProperty ROTATED;
    static {
        ROTATED = BooleanProperty.create("rotated");
    }
    public GridSupport() {
        super(Properties.create(Material.ROCK));
        this.setRegistryName("grid_support");
    }

    //1.14.4 function replaced by notSolid()
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_AXIS,ROTATED);
    }



    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer != null){
            Block eastBlock = worldIn.getBlockState(pos.east()).getBlock();
            Block westBlock = worldIn.getBlockState(pos.west()).getBlock();
            Block northBlock = worldIn.getBlockState(pos.north()).getBlock();
            Block southBlock = worldIn.getBlockState(pos.south()).getBlock();
            Block northEastBlock = worldIn.getBlockState(pos.east().north()).getBlock();
            Block northWestBlock = worldIn.getBlockState(pos.west().north()).getBlock();
            Block southEastBlock = worldIn.getBlockState(pos.south().east()).getBlock();
            Block southWestBlock = worldIn.getBlockState(pos.south().west()).getBlock();
            if (isSupportOrGrid(eastBlock) || isSupportOrGrid(westBlock)){
                worldIn.setBlockState(pos,state
                        .with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X)
                        .with(ROTATED,false)
                );
            }else if (isSupportOrGrid(northBlock) || isSupportOrGrid(southBlock)){
                worldIn.setBlockState(pos,state
                        .with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.Z)
                        .with(ROTATED,false)
                );
            }else if (isSupportOrGrid(southEastBlock) || isSupportOrGrid(northWestBlock)) {
                worldIn.setBlockState(pos,state
                        .with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.Z)
                        .with(ROTATED,true)
                );
            }else if (isSupportOrGrid(northEastBlock) || isSupportOrGrid(southWestBlock)) {
                worldIn.setBlockState(pos,state
                        .with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X)
                        .with(ROTATED,true)
                );
            }else {
                throw new IllegalStateException("grid block cannot be created if there is no support block to support it.");
            }

        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void harvestBlock(World world, PlayerEntity entity, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack) {
        super.harvestBlock(world, entity, pos, Blocks.AIR.getDefaultState(), tileEntity, stack);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        this.deleteOtherGrid(pos,worldIn,player);
        this.deleteBlock(pos,worldIn,player);
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public void deleteOtherGrid(BlockPos pos, World worldIn, PlayerEntity player) {
        BlockState state = worldIn.getBlockState(pos);
        boolean isRotated = state.get(ROTATED);
        Direction.Axis axis = state.get(BlockStateProperties.HORIZONTAL_AXIS);
        if (!isRotated) {
            Direction posDir = Direction.getFacingFromAxisDirection(axis, Direction.AxisDirection.POSITIVE);
            Direction negDir = Direction.getFacingFromAxisDirection(axis, Direction.AxisDirection.NEGATIVE);
            deleteGridRow(posDir,null,pos,worldIn,player);
            deleteGridRow(negDir,null,pos,worldIn,player);
        }else {
            Direction posDir1 = Direction.getFacingFromAxisDirection(axis, Direction.AxisDirection.POSITIVE);
            Direction posDir2 = posDir1.rotateYCCW();
            Direction negDir1 = Direction.getFacingFromAxisDirection(axis, Direction.AxisDirection.NEGATIVE);
            Direction negDir2 = negDir1.rotateYCCW();
            deleteGridRow(posDir1,posDir2,pos,worldIn,player);
            deleteGridRow(negDir1,negDir2,pos,worldIn,player);
        }
    }

    private void deleteGridRow(Direction dir1,@Nullable Direction dir2,BlockPos basePos,World worldIn,PlayerEntity player){
        BlockPos offset_pos = (dir2 == null) ? basePos.offset(dir1) : basePos.offset(dir1).offset(dir2);
        //check all the row of grid in the direction dir1 and stop when it is not a grid.
        while (worldIn.getBlockState(offset_pos).getBlock() instanceof GridSupport){
            offset_pos = (dir2 == null) ? offset_pos.offset(dir1) : offset_pos.offset(dir1).offset(dir2);
        }
        if (worldIn.getBlockState(offset_pos).getBlock() instanceof SignSupport) {
            //if it is stable do not bother with this
            return;
        }
        //if not we need to get back to the initial block and delete block in-between
        offset_pos = (dir2 == null) ? offset_pos.offset(dir1.getOpposite())
                : offset_pos
                .offset(dir1.getOpposite())
                .offset(dir2.getOpposite());
        while (!offset_pos.equals(basePos)) {
            ((GridSupport)worldIn.getBlockState(offset_pos).getBlock()).deleteBlock(offset_pos,worldIn,player);
            offset_pos = (dir2 == null) ? offset_pos.offset(dir1.getOpposite())
                    : offset_pos
                    .offset(dir1.getOpposite())
                    .offset(dir2.getOpposite());
        }
    }

    public void deleteBlock(BlockPos pos, World world,PlayerEntity playerEntity){
        ItemStack stack = playerEntity.getHeldItemMainhand();
        BlockState state1 = world.getBlockState(pos);
        world.playEvent(playerEntity,2001,pos,Block.getStateId(state1));
        if (!world.isRemote && !playerEntity.isCreative()) {
            Block.spawnDrops(state1, world, pos, null, playerEntity, stack);
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(),35);
    }

}
