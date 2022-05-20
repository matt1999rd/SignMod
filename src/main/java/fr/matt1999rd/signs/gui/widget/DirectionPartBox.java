package fr.matt1999rd.signs.gui.widget;

import fr.matt1999rd.signs.gui.DirectionScreen;
import fr.matt1999rd.signs.enums.Form;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.ITextComponent;

public class DirectionPartBox extends CheckboxButton {
    DirectionScreen screen;
    int ind;
    public DirectionPartBox(int ind, DirectionScreen screen,int relX,int relY,boolean initialPlace) {
        super(relX+189, relY+16+26*ind,20,20, ITextComponent.nullToEmpty(""), initialPlace);
        this.screen = screen;
        this.ind = ind;
    }

    @Override
    public void onPress() {
        super.onPress();
        this.screen.updateBoolean(ind);
        if (screen.getForm() == Form.ARROW)this.screen.updateCursorAuthorisation(ind,selected());
    }
}
