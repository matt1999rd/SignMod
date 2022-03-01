package fr.matt1999rd.signs.fixedpanel.panelblock;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.enums.ScreenType;
import fr.matt1999rd.signs.tileentity.primary.OctogoneSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class OctagonPanelBlock extends AbstractPanelBlock {
    public OctagonPanelBlock() {
        super("stop");
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.EDITING_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.OCTAGON;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new OctogoneSignTileEntity();
    }
}
