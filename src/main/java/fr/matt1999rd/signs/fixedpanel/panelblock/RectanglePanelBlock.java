package fr.matt1999rd.signs.fixedpanel.panelblock;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.enums.ScreenType;
import fr.matt1999rd.signs.tileentity.primary.RectangleSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class RectanglePanelBlock extends AbstractPanelBlock {
    public RectanglePanelBlock() {
        super("rectangle");
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.DIRECTION_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.RECTANGLE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RectangleSignTileEntity();
    }
}
