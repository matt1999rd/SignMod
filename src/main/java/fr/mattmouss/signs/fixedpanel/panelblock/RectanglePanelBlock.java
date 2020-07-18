package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.ScreenType;
import fr.mattmouss.signs.tileentity.primary.RectangleSignTileEntity;
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
        return ScreenType.EDITING_AND_COLOURING_SCREEN;
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
