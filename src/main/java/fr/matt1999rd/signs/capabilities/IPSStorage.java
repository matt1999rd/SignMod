package fr.matt1999rd.signs.capabilities;

import fr.matt1999rd.signs.enums.PSDisplayMode;
import fr.matt1999rd.signs.enums.PSPosition;
import fr.matt1999rd.signs.util.Text;

import java.awt.*;

public interface IPSStorage {
    void setInternVariable(PSPosition position, PSDisplayMode mode);
    PSPosition getPosition();
    void setPosition(PSPosition position);
    PSDisplayMode getDisplayMode();
    void setDisplayMode(PSDisplayMode mode);
    Color getBackgroundColor();
    void setBackgroundColor(int color);
    Color getForegroundColor();
    void setForegroundColor(int color);
    int getArrowId();
    void setArrowId(int arrowId);
    Text[] getTexts();
    Text getText(int ind);
    void setText(Text t, int ind);
}
