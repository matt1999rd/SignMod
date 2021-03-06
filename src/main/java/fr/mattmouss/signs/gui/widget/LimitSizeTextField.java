package fr.mattmouss.signs.gui.widget;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.AddTextScreen;
import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.screenutils.Option;
import fr.mattmouss.signs.util.Letter;
import fr.mattmouss.signs.util.Text;
import net.minecraft.client.Minecraft;
import static fr.mattmouss.signs.util.Functions.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.awt.*;

public class LimitSizeTextField extends TextFieldWidget implements Option {
    int x,y;
    Form form;
    int scale;
    Color color;
    boolean isEnd,isTextCentered;
    public LimitSizeTextField(Minecraft mc, int relX, int relY, Form form,@Nullable Text oldText) {
        super(mc.fontRenderer, relX+30, relY+118, 90, 12, " ");
        int xText= (oldText != null) ? (int)oldText.getX() : form.getXBegining(7);
        int yText= (oldText != null) ? (int)oldText.getY() : form.getYBegining(7);
        int scale = (oldText != null) ? oldText.getScale() : 1;
        String text = (oldText != null) ? oldText.getText() : "";
        color = (oldText != null) ? new Color(oldText.getColor(),true) : Color.WHITE;
        this.setText(text);
        this.x = xText;
        this.y = yText ;
        this.form = form ;
        this.scale = scale;
    }

    public void defineVariableForDirection(boolean isEnd,boolean isTextCentered){
        this.isEnd = isEnd;
        this.isTextCentered = isTextCentered;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getScale() {
        return scale;
    }

    public void incrementScale(boolean increase) {
        if (increase){
            scale++;
        }else {
            scale--;
        }
        if (form == Form.UPSIDE_TRIANGLE){
            x= 5+MathHelper.ceil(3.5f*scale-0.5);
        }
    }


    @Override
    public boolean charTyped(char c, int p_charTyped_2_) {
        String text = this.getText();
        if (Letter.isIn(c) || c == ' '){
            String newText = text+c;
            int length = getLength(newText)*scale;
            int height = 7*scale;
            if (form.isForDirection()){
                int lenlimit;
                if (isTextCentered){
                    lenlimit = panelLength-2*st_gap;
                }else {
                    lenlimit = isEnd ? endTextLength : begTextLength;
                }
                if (length<=lenlimit){
                    return super.charTyped(c,p_charTyped_2_);
                }
            }
            else if (form.rectangleIsIn(x,x+length-1,y,y+height-1)){
                return super.charTyped(c,p_charTyped_2_);
            }
        }
        return false;
    }

    @Override
    public void writeText(String textToWrite) {
        super.writeText(textToWrite);
        updatePlusButton();
    }

    @Override
    public void deleteFromCursor(int num) {
        super.deleteFromCursor(num);
        updatePlusButton();
    }

    @Override
    public void deleteWords(int num) {
        super.deleteWords(num);
        updatePlusButton();
    }

    public void updatePlusButton() {
        int length = getLength(this.getText());
        Screen screen = Minecraft.getInstance().currentScreen;
        if (screen instanceof AddTextScreen){
            int upperLength = (scale+1)*length;
            int upperHeight = (scale+1)*7;
            AddTextScreen addTextScreen = (AddTextScreen)screen;
            if (form.isForDirection()){
                int limlength = (isTextCentered) ? panelLength-2*st_gap : (isEnd) ? endTextLength : begTextLength;
                if (scale == 3 || upperLength>limlength){
                    addTextScreen.disablePlusButton();
                } else {
                    addTextScreen.enablePlusButton();
                }
            } else if (!form.rectangleIsIn(x,x+length*scale-1,y,y+7*scale-1)){
                this.setText("");
            }else if (!form.rectangleIsIn(x,x+upperLength-1,y,y+upperHeight-1)){
                addTextScreen.disablePlusButton();
            }else {
                addTextScreen.enablePlusButton();
            }
        }
    }

    public void setColor(int newColor,ColorType type) {
        int rColor = color.getRed();
        int gColor = color.getGreen();
        int bColor = color.getBlue();
        if (type != null) {
            if (newColor > 255) {
                newColor = 255;
            }
            switch (type) {
                case RED:
                    rColor = newColor;
                    break;
                case BLUE:
                    bColor = newColor;
                    break;
                case GREEN:
                    gColor = newColor;
                    break;
                default:
                    break;
            }
        }else {
            rColor = getRedValue(newColor);
            if (rColor>255)rColor=255;
            gColor = getGreenValue(newColor);
            if (gColor>255)gColor=255;
            bColor = getBlueValue(newColor);
            if (bColor>255)bColor=255;
        }
        this.color = new Color(rColor, gColor, bColor, 255);
    }

    public int getColor(ColorType type){
        if (type == null){
            return color.getRGB();
        }
        switch (type){
            case RED:
                return color.getRed();
            case BLUE:
                return color.getBlue();
            case GREEN:
                return color.getGreen();
            default:
                return -1;
        }
    }
}
