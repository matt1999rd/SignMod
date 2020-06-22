package fr.mattmouss.signs.fixedpanel;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupportTileEntity;
import fr.mattmouss.signs.tileentity.primary.SquareSignTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlock {
    @ObjectHolder("sign:sign_support")
    public static SignSupport SIGN_SUPPORT = new SignSupport();
    @ObjectHolder("sign:sign_support")
    public static TileEntityType<SignSupportTileEntity> SIGN_SUPPORT_TE_TYPE;
    @ObjectHolder("sign:grid_support")
    public static GridSupport GRID_SUPPORT = new GridSupport();


}
