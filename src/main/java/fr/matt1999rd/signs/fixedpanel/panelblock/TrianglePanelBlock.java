package fr.matt1999rd.signs.fixedpanel.panelblock;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.enums.ScreenType;
import fr.matt1999rd.signs.tileentity.primary.TriangleSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class TrianglePanelBlock extends AbstractPanelBlock {
    public TrianglePanelBlock() {
        super("triangle");
    }


    @Override
    public ScreenType getScreenType() {
        return ScreenType.DRAWING_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.TRIANGLE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TriangleSignTileEntity();
    }
}
