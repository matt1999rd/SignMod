package fr.matt1999rd.signs.tileentity;

import fr.matt1999rd.signs.enums.ExtendDirection;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.util.Functions;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public abstract class PanelTileEntity extends TileEntity implements ITickableTileEntity {

    public PanelTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        if (!level.isClientSide){
            updateState();
        }
    }

    private void updateState() {
        BlockState state = this.getBlockState();
        int flags = ExtendDirection.makeFlagsFromFunction(direction -> {
            BlockPos pos1 = direction.relative(worldPosition);
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
        Functions.setBlockState(level,worldPosition,state,flags);
    }
    
}
