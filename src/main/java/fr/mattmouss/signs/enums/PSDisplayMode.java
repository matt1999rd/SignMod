package fr.mattmouss.signs.enums;

import fr.mattmouss.signs.util.QuadPSPosition;
import net.minecraft.util.IStringSerializable;

import java.util.ArrayList;
import java.util.List;

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

    public List<QuadPSPosition> getTextPosition() {
        List<QuadPSPosition> textPositions = new ArrayList<>();
        switch (this){
            case EXIT:
                textPositions.add(new QuadPSPosition(8.0F,21.0F,22.5F,2));
                textPositions.add(new QuadPSPosition(1.5F,3.0F,29.0F,4));
                break;
            case DIRECTION:
                textPositions.add(new QuadPSPosition(2.0F,9.0F,44.0F,5));
                break;
            case SCH_MUL_EXIT:
                textPositions.add(new QuadPSPosition(14.5F, 27.5F,19.5F,1));
                textPositions.add(new QuadPSPosition(27.0F,3.5F,19.5F,1));
                textPositions.add(new QuadPSPosition(1.5F,3.5F,19.5F,1));
                break;
            case SCH_EXIT:
                textPositions.add(new QuadPSPosition(25.0F,21.5F,19.5F,2));
                textPositions.add(new QuadPSPosition(3.0F,4.0F,19.5F,4));
                break;
        }
        return textPositions;
    }
}
