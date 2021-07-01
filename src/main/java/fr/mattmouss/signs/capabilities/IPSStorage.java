package fr.mattmouss.signs.capabilities;

import fr.mattmouss.signs.enums.PSDisplayMode;
import fr.mattmouss.signs.enums.PSPosition;

import java.awt.*;

public interface IPSStorage extends ITextStorage {
    void setInternVariable(PSPosition position, PSDisplayMode mode);
    PSPosition getPosition();
    PSDisplayMode getDisplayMode();
    void setBackgroundColor(int color);
    Color getBackgroundColor();
    void setForegroundColor(int color);
    Color getForegroundColor();

}
