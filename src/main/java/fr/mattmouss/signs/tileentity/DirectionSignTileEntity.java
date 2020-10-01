package fr.mattmouss.signs.tileentity;

import fr.mattmouss.signs.capabilities.DirectionStorage;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.util.Text;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.LazyOptional;


import java.awt.*;

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
    public void renderOnScreen(int guiLeft, int guiTop, int selTextInd){
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
        int x1 = guiLeft,L = 126,y1 = guiTop-14,H,bgColor,limColor;
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
        AbstractGui.fill(x1+1,y1+1,x1+1+L,y1+1+H,bgColor);
        //up limit
        AbstractGui.fill(x1,y1,x1+L+2,y1+1,limColor);
        //down limit
        AbstractGui.fill(x1,y1+H+1,x1+L+2,y1+H+2,limColor);
        //left limit
        AbstractGui.fill(x1,y1+1,x1+1,y1+H+1,limColor);
        //right limit
        AbstractGui.fill(x1+L+1,y1+1,x1+L+2,y1+H+1,limColor);
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
            Text begText= getText(i,false);
            Text endText= getText(i,true);
            if (!begText.isEmpty()){
                begText.renderOnScreen(guiLeft,guiTop);
            }else if (flag)renderGrayRectangle(guiLeft, guiTop, i,false);
            if (!endText.isEmpty()){
                endText.renderOnScreen(guiLeft,guiTop);
            }else if (flag)renderGrayRectangle(guiLeft, guiTop, i, true);
        }
    }

    private void renderGrayRectangle(int guiLeft,int guiTop,int ind,boolean isEnd){
        int x1 = guiLeft+ ((isEnd)?101:2);
        //a gap of 25 and then 26
        int y1 = guiTop+2+(25*ind)+ind-(ind==0?0:1);
        int length = (isEnd)? 25:95;
        AbstractGui.fill(x1,y1,x1+length,y1+21, Color.GRAY.getRGB());
    }
}
