package fr.matt1999rd.signs.tileentity.primary;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class SquareSignTileEntity extends DrawingSignTileEntity {

    public SquareSignTileEntity(BlockPos pos, BlockState state) {
        super(TEType.SQUARE_SIGN,pos,state);
    }

    @Override
    protected Form getForm() {
        return Form.SQUARE;
    }
}
