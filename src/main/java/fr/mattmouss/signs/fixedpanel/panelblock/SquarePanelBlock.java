package fr.mattmouss.signs.fixedpanel.panelblock;


import fr.mattmouss.signs.tileentity.primary.SquareSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class SquarePanelBlock extends AbstractPanelBlock {
    public SquarePanelBlock() {
        super("square");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SquareSignTileEntity();
    }

}
