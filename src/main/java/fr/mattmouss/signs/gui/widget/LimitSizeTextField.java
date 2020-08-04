package fr.mattmouss.signs.gui.widget;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.screenutils.TextOption;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Letter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class LimitSizeTextField extends TextFieldWidget {
    int x,y;
    Form form;
    public LimitSizeTextField(Minecraft mc, int relX, int relY,int x,int y,Form form) {
        super(mc.fontRenderer, relX+30, relY+118, 90, 12, " ");
        this.x = x ;
        this.y = y ;
        this.form = form ;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void updateColor(TextOption option){
        int color = option.getColor();
        this.setTextColor(color);
    }

    @Override
    public boolean charTyped(char c, int p_charTyped_2_) {
        String text = this.getText();
        if (Letter.isIn(c)){
            String newText = text+c;
            int length = Functions.getLength(newText);
            int height = 7;
            if (form.rectangleIsIn(x,x+length,y,y+height)){
                return super.charTyped(c,p_charTyped_2_);
            }
        }
        return false;
    }
}
