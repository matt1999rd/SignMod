package fr.mattmouss.signs.util;

import net.minecraft.util.math.Vec2f;

public class QuadPSPosition {
    private Vec2f position;
    private float lengthMax;
    private int maxText;
    public QuadPSPosition(float x1, float y1, float lengthMax, int maxTextNumber){
        position = new Vec2f(x1,y1);
        this.lengthMax = lengthMax;
        this.maxText = maxTextNumber;
    }

    public Vec2f getPosition() {
        return position;
    }

    public float getLengthMax(){
        return lengthMax;
    }

    public int getMaxText(){
        return maxText;
    }
}
