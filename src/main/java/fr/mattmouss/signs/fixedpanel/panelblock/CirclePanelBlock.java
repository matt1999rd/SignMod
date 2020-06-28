package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.ScreenType;

public class CirclePanelBlock extends AbstractPanelBlock {
    public CirclePanelBlock() {
        super("circle");
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.DRAWING_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.CIRCLE;
    }
}
