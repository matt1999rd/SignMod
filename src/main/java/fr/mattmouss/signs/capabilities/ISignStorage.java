package fr.mattmouss.signs.capabilities;

import fr.mattmouss.signs.util.Text;

public interface ISignStorage {
    void setPixel(int x,int y,int rColor,int gColor,int bColor);
    int getRGBPixel(int x,int y);
    void setPixel(int x,int y,int color);
    void addText(Text t);
    Text[] getTexts();
    int[] getAllPixel();
    void setAllPixel(int[] pixels);

}
