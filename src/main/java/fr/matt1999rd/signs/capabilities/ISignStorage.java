package fr.matt1999rd.signs.capabilities;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.util.Text;

import java.util.List;

public interface ISignStorage {
    int getRGBPixel(int x, int y);
    void setPixel(int x,int y,int color);
    //client action function
    // SET_PIXEL and ERASE_PIXEL
    void setPixel(int x,int y,int color,int length);
    // FILL_PIXEL
    void fill(int x,int y,int color);
    // SET_BG
    void setBackGround(int color);
    // MOVE_TEXT
    void setTextPosition(int ind, int newX, int newY, Form form);
    // MAKE_LINE
    void makeLine(int x1,int y1,int x2,int y2,int length,int color);
    // function for text
    void addText(Text t);
    List<Text> getTexts();

    void setText(Text t, int ind);

    void delText(int ind);
}
