package fr.mattmouss.signs.gui.screenutils;

import fr.mattmouss.signs.util.Functions;

import java.awt.*;

public class TextOption extends Option{
    Color color;
    int length;
    private TextOption(Color color,int length){
        this.color = color;
        this.length = length;
    }

    public static TextOption getDefaultOption(){
        return new TextOption(Color.WHITE,1);
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
}
