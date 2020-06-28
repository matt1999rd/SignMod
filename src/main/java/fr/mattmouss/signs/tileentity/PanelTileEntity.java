package fr.mattmouss.signs.tileentity;

import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.BlockState;
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
            Direction first_dir = (i>3) ? Direction.byHorizontalIndex(i-4): Direction.byHorizontalIndex(i);
            Direction second_dir = (i>3) ? Direction.byHorizontalIndex((i-3)%4):Direction.NORTH; //useless when i<4
            BlockPos pos1 = (i>3)? pos.offset(first_dir).offset(second_dir): pos.offset(first_dir);
            BlockState state1 = world.getBlockState(pos1);
            flags[i] = Functions.isGridSupport(state1);
        }
        Functions.setBlockState(world,pos,state,flags);
    }



}
