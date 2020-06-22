package fr.mattmouss.signs.fixedpanel.support;

import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static fr.mattmouss.signs.util.Functions.*;


public class SignSupport extends Block {

    public SignSupport() {
        super(Properties.create(Material.ROCK, MaterialColor.STONE));
        this.setRegistryName("sign_support");
    }

    //1.14.4 function replaced by notSolid()
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SignSupportTileEntity();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void harvestBlock(World world, PlayerEntity entity, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack) {
        super.harvestBlock(world, entity, pos, Blocks.AIR.getDefaultState(), tileEntity, stack);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        this.deleteBlock(pos,worldIn,player);
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    private void deleteConnectingGrid(BlockPos pos, World worldIn, PlayerEntity player,BlockState state) {
        boolean[] flags = Functions.getFlagsFromState(state);
        for (int i=0;i<8;i++){
            if (flags[i]){
                if (i<4){
                    Direction dir1 = Direction.byHorizontalIndex(i);
                    BlockPos gridPos = pos.offset(dir1);
                    BlockState gridState = worldIn.getBlockState(gridPos);
                    GridSupport gridSupport = (GridSupport)(worldIn.getBlockState(gridPos).getBlock());
                    gridSupport.onBlockHarvested(worldIn,gridPos,gridState,player);
                }else {
                    Direction dir1 = Direction.byHorizontalIndex(i-4);
                    Direction dir2 = Direction.byHorizontalIndex((i-3)%4);
                    BlockPos gridPos = pos.offset(dir1).offset(dir2);
                    BlockState gridState = worldIn.getBlockState(gridPos);
                    GridSupport gridSupport = (GridSupport)(worldIn.getBlockState(gridPos).getBlock());
                    gridSupport.onBlockHarvested(worldIn,gridPos,gridState,player);
                }
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer != null){
            boolean[] flags = {false,false,false,false,false,false,false,false};
            Functions.setBlockState(worldIn,pos,state,flags);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(
                NORTH_EAST,
                NORTH_WEST,
                SOUTH_EAST,
                SOUTH_WEST,
                BlockStateProperties.NORTH,
                BlockStateProperties.SOUTH,
                BlockStateProperties.WEST,
                BlockStateProperties.EAST);
    }

    public void deleteBlock(BlockPos pos, World world, PlayerEntity playerEntity){
        BlockState state = world.getBlockState(pos);
        this.deleteConnectingGrid(pos,world,playerEntity,state);
        BlockState state2 = world.getBlockState(pos.up());
        if (state2.getBlock() instanceof SignSupport){
            ((SignSupport) state2.getBlock()).deleteBlock(pos.up(),world,playerEntity);
        }
        ItemStack stack = playerEntity.getHeldItemMainhand();
        BlockState state1 = world.getBlockState(pos);
        world.playEvent(playerEntity,2001,pos,Block.getStateId(state1));
        if (!world.isRemote && !playerEntity.isCreative() && playerEntity.canHarvestBlock(state1)) {
            Block.spawnDrops(state1, world, pos, null, playerEntity, stack);
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(),35);
    }

}
