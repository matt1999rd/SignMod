package fr.matt1999rd.signs.gui.widget;

import fr.matt1999rd.signs.gui.AddTextScreen;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.gui.screenutils.ColorType;
import fr.matt1999rd.signs.gui.screenutils.Option;
import fr.matt1999rd.signs.util.Letter;
import fr.matt1999rd.signs.util.Text;
import net.minecraft.client.Minecraft;
import static fr.matt1999rd.signs.util.Functions.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.awt.*;

public class LimitSizeTextField extends TextFieldWidget implements Option {
    int xText,yText;
    Form form;
    int scale;
    Color color;
    //boolean isEnd,isTextCentered;
    int limitLength;

    public LimitSizeTextField(Minecraft mc, int relX, int relY, Form form,@Nullable Text oldText) {
        super(mc.font, relX, relY, 90, 12, ITextComponent.nullToEmpty(" "));
        int xText= (oldText != null) ? (int)oldText.getX() : form.getXBeginning(7);
        int yText= (oldText != null) ? (int)oldText.getY() : form.getYBeginning(7);
        int scale = (oldText != null) ? oldText.getScale() : 1;
        String text = (oldText != null) ? oldText.getText() : "";
        color = (oldText != null) ? new Color(oldText.getColor(),true) : Color.WHITE;
        this.setValue(text);
        this.xText = xText;
        this.yText = yText ;
        this.form = form ;
        this.scale = scale;
        this.setCanLoseFocus(false);
        this.setFocus(true);
        onUpdate(true);
    }


    public void setLengthLimit(int maxLength){
        this.limitLength = maxLength;
    }

    public int getX() {
        return xText;
    }

    public int getY() {
        return yText;
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
            xText = 5+MathHelper.ceil(3.5f*scale-0.5);
        }
    }


    @Override
    public boolean charTyped(char c, int p_charTyped_2_) {
        String text = this.getValue();
        if (Letter.isIn(c) || c == ' '){
            String newText = text+c;
            int length = getLength(newText)*scale;
            int height = 7*scale;
            if (form.hasLengthPredefinedLimit()) {
                if (length <= limitLength) {
                    return super.charTyped(c, p_charTyped_2_);
                }
            } else if (form.rectangleIsIn(x,x+length-1,y,y+height-1)){
                return super.charTyped(c,p_charTyped_2_);
            }
        }
        return false;
    }

    @Override
    public void insertText(String textToWrite) {
        super.insertText(textToWrite);
        onUpdate(false);
    }

    @Override
    public void deleteChars(int num) {
        super.deleteChars(num);
        onUpdate(false);
    }

    @Override
    public void deleteWords(int num) {
        super.deleteWords(num);
        onUpdate(false);
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

    @Override
    public int getColor() {
        return color.getRGB();
    }

    public void onUpdate(boolean init){
        if (!init)updatePlusButton();
        updateTextPosition(init);
    }

    public void updatePlusButton() {
        int length = getLength(this.getValue());
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AddTextScreen){
            int upperLength = (scale+1)*length;
            int upperHeight = (scale+1)*7;
            AddTextScreen addTextScreen = (AddTextScreen)screen;
            if (form.hasLengthPredefinedLimit()){
                if (scale == 3 || upperLength> limitLength){
                    addTextScreen.disablePlusButton();
                } else {
                    addTextScreen.enablePlusButton();
                }
            } else if (!form.rectangleIsIn(xText,xText+length*scale-1,yText,yText+7*scale-1) && form != Form.PLAIN_SQUARE){
                this.setValue("");
            }else if (!form.rectangleIsIn(xText,xText+upperLength-1,yText,yText+upperHeight-1)){
                addTextScreen.disablePlusButton();
            }else {
                addTextScreen.enablePlusButton();
            }
        }
    }

    public void updateTextPosition(boolean init){
        if (form.isNotForEditing())return;
        int L = getLength(this.getValue()) * scale;
        if (form == Form.OCTAGON) {
            int H = 7*scale;
            xText = 64 - L / 2;
            yText = 64 - H / 2;
        }else {
            xText = 64 - L / 2;
            if (init) yText = Form.offsetLetWay;
        }
    }
}
