package fr.mattmouss.signs.enums;

import fr.mattmouss.signs.util.TextPSPosition;
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

    public List<TextPSPosition> getTextPosition() {
        List<TextPSPosition> textPositions = new ArrayList<>();
        switch (this){
            case EXIT:
                textPositions.add(new TextPSPosition(1.5F,1.5F,22.5F,2));
                textPositions.add(new TextPSPosition(1.5F,11.5F,29.0F,4));
                break;
            case DIRECTION:
                textPositions.add(new TextPSPosition(2.0F,1.5F,44.0F,5));
                break;
            case SCH_MUL_EXIT:
                textPositions.add(new TextPSPosition(1.5F, 1.5F,19.5F,3));
                textPositions.add(new TextPSPosition(1.5F,22.0F,19.5F,2));
                textPositions.add(new TextPSPosition(27.5F,3.5F,19.5F,3));
                break;
            case SCH_EXIT:
                textPositions.add(new TextPSPosition(4.5F,1.5F,19.0F,2));
                textPositions.add(new TextPSPosition(26.0F,11.5F,19.0F,3));
                break;
        }
        return textPositions;
    }
}
