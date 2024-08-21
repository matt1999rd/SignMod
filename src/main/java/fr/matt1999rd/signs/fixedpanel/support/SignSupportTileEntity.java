package fr.matt1999rd.signs.fixedpanel.support;

import fr.matt1999rd.signs.fixedpanel.ModBlock;
import fr.matt1999rd.signs.tileentity.PanelTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SignSupportTileEntity extends PanelTileEntity {
    public SignSupportTileEntity(BlockPos pos, BlockState state) {
        super(ModBlock.SIGN_SUPPORT_TE_TYPE,pos,state);
    }
}
