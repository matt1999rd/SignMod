package fr.matt1999rd.signs.tileentity.primary;

import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.util.LazyOptional;


public class RectangleSignTileEntity extends DirectionSignTileEntity {

    public RectangleSignTileEntity() {
        super(TEType.RECTANGLE_SIGN);
    }

}
