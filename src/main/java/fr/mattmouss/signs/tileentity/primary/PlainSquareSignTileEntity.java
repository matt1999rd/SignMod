package fr.mattmouss.signs.tileentity.primary;

import fr.mattmouss.signs.capabilities.PSStorage;
import fr.mattmouss.signs.capabilities.SignStorage;
import fr.mattmouss.signs.enums.PSDisplayMode;
import fr.mattmouss.signs.enums.PSPosition;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.tileentity.PanelTileEntity;
import fr.mattmouss.signs.tileentity.TEType;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.util.LazyOptional;


public class PlainSquareSignTileEntity extends PanelTileEntity {

    public PlainSquareSignTileEntity() {
        super(TEType.PLAIN_SQUARE_SIGN);
    }

    @Override
    public void tick() {
        BlockState state = getBlockState();
        if (!state.get(AbstractPanelBlock.GRID)){
            //if it is a support with grid
            super.tick();
        }
    }

    @Override
    public void renderOnScreen(int guiLeft, int guiTop,int selTextInd) {

    }

    public void registerData(PSPosition position, PSDisplayMode mode){
        storage.ifPresent(psStorage -> {
            psStorage.setInternVariable(position,mode);
        });
    }

    public PSPosition getPosition(){
        return storage.map(PSStorage::getPosition).orElse(PSPosition.DOWN_LEFT);
    }

    public PSDisplayMode getMode(){
        return storage.map(PSStorage::getDisplayMode).orElse(PSDisplayMode.DIRECTION);
    }

    private LazyOptional<PSStorage> storage = LazyOptional.of(this::getStorage).cast();

    private PSStorage getStorage() {
        return new PSStorage();
    }

}
