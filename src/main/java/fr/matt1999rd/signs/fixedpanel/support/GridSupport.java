package fr.matt1999rd.signs.fixedpanel.support;

import fr.matt1999rd.signs.enums.ExtendDirection;
import fr.matt1999rd.signs.util.Functions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

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
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        boolean isRotated = state.getValue(ROTATED);
        VoxelShape vs1 = Functions.getGridShape(isRotated,Direction.get(Direction.AxisDirection.POSITIVE,axis));
        VoxelShape vs2 = Functions.getGridShape(isRotated,Direction.get(Direction.AxisDirection.NEGATIVE,axis));
        return Shapes.or(vs1,vs2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_AXIS,ROTATED);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void playerDestroy(Level world, Player entity, BlockPos pos, BlockState state, @Nullable BlockEntity tileEntity, ItemStack stack) {
        super.playerDestroy(world, entity, pos, Blocks.AIR.defaultBlockState(), tileEntity, stack);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
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
