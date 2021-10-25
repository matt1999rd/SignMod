package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.ScreenType;
import fr.mattmouss.signs.tileentity.primary.OctogoneSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class OctogonePanelBlock extends AbstractPanelBlock {
    public OctogonePanelBlock() {
        super("stop");
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.EDITING_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.OCTOGONE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new OctogoneSignTileEntity();
    }
}
