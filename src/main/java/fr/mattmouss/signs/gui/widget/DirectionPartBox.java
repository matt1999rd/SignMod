package fr.mattmouss.signs.gui.widget;

import fr.mattmouss.signs.gui.DirectionScreen;
import net.minecraft.client.gui.widget.button.CheckboxButton;

public class DirectionPartBox extends CheckboxButton {
    DirectionScreen screen;
    int ind;
    public DirectionPartBox(int ind, DirectionScreen screen,int relX,int relY,boolean initialPlace) {
        super(relX+30, relY+30+20*ind,20,20, "", initialPlace);
        this.screen = screen;
        this.ind = ind;
    }

    @Override
    public void onPress() {
        super.onPress();
        this.screen.updateBoolean(ind);
    }
}
