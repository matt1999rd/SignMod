package fr.matt1999rd.signs.capabilities;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.util.Text;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.awt.*;


public class DirectionStorage implements IDirectionStorage, INBTSerializable<CompoundNBT> {

    // panelPlacement boolean table includes five value for the existence of panel
    // 0 -> first panel exists
    // 1 -> link between first and second exists
    // 2 -> second panel exists
    // 3 -> link between second and third exists
    // 4 -> third panel exists
    // 5 -> arrow is right for first panel
    // 6 -> arrow is right for second panel
    // 7 -> arrow is right for third panel

    boolean[] panelPlacement = new boolean[8];

    Text[] texts = new Text[5];
    Text[] endTexts = new Text[5];
    Color[] bg_color,limit_color;
    boolean center_text;

    public DirectionStorage(){
        for (int i=0;i<5;i++){
            panelPlacement[i] = (i==2);
            texts[i] = Text.getDefaultText();
            endTexts[i] = Text.getDefaultText();
        }
        for (int i=0;i<3;i++){
            panelPlacement[5+i] = false;
        }
        bg_color = new Color[]{Color.WHITE,Color.WHITE,Color.WHITE};
        limit_color = new Color[]{Color.BLACK,Color.BLACK,Color.BLACK};
        center_text = false;
    }

    //12 connection -> 1
    @Override
    public boolean is12connected() {
        return panelPlacement[1];
    }

    @Override
    public void remove12connection() {
        panelPlacement[1] = false;
    }

    @Override
    public void add12connection() {
        panelPlacement[1] = true;
    }

    //23 connection -> 3

    @Override
    public boolean is23connected() {
        return panelPlacement[3];
    }

    @Override
    public void remove23connection() {
        panelPlacement[3] = false;
    }

    @Override
    public void add23connection() {
        panelPlacement[3] = true;
    }

    //panel is present 0->1st 2->2nd 4->3rd

    @Override
    public boolean hasPanel(int ind) {
        if (ind<1 || ind>3){
            warn("hasPanel",ind);
            return false;
        }
        return panelPlacement[(ind-1)*2];
    }

    @Override
    public void removePanel(int ind) {
        if (ind<1 || ind>3){
            warn("removePanel",ind);
            return;
        }
        panelPlacement[(ind-1)*2] = false;
    }

    @Override
    public void addPanel(int ind) {
        if (ind<1 || ind>3){
            warn("addPanel",ind);
            return;
        }
        panelPlacement[(ind-1)*2] = true;
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

    public void changeArrowSide(int ind, boolean newValue) {
        if (ind<1 || ind>3){
            warn("changeArrowSide",ind);
            return;
        }
        panelPlacement[4+ind] = newValue;
    }

    //color of background and limit

    @Override
    public int getBgColor(int ind) {
        if (ind<1 || ind>3){
            warn("getBgColor",ind);
            return 0;
        }
        return bg_color[ind-1].getRGB();
    }

    @Override
    public void setBgColor(int color,int ind) {
        if (ind<1 || ind>3){
            warn("setBgColor",ind);
            return;
        }
        bg_color[ind-1] = new Color(color,true);
    }

    @Override
    public int getLimColor(int ind) {
        if (ind<1 || ind>3){
            warn("getLimColor",ind);
            return 0;
        }
        return limit_color[ind-1].getRGB();
    }

    @Override
    public void setLimColor(int color,int ind) {
        if (ind<1 || ind>3){
            warn("setLimColor",ind);
            return;
        }
        limit_color[ind-1] = new Color(color,true);
    }

    //for text treatment

    @Override
    public Text getText(int ind,boolean isEnd) {
        if (ind<0 || ind>4){
            warn("getText",ind);
            return Text.getDefaultText();
        }
        if (isEnd) {
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
        if (isCellPresent(ind)) {
            if (isEnd) {
                endTexts[ind] = newText;
            } else {
                texts[ind] = newText;
            }
        }
    }

    //for read and write in capability

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
    public void setAllTexts(Text[] allTexts) {
        for (int i=0;i<5;i++){
            texts[i]=allTexts[2*i];
            endTexts[i]=allTexts[2*i+1];
        }
    }

    @Override
    public void setPanelPlacement(boolean[] panelPlacement) {
        this.panelPlacement = panelPlacement;
    }

    public boolean[] getPanelPlacement() {
        return panelPlacement;
    }

    public boolean isCellPresent(int i){
        if (i == 1 || i == 3){
            return panelPlacement[i] && panelPlacement[i-1] && panelPlacement[i+1];
        }
        return panelPlacement[i];
    }

    //for registering storage

    public boolean isTextCentered(){
        return this.center_text;
    }

    public void setCenterText(boolean center_text){
        this.center_text = center_text;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT textNBT = new ListNBT();
        ListNBT boolNBT = new ListNBT();
        ListNBT colorNBT = new ListNBT();
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
        for (int i=0;i<8;i++) {
            boolean b = panelPlacement[i];
            CompoundNBT nbt4 = new CompoundNBT();
            nbt4.putBoolean("bool", b);
            boolNBT.add(nbt4);
        }
        for (int i=0;i<3;i++){
            CompoundNBT nbt1 = new CompoundNBT();
            nbt1.putInt("bg_color",bg_color[i].getRGB());
            nbt1.putInt("lim_color",limit_color[i].getRGB());
            colorNBT.add(nbt1);
        }
        nbt.put("txts",textNBT);
        nbt.put("bool",boolNBT);
        nbt.put("color",colorNBT);
        nbt.putBoolean("center",center_text);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ListNBT textNBT = (ListNBT) nbt.get("txts");
        ListNBT boolNBT = (ListNBT) nbt.get("bool");
        ListNBT colorNBT = (ListNBT) nbt.get("color");
        for (int i=0;i<5;i++){
            assert textNBT != null;
            CompoundNBT nbt1= (CompoundNBT) textNBT.get(i);
            CompoundNBT nbt2 = nbt1.getCompound("beg");
            CompoundNBT nbt3 = nbt1.getCompound("end");
            texts[i] = Text.getTextFromNBT(nbt2);
            endTexts[i] = Text.getTextFromNBT(nbt3);
        }
        for (int i=0;i<8;i++){
            assert boolNBT != null;
            CompoundNBT nbt4 = (CompoundNBT) boolNBT.get(i);
            panelPlacement[i]=nbt4.getBoolean("bool");
        }
        for (int i=0;i<2;i++){
            assert colorNBT != null;
            CompoundNBT nbt5 = (CompoundNBT) colorNBT.get(i);
            bg_color[i] = new Color(nbt5.getInt("bg_color"),true);
            limit_color[i] = new Color(nbt5.getInt("lim_color"),true);
        }
        center_text = nbt.getBoolean("center");
    }


    //warning !!

    private void warn(String function, int ind){
        SignMod.LOGGER.warn("Bad usage of method "+function+" in Direction Storage. bad indices was given : "+ind);
    }


}
