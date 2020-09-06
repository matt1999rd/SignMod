package fr.mattmouss.signs.tileentity;

import fr.mattmouss.signs.capabilities.DirectionStorage;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.LazyOptional;

public abstract class DirectionSignTileEntity extends PanelTileEntity{
    private LazyOptional<DirectionStorage> storage = LazyOptional.of(this::getStorage).cast();
    public DirectionSignTileEntity(TileEntityType<?> tileEntityTypeIn) {
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

    private DirectionStorage getStorage() {
        return new DirectionStorage();
    }

    public boolean hasPanel(int ind){
        return storage.map(directionStorage -> directionStorage.hasPanel(ind)).orElse(false);
    }

    public void addPanel(int ind){
        storage.ifPresent(directionStorage -> directionStorage.addPanel(ind));
    }

    public void removePanel(int ind){
        storage.ifPresent(directionStorage -> directionStorage.removePanel(ind));
    }

    public boolean isRightArrow(int ind){
        return storage.map(directionStorage -> directionStorage.isArrowRight(ind)).orElse(false);
    }

    public void changeArrowSide(int ind){
        storage.ifPresent(directionStorage -> directionStorage.changeArrowSide(ind));
    }

    public boolean is12connected(){
        return storage.map(DirectionStorage::is12connected).orElse(false);
    }

    public void add12connection(){
        storage.ifPresent(DirectionStorage::add12connection);
    }

    public void remove12connection(){
        storage.ifPresent(DirectionStorage::remove12connection);
    }

    public boolean is23connected(){
        return storage.map(DirectionStorage::is23connected).orElse(false);
    }

    public void add23connection(){
        storage.ifPresent(DirectionStorage::add23connection);
    }

    public void remove23connection(){
        storage.ifPresent(DirectionStorage::remove23connection);
    }

    public int getColor(int ind,boolean isBackGround){
        return storage.map(directionStorage -> {
            if (isBackGround){
                return directionStorage.getBgColor(ind);
            }
            return directionStorage.getLimColor(ind);
        }).orElse(0);
    }

    public void setColor(int ind,boolean isBackGround,int color){
        storage.ifPresent(directionStorage -> {
            if (isBackGround){
                directionStorage.setBgColor(color,ind);
            }
            directionStorage.setLimColor(color,ind);
        });
    }

    @Override
    public void renderOnScreen(int guiLeft, int guiTop, int selTextInd) {
    }
}
