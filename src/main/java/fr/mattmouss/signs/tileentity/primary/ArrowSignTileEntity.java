package fr.mattmouss.signs.tileentity.primary;

import fr.mattmouss.signs.capabilities.SignStorage;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.tileentity.DirectionSignTileEntity;
import fr.mattmouss.signs.tileentity.PanelTileEntity;
import fr.mattmouss.signs.tileentity.TEType;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.util.LazyOptional;


public class ArrowSignTileEntity extends DirectionSignTileEntity {

    public ArrowSignTileEntity() {
        super(TEType.ARROW_SIGN);
    }

}
