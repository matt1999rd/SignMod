package fr.matt1999rd.signs.util;


public class QuadPSPosition {
    private Vector2i position;
    private int lengthMax;
    private int maxText;
    public QuadPSPosition(int x1, int y1, int lengthMax, int maxTextNumber){
        position = new Vector2i(x1,y1);
        this.lengthMax = lengthMax;
        this.maxText = maxTextNumber;
    }

    public Vector2i getPosition() {
        return position;
    }

    public float getLengthMax(){
        return lengthMax;
    }

    public int getMaxText(){
        return maxText;
    }

}
