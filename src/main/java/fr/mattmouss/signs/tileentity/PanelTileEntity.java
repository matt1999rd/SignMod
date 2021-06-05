package fr.mattmouss.signs.tileentity;

import fr.mattmouss.signs.enums.ExtendDirection;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
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
        if (!world.isRemote){
            updateState();
        }
    }

    private void updateState() {
        BlockState state = this.getBlockState();
        boolean[] flags =new boolean[8];
        //we search first for the horizontal direction 0 : S ; 1 : W ; 2 : N ; 3 : E
        //then we search the diagonal direction 4 : (0 1) = SW ; 5 : (1 2) = NW ; 6 : (2 3) = NE ; 7 : (3 0) = SE
        for (int i=0;i<8;i++){
            ExtendDirection dir = ExtendDirection.byIndex(i);
            BlockPos pos1 = dir.offset(pos);
            BlockState state1 = world.getBlockState(pos1);
            boolean isGrid = Functions.isGridSupport(state1);
            boolean matchState = false;
            if (isGrid){
                boolean isRotated = state1.get(GridSupport.ROTATED);
                Direction.Axis axis = (state1.getBlock() instanceof GridSupport) ?
                        state1.get(BlockStateProperties.HORIZONTAL_AXIS) :
                        state1.get(BlockStateProperties.HORIZONTAL_FACING).rotateY().getAxis();
                matchState = (isRotated == dir.isRotated()) && (axis.test(dir.getDirection()));
            }
            //don't need to had isGrid because if isGrid is false matchState remain false.
            flags[i] = matchState;
        }
        Functions.setBlockState(world,pos,state,flags);
    }


    public abstract void renderOnScreen(int guiLeft, int guiTop,int selTextInd);
}
