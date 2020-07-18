package fr.mattmouss.signs.tileentity;

import fr.mattmouss.signs.capabilities.SignStorage;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.LazyOptional;

public class DrawingSignTileEntity extends PanelTileEntity {
    public DrawingSignTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        BlockState state = getBlockState();
        if (!state.get(AbstractPanelBlock.GRID)){
            //if it is a support with grid
            super.tick();
        }
    }

    private LazyOptional<SignStorage> storage = LazyOptional.of(this::getStorage).cast();

    private SignStorage getStorage() {
        return new SignStorage();
    }
}
