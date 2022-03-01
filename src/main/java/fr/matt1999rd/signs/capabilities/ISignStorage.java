package fr.matt1999rd.signs.capabilities;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.util.Text;

import java.util.List;

public interface ISignStorage {
    int getRGBPixel(int x, int y);
    void setPixel(int x,int y,int color);
    void setPixel(int x,int y,int color,int length);
    void addText(Text t);
    List<Text> getTexts();
    int[] getAllPixel();
    void setAllPixel(int[] pixels);
    void setText(Text t, int ind);
    void setTextPosition(int ind, int newX, int newY, Form form);
    void delText(int ind);
}
