package fr.mattmouss.signs.capabilities;

import fr.mattmouss.signs.enums.PSDisplayMode;
import fr.mattmouss.signs.enums.PSPosition;

public interface IPSStorage extends ITextStorage {
    void setInternVariable(PSPosition position, PSDisplayMode mode);
    PSPosition getPosition();
    PSDisplayMode getDisplayMode();
    void setBackgroundColor(int color);
    int getBackgroundColor();
}
