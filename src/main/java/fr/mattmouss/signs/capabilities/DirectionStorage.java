package fr.mattmouss.signs.capabilities;

import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.util.Text;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;


public class DirectionStorage implements IDirectionStorage, INBTSerializable<CompoundNBT> {


    boolean[] panelPlacement = new boolean[8];
    Text[] texts = new Text[5];
    Text[] endTexts = new Text[5];

    public DirectionStorage(){
        for (int i=0;i<5;i++){
            panelPlacement[i] = (i==3);
            texts[i] = Text.getDefaultText();
            endTexts[i] = Text.getDefaultText();
        }
    }

    //12 connection -> 0
    @Override
    public boolean is12connected() {
        return panelPlacement[0];
    }

    @Override
    public void remove12connection() {
        panelPlacement[0] = false;
    }

    @Override
    public void add12connection() {
        panelPlacement[0] = true;
    }

    //23 connection -> 1

    @Override
    public boolean is23connected() {
        return panelPlacement[1];
    }

    @Override
    public void remove23connection() {
        panelPlacement[1] = false;
    }

    @Override
    public void add23connection() {
        panelPlacement[1] = true;
    }

    //panel is present 2->1st 3->2nd 4->3rd

    @Override
    public boolean hasPanel(int ind) {
        if (ind<1 || ind>3){
            warn("hasPanel",ind);
            return false;
        }
        return panelPlacement[1+ind];
    }

    @Override
    public void removePanel(int ind) {
        if (ind<1 || ind>3){
            warn("removePanel",ind);
            return;
        }
        panelPlacement[1+ind] = false;
    }

    @Override
    public void addPanel(int ind) {
        if (ind<1 || ind>3){
            warn("addPanel",ind);
            return;
        }
        panelPlacement[1+ind] = true;
    }

    //is arrow on right

    @Override
    public boolean isArrowRight(int ind){
        if (ind<1 || ind>3){
            warn("isArrowRight",ind);
            return false;
        }
        return panelPlacement[4+ind];
    }

    @Override
    public void changeArrowSide(int ind) {
        if (ind<1 || ind>3){
            warn("changeArrowSide",ind);
            return;
        }
        boolean b = panelPlacement[4+ind];
        panelPlacement[4+ind] = !b;
    }

    @Override
    public Text getText(int ind,boolean isEnd) {
        if (ind<0 || ind>4){
            warn("getText",ind);
            return null;
        }
        if (isEnd){
            return endTexts[ind];
        }
        return texts[ind];
    }

    @Override
    public void setText(int ind, Text newText,boolean isEnd) {
        if (ind<0 || ind>4){
            warn("setText",ind);
            return;
        }
        if (isEnd){
            endTexts[ind] = newText;
        }else {
            texts[ind] = newText;
        }
    }

    @Override
    public Text[] getAllTexts() {
        Text[] allTexts = new Text[10];
        for (int i=0;i<5;i++){
            allTexts[2*i]=texts[i];
            allTexts[2*i+1]=endTexts[i];
        }
        return allTexts;
    }

    @Override
    public void setPanelPlacement(boolean[] panelPlacement) {
        this.panelPlacement = panelPlacement;
    }

    @Override
    public void setAllTexts(Text[] allTexts) {
        for (int i=0;i<5;i++){
            texts[i]=allTexts[2*i];
            endTexts[i]=allTexts[2*i+1];
        }
    }

    public boolean[] getPanelPlacement() {
        return panelPlacement;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT textNBT = new ListNBT();
        ListNBT boolNBT = new ListNBT();
        for (int i=0;i<5;i++){
            Text t = texts[i];
            CompoundNBT nbt1 = new CompoundNBT();
            CompoundNBT nbt2 = t.serializeNBT();
            nbt1.put("beg",nbt2);
            Text end = endTexts[i];
            CompoundNBT nbt3 = end.serializeNBT();
            nbt1.put("end",nbt3);
            textNBT.add(nbt1);
        }
        for (int i=0;i<8;i++){
            boolean b = panelPlacement[i];
            CompoundNBT nbt4 = new CompoundNBT();
            nbt4.putBoolean("bool",b);
            boolNBT.add(nbt4);
        }
        nbt.put("txts",textNBT);
        nbt.put("bool",boolNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ListNBT textNBT = (ListNBT) nbt.get("txts");
        ListNBT boolNBT = (ListNBT) nbt.get("bool");
        for (int i=0;i<5;i++){
            CompoundNBT nbt1= (CompoundNBT) textNBT.get(i);
            CompoundNBT nbt2 = nbt1.getCompound("beg");
            CompoundNBT nbt3 = nbt1.getCompound("end");
            texts[i] = Text.getTextFromNBT(nbt2);
            endTexts[i] = Text.getTextFromNBT(nbt3);
        }
        for (int i=0;i<8;i++){
            CompoundNBT nbt4 = (CompoundNBT) boolNBT.get(i);
            panelPlacement[i]=nbt4.getBoolean("bool");
        }
    }

    private void warn(String function,int ind){
        SignMod.LOGGER.warn("Bad usage of method "+function+" in Direction Storage. bad indice was given : "+ind);
    }
}
