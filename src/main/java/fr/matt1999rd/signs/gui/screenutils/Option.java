package fr.matt1999rd.signs.gui.screenutils;

import java.awt.*;

public  interface Option {
    void setColor(int color,ColorType type);
    void setColor(Color color);
    int getColor(ColorType type);
    int getColor();

}
