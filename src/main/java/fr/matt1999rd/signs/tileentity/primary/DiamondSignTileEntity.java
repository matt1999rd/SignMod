package fr.matt1999rd.signs.tileentity.primary;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class DiamondSignTileEntity extends DrawingSignTileEntity {

    public DiamondSignTileEntity(BlockPos pos, BlockState state) {
        super(TEType.DIAMOND_SIGN,pos,state);
    }

    @Override
    public Form getForm() {
        return Form.DIAMOND;
    }
}
