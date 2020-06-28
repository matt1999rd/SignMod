package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.ScreenType;

public class RectanglePanelBlock extends AbstractPanelBlock {
    public RectanglePanelBlock() {
        super("rectangle");
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.EDITING_AND_COLOURING_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.RECTANGLE;
    }


}
