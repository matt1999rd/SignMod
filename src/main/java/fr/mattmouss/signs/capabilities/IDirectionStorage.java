package fr.mattmouss.signs.capabilities;

import fr.mattmouss.signs.util.Text;

public interface IDirectionStorage {
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
    Text getText(int ind,boolean isEnd);
    void setText(int ind,Text newText,boolean isEnd);
    boolean[] getPanelPlacement();
    Text[] getAllTexts();
    void setAllTexts(Text[] allTexts);
    void setPanelPlacement(boolean[] panelPlacement);
}
