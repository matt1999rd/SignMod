package fr.mattmouss.signs.enums;

import net.minecraft.util.IStringSerializable;

public enum PSDisplayMode implements IStringSerializable {
    EXIT(0,"exit"),
    DIRECTION(1,"direction"),
    SCH_MUL_EXIT(2,"scheme_round_about"),
    SCH_EXIT(3,"scheme_exit");

    private final int meta;
    private final String name;
    PSDisplayMode(int mode,String name){
        this.meta = mode;
        this.name = name;
    }

    public static PSDisplayMode byIndex(byte meta){
        PSDisplayMode[] modes = PSDisplayMode.values();
        if (meta>3 || meta<0){
            return null;
        }
        return modes[meta];
    }

    public byte getMeta(){
        return (byte) meta;
    }

    public boolean is2by2(){
        return this == EXIT;
    }

    @Override
    public String getName() {
        return name;
    }
}
