package fr.mattmouss.signs.enums;

import net.minecraft.util.IStringSerializable;

public enum PSDisplayMode implements IStringSerializable {
    EXIT(0,"exit"),
    DIRECTION(1,"direction"),
    SCH_EXIT(2,"scheme_exit"),
    SCH_MUL_EXIT(3,"scheme_round_about");

    private final int meta;
    private final String name;
    PSDisplayMode(int mode,String name){
        this.meta = mode;
        this.name = name;
    }

    public static PSDisplayMode byIndex(int meta){
        PSDisplayMode[] modes = PSDisplayMode.values();
        if (meta>3 || meta<0){
            return null;
        }
        return modes[meta];
    }

    public boolean is2by2(){
        return this == EXIT;
    }

    @Override
    public String getName() {
        return name;
    }
}
