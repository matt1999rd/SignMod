package fr.mattmouss.signs.tileentity.primary;

import fr.mattmouss.signs.capabilities.SignStorage;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.tileentity.PanelTileEntity;
import fr.mattmouss.signs.tileentity.TEType;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.util.LazyOptional;


public class DiamondSignTileEntity extends PanelTileEntity {

    public DiamondSignTileEntity() {
        super(TEType.DIAMOND_SIGN);
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
