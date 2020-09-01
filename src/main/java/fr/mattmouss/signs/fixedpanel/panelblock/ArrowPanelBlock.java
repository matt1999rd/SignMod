package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.ScreenType;
import fr.mattmouss.signs.tileentity.primary.ArrowSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class ArrowPanelBlock extends AbstractPanelBlock {
    public ArrowPanelBlock() {
        super("direction");
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.DIRECTION_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.ARROW;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ArrowSignTileEntity();
    }
}
