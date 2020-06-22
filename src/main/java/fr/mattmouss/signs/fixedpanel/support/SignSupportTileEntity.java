package fr.mattmouss.signs.fixedpanel.support;

import fr.mattmouss.signs.fixedpanel.ModBlock;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class SignSupportTileEntity extends TileEntity implements ITickableTileEntity {
    public SignSupportTileEntity() {
        super(ModBlock.SIGN_SUPPORT_TE_TYPE);
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
            Block block = world.getBlockState(pos1).getBlock();
            flags[i] = (block instanceof GridSupport);
        }
        Functions.setBlockState(world,pos,state,flags);
    }
}
