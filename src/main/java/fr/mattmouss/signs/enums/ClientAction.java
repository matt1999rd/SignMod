package fr.mattmouss.signs.enums;

import fr.mattmouss.signs.gui.screenutils.PencilMode;

public enum ClientAction {
    SET_PIXEL(0),
    FILL_PIXEL(1),
    ERASE_PIXEL(2),
    SET_BG(3),
    MOVE_TEXT(4);

    int meta;
    ClientAction(int meta){
        this.meta = meta;
    }

    public int getMeta() {
        return meta;
    }

    public static ClientAction getAction(int meta){
        if (meta<0 || meta>4){
            return null;
        }else {
            return ClientAction.values()[meta];
        }
    }

    public static ClientAction getActionFromMode(PencilMode mode){
        switch (mode){
            case WRITE:
                return SET_PIXEL;
            case ERASE:
                return ERASE_PIXEL;
            case FILL:
                return FILL_PIXEL;
            case SELECT:
                return MOVE_TEXT;
            default:
                return null;
        }
    }
}
