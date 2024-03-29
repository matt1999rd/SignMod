package fr.matt1999rd.signs.enums;

//this discriminates different type of screen to be open from a onBlockActivated function
public enum ScreenType {
    //drawing screen is a screen when you can modify pixels of a picture
    DRAWING_SCREEN(0),
    //editing screen is a screen when you can only add text (use for Octagon and Upside Triangle panel)
    EDITING_SCREEN(1),
    //direction screen is a screen for direction indicating panel which has features of adding and removing part of panel
    DIRECTION_SCREEN(2),
    //a more advanced screen than previous one which can add text and modify colour of the text and colour of background
    //and with others features -> used only for the plain square panel
    PLAIN_SQUARE_SCREEN(3);
    final int meta;

    ScreenType(int meta){
        this.meta = meta;
    }
    public int getMeta() {
        return meta;
    }
    public static ScreenType getType(int meta){
        if (meta>3 || meta<0){
            return null;
        }else {
            ScreenType[] types = ScreenType.values();
            return types[meta];
        }
    }
}
