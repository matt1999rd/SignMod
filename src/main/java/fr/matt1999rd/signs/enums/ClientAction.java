package fr.matt1999rd.signs.enums;

import fr.matt1999rd.signs.gui.screenutils.PencilMode;

public enum ClientAction {
    SET_PIXEL(0),
    FILL_PIXEL(1),
    ERASE_PIXEL(2),
    SET_BG(3),
    MOVE_TEXT(4);

    final int meta;
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
        return switch (mode) {
            case WRITE -> SET_PIXEL;
            case ERASE -> ERASE_PIXEL;
            case FILL -> FILL_PIXEL;
            case SELECT -> MOVE_TEXT;
            default -> null;
        };
    }
}
