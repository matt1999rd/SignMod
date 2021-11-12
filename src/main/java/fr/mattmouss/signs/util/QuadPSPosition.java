package fr.mattmouss.signs.util;


public class QuadPSPosition {
    private Vec2i position;
    private int lengthMax;
    private int maxText;
    public QuadPSPosition(int x1, int y1, int lengthMax, int maxTextNumber){
        position = new Vec2i(x1,y1);
        this.lengthMax = lengthMax;
        this.maxText = maxTextNumber;
    }

    public Vec2i getPosition() {
        return position;
    }

    public float getLengthMax(){
        return lengthMax;
    }

    public int getMaxText(){
        return maxText;
    }

}
