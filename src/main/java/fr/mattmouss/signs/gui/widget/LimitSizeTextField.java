package fr.mattmouss.signs.gui.widget;

import fr.mattmouss.signs.gui.screenutils.TextOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class LimitSizeTextField extends TextFieldWidget {
    public LimitSizeTextField(Minecraft mc, int relX, int relY) {
        super(mc.fontRenderer, relX+30, relY+118, 90, 12, " ");
    }

    public void updateColor(TextOption option){
        int color = option.getColor();
        this.setTextColor(color);
    }


}
