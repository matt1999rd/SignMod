package fr.matt1999rd.signs.tileentity.primary;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.tileentity.EditingSignTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class OctagonSignTileEntity extends EditingSignTileEntity {

    public OctagonSignTileEntity(BlockPos pos, BlockState state) {
        super(TEType.OCTAGON_SIGN,pos,state);
    }

    @Override
    protected Form getForm() {
        return Form.OCTAGON;
    }
}
