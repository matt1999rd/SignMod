package fr.matt1999rd.signs.gui.screenutils;

public enum PencilMode {
    WRITE(0),
    ERASE(1),
    FILL(2),
    PICK(3),
    SELECT(4);

    final int meta;

    PencilMode(int meta){
        this.meta = meta;
    }

    public static PencilMode getPencilMode(int meta){
        if (meta<0 || meta>4)return null;
        return PencilMode.values()[meta];
    }

    public int getMeta() {
        return meta;
    }

    public boolean enableSlider(){
        return this == WRITE || this == FILL;
    }
}
