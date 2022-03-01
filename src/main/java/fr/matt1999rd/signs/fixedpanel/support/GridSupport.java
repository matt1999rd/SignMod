package fr.matt1999rd.signs.fixedpanel.support;

import fr.matt1999rd.signs.enums.ExtendDirection;
import fr.matt1999rd.signs.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;


import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;



public class GridSupport extends Block {

    //this property specified if the grid support is rotated in trigonometric rotation of angle 45°
    public static BooleanProperty ROTATED;
    static {
        ROTATED = BooleanProperty.create("rotated");
    }
    public GridSupport() {
        super(Properties.of(Material.STONE).noOcclusion());
        this.setRegistryName("grid_support");
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        boolean isRotated = state.getValue(ROTATED);
        VoxelShape vs1 = Functions.getGridShape(isRotated,Direction.get(Direction.AxisDirection.POSITIVE,axis));
        VoxelShape vs2 = Functions.getGridShape(isRotated,Direction.get(Direction.AxisDirection.NEGATIVE,axis));
        return VoxelShapes.or(vs1,vs2);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_AXIS,ROTATED);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void playerDestroy(World world, PlayerEntity entity, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack) {
        super.playerDestroy(world, entity, pos, Blocks.AIR.defaultBlockState(), tileEntity, stack);
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        Functions.deleteOtherGrid(pos,worldIn,player,state);
        Functions.deleteBlock(pos,worldIn,player);
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    public BlockState getStateFromPos(BlockPos pos,BlockPos supportPos) {
        BlockState state = this.defaultBlockState();
        ExtendDirection dir = ExtendDirection.getDirectionFromPos(supportPos,pos);
        if (dir != null){
            return state.setValue(GridSupport.ROTATED,dir.isRotated()).setValue(BlockStateProperties.HORIZONTAL_AXIS,dir.getAxis());
        }else {
            return null;
        }
    }

}
