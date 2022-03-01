package fr.matt1999rd.signs.capabilities;

import fr.matt1999rd.signs.enums.PSDisplayMode;
import fr.matt1999rd.signs.enums.PSPosition;
import fr.matt1999rd.signs.util.Text;

import java.awt.*;

public interface IPSStorage {
    void setInternVariable(PSPosition position, PSDisplayMode mode);
    PSPosition getPosition();
    PSDisplayMode getDisplayMode();
    void setBackgroundColor(int color);
    Color getBackgroundColor();
    void setForegroundColor(int color);
    Color getForegroundColor();
    int getArrowId();
    void setArrowId(int arrowId);
    Text[] getTexts();
    void setText(Text t, int ind);
    Text getText(int ind);
}
