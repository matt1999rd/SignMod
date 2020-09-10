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

    public void changeArrowSide(int ind,boolean newValue){
        storage.ifPresent(directionStorage -> directionStorage.changeArrowSide(ind,newValue));
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
            }else {
                directionStorage.setLimColor(color, ind);
            }
        });
    }

    //boolean norms : L n for an arrow of length n P i (j) panels where to put the arrow
    //the not values are here to prevent boolean of being used both
    // (L1P1 != L3P12 != L5 and L1P2 != L3P12 != L3P23 != L5 and L1P3 != L3P23 != L5)
    public int getLFlag(){
        boolean L1P1 = hasPanel(1) && (!is12connected() || !hasPanel(2));
        boolean L1P2 = hasPanel(2) && (!is12connected() || !hasPanel(1))
                && (!is23connected() || !hasPanel(3));
        boolean L1P3 = hasPanel(3) && (!is23connected() || !hasPanel(2));
        boolean L3P12 = hasPanel(1) && hasPanel(2) && is12connected() &&
                (!is23connected() || !hasPanel(3));
        boolean L3P23 = hasPanel(2) && hasPanel(3) && is23connected() &&
                (!is12connected() || !hasPanel(1));
        boolean L5 = hasPanel(1) && hasPanel(2) && hasPanel(3) && is12connected() && is23connected();
        int flag = 0;
        if (L1P1)flag+=1;
        if (L1P2)flag+=2;
        if (L1P3)flag+=4;
        if (L3P12)flag+=8;
        if (L3P23)flag+=16;
        if (L5)flag+=32;
        return flag;
    }

    public void updateBoolean(int ind,boolean newBool){
        if (ind==1){
            if (newBool)add12connection();
            else remove12connection();
        }else if (ind==3){
            if (newBool)add23connection();
            else remove23connection();
        } else if (ind<5){
            int newInd= (ind+2)/2;
            if (newBool)addPanel(newInd);
            else removePanel(newInd);
        } else {
            int newInd = ind-4;
            changeArrowSide(newInd,newBool);
        }
    }

    @Override
    public void renderOnScreen(int guiLeft, int guiTop, int selTextInd) {
    }
}
