package fr.matt1999rd.signs.gui.screenutils;

import fr.matt1999rd.signs.util.Functions;

import java.awt.*;

public class ColorOption implements Option {

    Color color;

    public ColorOption(Color initialColor){
        color = initialColor;
    }
    @Override
    public void setColor(int newColor, ColorType type) {
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

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getColor(ColorType type) {
        return switch (type) {
            case RED -> color.getRed();
            case BLUE -> color.getBlue();
            case GREEN -> color.getGreen();
        };
    }

    public int getColor(){
        return color.getRGB();
    }
}
