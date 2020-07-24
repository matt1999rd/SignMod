package fr.mattmouss.signs.capabilities;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.util.Text;

public interface ISignStorage {
    int getRGBPixel(int x, int y);
    void setPixel(int x,int y,int color);
    void setPixel(int x,int y,int color,int length);
    void addText(Text t);
    Text[] getTexts();
    int[] getAllPixel();
    void setAllPixel(int[] pixels);


}
