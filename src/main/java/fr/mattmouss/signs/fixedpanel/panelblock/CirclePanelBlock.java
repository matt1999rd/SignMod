package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.ScreenType;
import fr.mattmouss.signs.tileentity.primary.CircleSignTileEntity;
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
