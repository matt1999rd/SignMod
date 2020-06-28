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
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Functions.deleteConnectingGrid(pos,world,player,state);
        BlockPos offset_pos = pos.up();
        //we delete all block that this support block was handling
        while (isSignSupport(world.getBlockState(offset_pos))){
            BlockState state1 = world.getBlockState(offset_pos);
            Functions.deleteBlock(offset_pos,world,player);
            Functions.deleteConnectingGrid(offset_pos,world,player,state1);
            offset_pos = offset_pos.up();
        }
        Functions.deleteBlock(pos,world,player);
        super.onBlockHarvested(world, pos, state, player);
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

}
