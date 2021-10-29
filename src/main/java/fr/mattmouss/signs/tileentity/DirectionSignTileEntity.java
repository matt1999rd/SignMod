package fr.mattmouss.signs.tileentity;

import fr.mattmouss.signs.capabilities.DirectionCapability;
import fr.mattmouss.signs.capabilities.DirectionStorage;
import fr.mattmouss.signs.capabilities.SignCapability;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.tileentity.primary.ArrowSignTileEntity;
import fr.mattmouss.signs.tileentity.primary.RectangleSignTileEntity;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Text;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

import static fr.mattmouss.signs.util.Functions.*;

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

    //take three possible int value and return direct boolean in storage (1-2-3)
    private boolean isArrowRight(int ind){
        return storage.map(directionStorage -> directionStorage.isArrowRight(ind)).orElse(false);
    }

    //take 5 possible value for each position to write (0-4)
    public boolean isRightArrow(int ind){
        if (ind%2==0){
            //in the case of corresponding boolean : ok !
            return isArrowRight((ind+2)/2);
        }else if (ind == 1 || is12connected()){
            //in the case of 12 gap or 23 with 12 connection get 1 arrow direction
            return isArrowRight(1);
        }else {
            //in the case of 23 without 12 connection
            return isArrowRight(2);
        }
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

    public Text getText(int ind,boolean isEnd){
        Text t = storage.map(directionStorage -> {
            return directionStorage.getText(ind,isEnd);
        }).orElse(Text.getDefaultText());
        return t;
    }

    public void setText(int ind, boolean isEnd, Text newText){
        storage.ifPresent(d->{
            d.setText(ind,newText,isEnd);
        });
    }

    //text center boolean

    public boolean isTextCentered(){
        return storage.map(DirectionStorage::isTextCentered).orElse(true);
    }

    public void setCenterText(boolean isTextCentered){
        storage.ifPresent(directionStorage -> directionStorage.setCenterText(isTextCentered));
    }

    public void centerText(boolean center){
        for (int i=0;i<5;i++){
            Text beg = getText(i,false);
            int text_length = beg.getLength();
            float x = xOrigin+((center) ? (panelLength-text_length)/2.0F: st_gap);
            float y = beg.getY();
            beg.setPosition(x,y);
        }
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

    public void renderOnScreen(int guiLeft, int guiTop){
        int flag = getLFlag();
        if (flag == 0)return;
        for (int i=0;i<6;i++){
            if (((flag>>i)&1) == 1){
                renderPart(i,guiLeft,guiTop);
            }
        }
        renderText(guiLeft,guiTop);
    }

    //render each of the 6 part 0->L1P1 1->L1P2 2->L1P3 3->L3P12 4->L3P23 5->L5
    private void renderPart(int indFlag,int guiLeft,int guiTop){
        if (indFlag<0 || indFlag>5)return;
        int x1 = guiLeft,y1 = guiTop-14,H,bgColor,limColor;
        //start condition
        //L1P1 or L3P12 or L5 -> start at 1
        if (indFlag==0 || indFlag == 3 || indFlag == 5){
            y1+=14;
            bgColor = getColor(1,true);
            limColor = getColor(1,false);
        //L1P2 or L3P23 -> start at 2
        }else if (indFlag%3 == 1){
            y1 += 65;
            bgColor = getColor(2,true);
            limColor = getColor(2,false);
        //L1P3 -> start at 3
        } else  {
            y1 += 117;
            bgColor = getColor(3, true);
            limColor = getColor(3, false);
        }
        //length condition
        //L1P1/2/3
        if (indFlag<3){
            H = 23;
        //L2P12/23 not exactly the same length to get 128 * 128 square
        }else if (indFlag != 5) {
            H = 71 + indFlag;
        }else {
            H = 126;
        }
        AbstractGui.fill(x1+1,y1+1,x1+ panelLength-1,y1+1+H,bgColor);
        //up limit
        AbstractGui.fill(x1,y1,x1+ panelLength,y1+1,limColor);
        //down limit
        AbstractGui.fill(x1,y1+H+1,x1+ panelLength,y1+H+2,limColor);
        //left limit
        AbstractGui.fill(x1,y1+1,x1+1,y1+H+1,limColor);
        //right limit
        AbstractGui.fill(x1+ panelLength-1,y1+1,x1+ panelLength,y1+H+1,limColor);
    }

    public boolean isCellPresent(int i){
         return storage.map(directionStorage -> {
            boolean[] panelPlacement = directionStorage.getPanelPlacement();
            if (i == 1 || i == 3){
                return panelPlacement[i] && panelPlacement[i-1] && panelPlacement[i+1];
            }
            return panelPlacement[i];
         }).orElse(false);
    }


    private void renderText(int guiLeft,int guiTop){
        for (int i=0;i<5;i++){
            // flag indicates if we have to display the gray empty text rectangle
            boolean flag = isCellPresent(i);
            if (!isTextCentered()){
                Text endText= getText(i,true);
                if (!endText.isEmpty()){
                    endText.renderOnScreen((int) (guiLeft-xOrigin),guiTop);
                }else if (flag)renderGrayRectangle(guiLeft, guiTop, i, true);
            }
            Text begText= getText(i,false);
            if (!begText.isEmpty()){
                begText.renderOnScreen((int)(guiLeft-xOrigin),guiTop);
            }else if (flag)renderGrayRectangle(guiLeft, guiTop, i,false);

        }
    }

    private void flipSpecifiedText(int ind){
        Text beg = getText(ind,false);
        if (!beg.isEmpty()) {
            int length = beg.getLength();
            float x = xOrigin+ ((beg.getX() == st_gap+xOrigin) ? panelLength - length - st_gap: st_gap);
            float y = beg.getY();
            beg.setPosition(x, y);
        }
        setText(ind,false,new Text(beg));

        Text end = getText(ind,true);
        if (!end.isEmpty()) {
            int length = end.getLength();
            float x = Functions.xOrigin+((end.getX() == st_gap+xOrigin) ? panelLength - length - st_gap: st_gap);
            float y = end.getY();
            end.setPosition(x, y);
        }
        setText(ind,true,new Text(end));
    }

    //flip all text that are in area connected to this one 1 or 2 or 3
    public void flipText(int ind){
        if (this instanceof ArrowSignTileEntity){
            if (ind == 0){
                flipSpecifiedText(0);
                if (is12connected()){
                    flipSpecifiedText(1);
                    flipSpecifiedText(2);
                    if (is23connected()){
                        flipSpecifiedText(3);
                        flipSpecifiedText(4);
                    }
                }
            } else if (ind == 1 && !is12connected()){
                flipSpecifiedText(2);
                if (is23connected()){
                    flipSpecifiedText(3);
                    flipSpecifiedText(4);
                }
            } else if (!is23connected()){
                flipSpecifiedText(4);
            }
        }
    }

    private void renderGrayRectangle(int guiLeft,int guiTop,int ind,boolean isEnd) {
        int x1,length;
        if (isTextCentered()) {
            x1 = guiLeft + st_gap;
            length = panelLength-2*st_gap;
        }else {
            if (this instanceof RectangleSignTileEntity || this.isRightArrow(ind)) {
                x1 = guiLeft + st_gap + ((isEnd) ? begTextLength+center_gap : 0);
            } else {
                x1 = guiLeft + st_gap + ((isEnd) ? 0 : endTextLength+center_gap);
            }
            length = (isEnd) ? endTextLength : begTextLength;
        }
        //a gap of 25 and then 26
        int y1 = guiTop+2+(25*ind)+ind-(ind==0?0:1);
        AbstractGui.fill(x1,y1,x1+length,y1+21, Color.GRAY.getRGB());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == DirectionCapability.DIRECTION_STORAGE){
            return storage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT storage_tag = compound.getCompound("direction");
        getCapability(DirectionCapability.DIRECTION_STORAGE).ifPresent(s -> ((INBTSerializable<CompoundNBT>) s).deserializeNBT(storage_tag));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        getCapability(DirectionCapability.DIRECTION_STORAGE).ifPresent(storage -> {
            CompoundNBT compoundNBT = ((INBTSerializable<CompoundNBT>) storage).serializeNBT();
            tag.put("direction", compoundNBT);
        });
        return super.write(tag);
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }
}
