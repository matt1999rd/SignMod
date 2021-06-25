package fr.mattmouss.signs.enums;

//this discrimintate different type of screen to be open from a onBlockActivated function
public enum ScreenType {
    //drawing screen is a screen when you can modify pixels of a picture
    DRAWING_SCREEN(0),
    //editing screen is a screen when you can only add text (use for Octogone and Upside Triangle panel)
    EDITING_SCREEN(1),
    //direction screen is a screen for direction indicating panel which has feature of adding and removing part of panel
    DIRECTION_SCREEN(2),
    //a more advanced screen than previous one which can add text and modify colour of the text and colour of background
    //and with others features
    EDITING_AND_COLOURING_SCREEN(3);
    int meta;

    ScreenType(int meta){
        this.meta = meta;
    }
    public int getMeta() {
        return meta;
    }
    public static ScreenType getType(int meta){
        if (meta>2 || meta<0){
            return null;
        }else {
            ScreenType[] types = ScreenType.values();
            return types[meta];
        }
    }
}
