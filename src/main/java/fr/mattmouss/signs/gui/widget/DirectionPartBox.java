package fr.mattmouss.signs.gui.widget;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.DirectionScreen;
import net.minecraft.client.gui.widget.button.CheckboxButton;

public class DirectionPartBox extends CheckboxButton {
    DirectionScreen screen;
    int ind;
    public DirectionPartBox(int ind, DirectionScreen screen,int relX,int relY,boolean initialPlace) {
        super(relX+189, relY+16+26*ind,20,20, "", initialPlace);
        this.screen = screen;
        this.ind = ind;
    }

    @Override
    public void onPress() {
        super.onPress();
        this.screen.updateBoolean(ind);
        if (screen.getForm() == Form.ARROW)this.screen.updateCursorAuthorisation(ind);
    }
}
