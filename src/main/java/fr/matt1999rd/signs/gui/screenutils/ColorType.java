package fr.matt1999rd.signs.gui.screenutils;

public enum ColorType {
    RED,
    GREEN,
    BLUE;

    public static ColorType byIndex(int index){
        if (index == 0){
            return RED;
        }else if (index == 1){
            return GREEN;
        }else {
            return BLUE;
        }
    }
}
