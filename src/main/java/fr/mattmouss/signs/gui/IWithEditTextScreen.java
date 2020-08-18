package fr.mattmouss.signs.gui;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.util.Text;
import net.minecraft.client.gui.screen.Screen;

public interface IWithEditTextScreen {
    Form getForm();
    void addOrEditText(Text t);
    Screen getScreen();
}
