package fr.matt1999rd.signs.tileentity.primary;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class CircleSignTileEntity extends DrawingSignTileEntity {

    public CircleSignTileEntity(BlockPos pos, BlockState state) {
        super(TEType.CIRCLE_SIGN,pos,state);
    }

    @Override
    protected Form getForm() {
        return Form.CIRCLE;
    }
}
