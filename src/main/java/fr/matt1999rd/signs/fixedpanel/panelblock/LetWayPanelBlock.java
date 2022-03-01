package fr.matt1999rd.signs.fixedpanel.panelblock;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.enums.ScreenType;
import fr.matt1999rd.signs.tileentity.primary.UpsideTriangleSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class LetWayPanelBlock extends AbstractPanelBlock {
    public LetWayPanelBlock() {
        super("let_way");
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.EDITING_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.UPSIDE_TRIANGLE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new UpsideTriangleSignTileEntity();
    }
}
