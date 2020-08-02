package fr.mattmouss.signs.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class LimitSizeTextField extends TextFieldWidget {
    public LimitSizeTextField(Minecraft mc, int relX, int relY) {
        super(mc.fontRenderer, relX+30, relY+118, 90, 12, " ");
    }


}
