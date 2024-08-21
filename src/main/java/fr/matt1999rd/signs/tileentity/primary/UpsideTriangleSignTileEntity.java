package fr.matt1999rd.signs.tileentity.primary;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.tileentity.EditingSignTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class UpsideTriangleSignTileEntity extends EditingSignTileEntity {

    public UpsideTriangleSignTileEntity(BlockPos pos, BlockState state) {
        super(TEType.UPSIDE_TRIANGLE_SIGN,pos,state);
    }

    @Override
    protected Form getForm() {
        return Form.UPSIDE_TRIANGLE;
    }
}
