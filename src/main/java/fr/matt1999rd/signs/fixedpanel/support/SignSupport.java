package fr.matt1999rd.signs.fixedpanel.support;

import fr.matt1999rd.signs.enums.ExtendDirection;
import fr.matt1999rd.signs.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;


public class SignSupport extends Block {

    public SignSupport() {
        super(Properties.of(Material.STONE, MaterialColor.STONE).noOcclusion());
        this.setRegistryName("sign_support");
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape vs = Functions.getSupportShape();
        int flags = Functions.getFlagsFromState(state);
        for (ExtendDirection direction : ExtendDirection.values()){
            if ((flags&1)==1){
                assert direction != null;
                vs=VoxelShapes.or(vs,Functions.getGridShape(direction.isRotated(),direction.getDirection()));
            }
            flags = flags >> 1;
        }
        return vs;
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
    public void playerDestroy(World world, PlayerEntity entity, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack) {
        super.playerDestroy(world, entity, pos, Blocks.AIR.defaultBlockState(), tileEntity, stack);
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Functions.deleteConnectingGrid(pos,world,player,state);
        BlockPos offset_pos = pos.above();
        //we delete all sign support block that this support block was handling
        while (Functions.isSignSupport(world.getBlockState(offset_pos))){
            BlockState state1 = world.getBlockState(offset_pos);
            Functions.deleteBlock(offset_pos,world,player);
            Functions.deleteConnectingGrid(offset_pos,world,player,state1);
            offset_pos = offset_pos.above();
        }
        Functions.deleteBlock(pos,world,player);
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer != null){
            Functions.setBlockState(worldIn,pos,state,0);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(Arrays.stream(ExtendDirection.values()).map(ExtendDirection::getSupportProperty).toArray(BooleanProperty[]::new));
    }

}
