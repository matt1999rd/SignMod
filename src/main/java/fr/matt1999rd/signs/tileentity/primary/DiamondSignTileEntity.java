package fr.matt1999rd.signs.tileentity.primary;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.util.LazyOptional;


public class DiamondSignTileEntity extends DrawingSignTileEntity {

    public DiamondSignTileEntity() {
        super(TEType.DIAMOND_SIGN);
    }

    @Override
    protected Form getForm() {
        return Form.DIAMOND;
    }
}
