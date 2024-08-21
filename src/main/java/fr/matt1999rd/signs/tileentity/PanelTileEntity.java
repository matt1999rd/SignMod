package fr.matt1999rd.signs.tileentity;

import fr.matt1999rd.signs.enums.ExtendDirection;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.util.Functions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public abstract class PanelTileEntity extends BlockEntity {

    public PanelTileEntity(BlockEntityType<?> tileEntityTypeIn,BlockPos pos,BlockState state) {
        super(tileEntityTypeIn,pos,state);
    }

    public void tick(Level level, BlockState blockState, BlockPos blockPos, PanelTileEntity t) {
        if (!level.isClientSide){
            updateState(level,blockState,blockPos);
        }
    }

    private void updateState(Level level,BlockState state,BlockPos pos) {
        int flags = ExtendDirection.makeFlagsFromFunction(direction -> { //todo : check this function for sign support and grid support tile entity
            BlockPos pos1 = direction.relative(pos);
            BlockState state1 = level.getBlockState(pos1);
            boolean isGrid = Functions.isGridSupport(state1);
            boolean matchState = false;
            if (isGrid){
                boolean isRotated = state1.getValue(GridSupport.ROTATED);
                Direction.Axis axis = (state1.getBlock() instanceof GridSupport) ?
                        state1.getValue(BlockStateProperties.HORIZONTAL_AXIS) :
                        state1.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise().getAxis();
                matchState = (isRotated == direction.isRotated()) && (axis.test(direction.getDirection()));
            }
            return matchState;
        });
        Functions.setBlockState(level,pos,state,flags);
    }


}
