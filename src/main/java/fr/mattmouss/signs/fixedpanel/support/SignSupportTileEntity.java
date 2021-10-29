package fr.mattmouss.signs.fixedpanel.support;

import fr.mattmouss.signs.fixedpanel.ModBlock;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.tileentity.PanelTileEntity;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class SignSupportTileEntity extends PanelTileEntity {
    public SignSupportTileEntity() {
        super(ModBlock.SIGN_SUPPORT_TE_TYPE);
    }
}
