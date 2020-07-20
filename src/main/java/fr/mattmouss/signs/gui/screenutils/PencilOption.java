package fr.mattmouss.signs.gui.screenutils;

import fr.mattmouss.signs.util.Functions;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class PencilOption {
    Color color;
    int length;
    PencilMode mode;

    private PencilOption(Color color,int length,PencilMode mode){
        this.color = color;
        this.length = length;
        this.mode = mode;
    }

    public static PencilOption getDefaultOption(){
        return new PencilOption(Color.WHITE,1,PencilMode.WRITE);
    }

    public void setColor(int newColor,ColorType type) {
        int rColor = color.getRed();
        int gColor = color.getGreen();
        int bColor = color.getBlue();
        if (newColor>255){
            newColor = 255;
        }
        switch (type){
            case RED:
                rColor = newColor;
                break;
            case BLUE:
                bColor =  newColor;
                break;
            case GREEN:
                gColor = newColor;
                break;
            default:
                break;
        }
        this.color = new Color(rColor,gColor,bColor,255);
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
}
