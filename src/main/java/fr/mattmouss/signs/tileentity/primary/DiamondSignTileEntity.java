package fr.mattmouss.signs.tileentity.primary;

import fr.mattmouss.signs.capabilities.SignStorage;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.tileentity.DrawingSignTileEntity;
import fr.mattmouss.signs.tileentity.PanelTileEntity;
import fr.mattmouss.signs.tileentity.TEType;
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
