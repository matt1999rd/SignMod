package fr.mattmouss.signs.util;

import fr.mattmouss.signs.SignMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Text implements INBTSerializable<CompoundNBT> {
    private int x,y;
    private String content;
    public Text(int x,int y,String txt){
        this.x = x;
        this.y = y;
        this.content = txt;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public String getText(){
        return content;
    }

    public void set(int x,int y,String newText) {
        if (Functions.isValidCoordinate(x,y)){
            this.x = x;
            this.y = y;
            this.content = newText;
        }
        SignMod.LOGGER.warn("Set is unvalid : x : "+this.x+" / y : "+this.y+"\nSkip unvalid settlement !!");
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT txtNBT = new CompoundNBT();
        txtNBT.putInt("x_coor",getX());
        txtNBT.putInt("y_coor",getY());
        txtNBT.putString("text_content",getText());
        return txtNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        int x = nbt.getInt("x_coor");
        int y = nbt.getInt("y_coor");
        String content = nbt.getString("text_content");
        set(x,y,content);
    }
}
