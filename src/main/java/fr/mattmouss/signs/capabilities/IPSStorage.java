package fr.mattmouss.signs.capabilities;

import fr.mattmouss.signs.enums.PSDisplayMode;
import fr.mattmouss.signs.enums.PSPosition;
import fr.mattmouss.signs.util.Text;

import java.awt.*;
import java.util.List;

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
