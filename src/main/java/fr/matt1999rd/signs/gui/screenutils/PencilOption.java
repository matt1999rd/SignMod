package fr.matt1999rd.signs.gui.screenutils;


import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.util.Text;

import java.awt.*;

public class PencilOption implements Option{
    Color color;
    int length;
    PencilMode mode;
    int selectedTextIndices;

    public PencilOption(Color color, int length, PencilMode mode){
        this.color = color;
        this.length = length;
        this.mode = mode;
        this.selectedTextIndices = Text.UNSELECTED_TEXT_ID;
    }

    public static PencilOption getDefaultOption(){
        return new PencilOption(Color.WHITE,1,PencilMode.WRITE);
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
            rColor = Functions.getRedValue(newColor);
            if (rColor>255)rColor=255;
            gColor = Functions.getGreenValue(newColor);
            if (gColor>255)gColor=255;
            bColor = Functions.getBlueValue(newColor);
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

    public void changePencilMode(PencilMode newMode) {
        this.mode = newMode;
    }

    public PencilMode getMode(){
        return mode;
    }

    public int getLength() {
        return length;
    }

    public int getColor() {
        return color.getRGB();
    }

    public void incrementLength(boolean increase) {
        if (increase){
            length++;
        }else {
            length--;
        }
    }

    public void selectText(int n){
        this.selectedTextIndices = n;
    }

    public int getTextIndices(){
        return selectedTextIndices;
    }

    public boolean isTextSelected(){
        return (selectedTextIndices != Text.UNSELECTED_TEXT_ID);
    }

    public void unselectText() {
        this.selectedTextIndices = Text.UNSELECTED_TEXT_ID;
    }
}
