package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.ScreenType;

public class LetWayPanelBlock extends AbstractPanelBlock {
    public LetWayPanelBlock() {
        super("let_way");
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.EDITING_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.UPSIDE_TRIANGLE;
    }
}
