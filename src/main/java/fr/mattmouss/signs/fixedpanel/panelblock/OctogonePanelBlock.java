package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.ScreenType;

public class OctogonePanelBlock extends AbstractPanelBlock {
    public OctogonePanelBlock() {
        super("stop");
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.EDITING_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.OCTOGONE;
    }
}
