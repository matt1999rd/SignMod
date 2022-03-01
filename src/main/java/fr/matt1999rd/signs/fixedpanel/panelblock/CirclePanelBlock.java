package fr.matt1999rd.signs.fixedpanel.panelblock;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.enums.ScreenType;
import fr.matt1999rd.signs.tileentity.primary.CircleSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class CirclePanelBlock extends AbstractPanelBlock {
    public CirclePanelBlock() {
        super("circle");
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.DRAWING_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.CIRCLE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CircleSignTileEntity();
    }
}
