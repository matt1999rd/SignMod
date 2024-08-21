package fr.matt1999rd.signs.tileentity.primary;

import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class RectangleSignTileEntity extends DirectionSignTileEntity {

    public RectangleSignTileEntity(BlockPos pos, BlockState state) {
        super(TEType.RECTANGLE_SIGN,pos,state);
    }

}
