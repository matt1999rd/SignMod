package fr.mattmouss.signs.capabilities;

import fr.mattmouss.signs.util.Text;

public interface IDirectionStorage {
    //test of display and modification
    boolean is12connected();
    void add12connection();
    void remove12connection();
    boolean is23connected();
    void add23connection();
    void remove23connection();
    boolean hasPanel(int ind);
    void addPanel(int ind);
    void removePanel(int ind);
    boolean isArrowRight(int ind);
    void changeArrowSide(int ind);
    //color of background and limit
    int getBgColor(int ind);
    void setBgColor(int color,int ind);
    int getLimColor(int ind);
    void setLimColor(int color,int ind);
    //text treatment
    Text getText(int ind,boolean isEnd);
    void setText(int ind,Text newText,boolean isEnd);
    //center text
    boolean isTextCentered();
    void setCenterText(boolean center_text);

    //for capability
    boolean[] getPanelPlacement();
    void setPanelPlacement(boolean[] panelPlacement);
    Text[] getAllTexts();
    void setAllTexts(Text[] allTexts);
}
