package fr.matt1999rd.signs.gui;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.util.Text;
import net.minecraft.client.gui.screen.Screen;

public interface IWithEditTextScreen {
    Form getForm();
    void addOrEditText(Text t);
    Screen getScreen();
}
