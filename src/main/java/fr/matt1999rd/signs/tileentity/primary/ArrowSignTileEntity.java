package fr.matt1999rd.signs.tileentity.primary;

import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class ArrowSignTileEntity extends DirectionSignTileEntity {

    public ArrowSignTileEntity(BlockPos pos, BlockState state) {
        super(TEType.ARROW_SIGN,pos,state);
    }

}
