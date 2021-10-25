package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.ScreenType;
import fr.mattmouss.signs.tileentity.primary.DiamondSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class DiamondPanelBlock extends AbstractPanelBlock {
    public DiamondPanelBlock() {
        super("diamond");
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.DRAWING_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.DIAMOND;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new DiamondSignTileEntity();
    }
}
