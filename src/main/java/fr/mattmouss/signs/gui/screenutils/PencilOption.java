package fr.mattmouss.signs.gui.screenutils;

import fr.mattmouss.signs.util.Functions;
import net.minecraft.util.math.MathHelper;

public class PencilOption {
    int color;
    int length;
    PencilMode mode;

    private PencilOption(int color,int length,PencilMode mode){
        this.color = color;
        this.length = length;
        this.mode = mode;
    }

    public static PencilOption getDefaultOption(){
        int white = MathHelper.rgb(255,255,255);
        return new PencilOption(white,1,PencilMode.WRITE);
    }

    public void setColor(int newColor,ColorType type) {
        int rColor = Functions.getRedValue(color);
        int gColor = Functions.getGreenValue(color);
        int bColor = Functions.getBlueValue(color);
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
        this.color = MathHelper.rgb(rColor,gColor,bColor);
    }

    public int getColor(ColorType type){
        switch (type){
            case RED:
                return Functions.getRedValue(this.color);
            case BLUE:
                return Functions.getBlueValue(this.color);
            case GREEN:
                return Functions.getGreenValue(this.color);
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
        return color;
    }
}
