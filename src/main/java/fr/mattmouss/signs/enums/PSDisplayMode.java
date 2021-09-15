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
                textPositions.add(new QuadPSPosition(3.0F,6.0F,45.0F,2));
                textPositions.add(new QuadPSPosition(3.0F,24.0F,58.0F,4));
                break;
            case DIRECTION:
                textPositions.add(new QuadPSPosition(4.0F,3.0F,88.0F,5));
                break;
            case SCH_MUL_EXIT:
                textPositions.add(new QuadPSPosition(28.0F, 2.0F,39.0F,1));
                textPositions.add(new QuadPSPosition(3.0F,50.0F,39.0F,1));
                textPositions.add(new QuadPSPosition(54.0F,50.0F,39.0F,1));
                break;
            case SCH_EXIT:
                textPositions.add(new QuadPSPosition(7.0F,5.0F,39.0F,2));
                textPositions.add(new QuadPSPosition(51.0F,22.0F,39.0F,4));
                break;
        }
        return textPositions;
    }
}
