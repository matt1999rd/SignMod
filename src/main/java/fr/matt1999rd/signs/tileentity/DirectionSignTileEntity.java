package fr.matt1999rd.signs.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.matt1999rd.signs.util.Text;
import fr.matt1999rd.signs.capabilities.DirectionCapability;
import fr.matt1999rd.signs.capabilities.DirectionStorage;
import fr.matt1999rd.signs.tileentity.primary.ArrowSignTileEntity;
import fr.matt1999rd.signs.tileentity.primary.RectangleSignTileEntity;
import fr.matt1999rd.signs.util.Vector2i;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import static fr.matt1999rd.signs.util.DirectionSignConstants.*;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public abstract class DirectionSignTileEntity extends PanelTileEntity{

    private final LazyOptional<DirectionStorage> storage = LazyOptional.of(this::getStorage).cast();
    public DirectionSignTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        BlockState state = getBlockState();
        if (!state.getValue(AbstractPanelBlock.GRID)){
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

    public Text getText(int ind, boolean isEnd){
        return storage.map(directionStorage -> directionStorage.getText(ind,isEnd)).orElse(Text.getDefaultText());
    }

    public void setText(int ind, boolean isEnd, Text newText){
        storage.ifPresent(d-> d.setText(ind,newText,isEnd));
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
            float text_length = beg.getLength(true,true);
            float x = ((center) ? (horPixelNumber-text_length)/2.0F: sideGapPixelNumber);
            float y = beg.getY(false);
            beg.setPosition(x,y,false,false);
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

    public void renderOnScreen(MatrixStack stack,int guiLeft, int guiTop,int selTextInd){
        int flag = getLFlag();
        if (flag == 0)return;
        for (int i=0;i<6;i++){
            if (((flag>>i)&1) == 1){
                renderPart(stack,i,guiLeft,guiTop);
            }
        }
        renderText(stack,guiLeft,guiTop,selTextInd);
    }

    //render each of the 6 part 0->L1P1 1->L1P2 2->L1P3 3->L3P12 4->L3P23 5->L5
    private void renderPart(MatrixStack stack,int indFlag, int guiLeft, int guiTop){
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
        AbstractGui.fill(stack,x1+1,y1+1,x1+ horPixelNumber-1,y1+1+H,bgColor);
        //up limit
        AbstractGui.fill(stack,x1,y1,x1+ horPixelNumber,y1+1,limColor);
        //down limit
        AbstractGui.fill(stack,x1,y1+H+1,x1+ horPixelNumber,y1+H+2,limColor);
        //left limit
        AbstractGui.fill(stack,x1,y1+1,x1+1,y1+H+1,limColor);
        //right limit
        AbstractGui.fill(stack,x1+ horPixelNumber-1,y1+1,x1+ horPixelNumber,y1+H+1,limColor);
    }

    public boolean isCellPresent(int i){
         return storage.map(directionStorage -> directionStorage.isCellPresent(i)).orElse(false);
    }


    private void renderText(MatrixStack stack,int guiLeft,int guiTop,int selTextInd){
        Vector2i origin = new Vector2i(guiLeft,guiTop); //todo : two bugs found : 2 -> position of text when using style is incorrect : length and height must be managed differently
        Vector2f pixelDimension = new Vector2f(1.0F,1.0F);
        for (int i=0;i<5;i++){
            // flag indicates if we have to display the gray empty text rectangle
            boolean flag = isCellPresent(i);
            boolean isSelected;
            if (!flag)continue;
            if (!isTextCentered()){
                Text endText= getText(i,true);
                isSelected = selTextInd == 2*i+1;
                if (!endText.isEmpty()){
                    endText.renderOnScreen(stack,origin,pixelDimension,isSelected,false);
                }else renderGrayRectangle(stack,guiLeft, guiTop, i, true,isSelected);
            }
            Text begText= getText(i,false);
            isSelected = selTextInd == 2*i;
            if (!begText.isEmpty()){
                begText.renderOnScreen(stack,origin,pixelDimension,isSelected,false);
            }else renderGrayRectangle(stack,guiLeft, guiTop, i,false,isSelected);

        }
    }

    private void flipSpecifiedText(int ind){
        Text[] allText = new Text[]{getText(ind,false),getText(ind,true)};
        for (int i=0;i<2;i++){
            Text t = allText[i];
            if (!t.isEmpty()){
                float length = t.getLength(true,false);
                float x = horPixelNumber - length - t.getX(false);
                float y = t.getY(false);
                t.setPosition(x, y,false,false);
            }
        }
        setText(ind,false,new Text(allText[0]));
        setText(ind,true, new Text(allText[1]));
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

    private void renderGrayRectangle(MatrixStack stack,int guiLeft,int guiTop,int ind,boolean isEnd,boolean isSelected) {
        int x1,length;
        if (isTextCentered()) {
            x1 = guiLeft + sideGapPixelNumber;
            length = horPixelNumber-2* sideGapPixelNumber;
        }else {
            if (this instanceof RectangleSignTileEntity || this.isRightArrow(ind)) {
                x1 = guiLeft + sideGapPixelNumber + ((isEnd) ? begTextPixelNumber+ centerGapPixelNumber : 0);
            } else {
                x1 = guiLeft + sideGapPixelNumber + ((isEnd) ? 0 : endTextPixelNumber+ centerGapPixelNumber);
            }
            length = (isEnd) ? endTextPixelNumber : begTextPixelNumber;
        }
        //a gap of 25 and then 26
        int y1 = guiTop+2+(25*ind)+ind-(ind==0?0:1);
        AbstractGui.fill(stack,x1,y1,x1+length,y1+21, (isSelected ? Color.DARK_GRAY : Color.GRAY).getRGB());
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
    public void load(BlockState state,CompoundNBT compound) {
        CompoundNBT storage_tag = compound.getCompound("direction");
        getCapability(DirectionCapability.DIRECTION_STORAGE).ifPresent(s -> ((INBTSerializable<CompoundNBT>) s).deserializeNBT(storage_tag));
        super.load(state,compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        getCapability(DirectionCapability.DIRECTION_STORAGE).ifPresent(storage -> {
            CompoundNBT compoundNBT = ((INBTSerializable<CompoundNBT>) storage).serializeNBT();
            tag.put("direction", compoundNBT);
        });
        return super.save(tag);
    }

    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }
}
