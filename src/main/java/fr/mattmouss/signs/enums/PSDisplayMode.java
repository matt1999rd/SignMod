package fr.mattmouss.signs.enums;

public enum PSDisplayMode {
    EXIT(0),
    DIRECTION(1),
    SCH_EXIT(2),
    SCH_MUL_EXIT(3);

    private final int meta;
    PSDisplayMode(int mode){
        this.meta = mode;
    }

    public static PSDisplayMode byIndex(int meta){
        PSDisplayMode[] modes = PSDisplayMode.values();
        if (meta>3 || meta<0){
            return null;
        }
        return modes[meta];
    }
}
