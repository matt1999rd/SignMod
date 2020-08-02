package fr.mattmouss.signs.util;

import fr.mattmouss.signs.SignMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.INBTSerializable;
import org.lwjgl.system.CallbackI;

import java.awt.*;

public class Text implements INBTSerializable<CompoundNBT> {
    private int x,y;
    private String content;
    private Color color;
    public Text(int x,int y,String txt,Color color){
        this.x = x;
        this.y = y;
        this.content = txt;
        this.color = color;
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

    public int getColor(){ return this.color.getRGB(); }

    public void set(int x,int y,String newText,int color) {
        if (Functions.isValidCoordinate(x,y)){
            this.x = x;
            this.y = y;
            this.content = newText;
            this.color = new Color(color,true);
        }
        SignMod.LOGGER.warn("Set is unvalid : x : "+this.x+" / y : "+this.y+"\nSkip unvalid settlement !!");
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT txtNBT = new CompoundNBT();
        txtNBT.putInt("x_coor",getX());
        txtNBT.putInt("y_coor",getY());
        txtNBT.putString("text_content",getText());
        txtNBT.putInt("color",color.getRGB());
        return txtNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        int x = nbt.getInt("x_coor");
        int y = nbt.getInt("y_coor");
        String content = nbt.getString("text_content");
        int color =nbt.getInt("color");
        set(x,y,content,color);
    }

    public void writeText(PacketBuffer buf){
        byte[] bytes = new byte[2];
        bytes[0] = (byte) x;
        bytes[1] = (byte) y;
        buf.writeByteArray(bytes);
        buf.writeInt(color.getRGB());
        buf.writeString(content);
    }

    public static Text readText(PacketBuffer buf){
        byte[] pos = buf.readByteArray();
        int color = buf.readInt();
        String content = buf.readString();
        if (Functions.isValidCoordinate(pos[0],pos[1])){
            return new Text(pos[0],pos[1],content,new Color(color,true));
        }
        throw new IllegalArgumentException("position are badly transmited : get text outside bound");
    }
}
