package fr.mattmouss.signs.util;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.awt.*;

public class Text {
    private int x,y;
    private int scale;
    private String content;
    private Color color;
    private final ResourceLocation TEXT = new ResourceLocation(SignMod.MODID,"textures/gui/letter.png");
    public Text(int x,int y,String txt,Color color,int scale){
        this.x = x;
        this.y = y;
        this.content = txt;
        this.color = color;
        this.scale = scale;
    }

    public static Text getDefaultText(){
        return new Text(0,0,"",Color.WHITE,1);
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

    public int getLength(){
        return Functions.getLength(this.content)* scale;
    }

    public int getScale() {
        return scale;
    }

    public int getHeight(){
        return 7* scale;
    }

    public void changeScale(int incr){
        scale +=incr;
    }

    public void set(int x,int y,String newText,int color,int scale) {
        if (Functions.isValidCoordinate(x,y)){
            this.x = x;
            this.y = y;
            this.content = newText;
            this.color = new Color(color,true);
            this.scale = scale;
        }else {
            SignMod.LOGGER.warn("Set is unvalid : x : " + this.x + " / y : " + this.y + "\nSkip unvalid settlement !!");
        }
    }

    public void setPosition(int x,int y){
        if (Functions.isValidCoordinate(x,y)){
            this.x = x;
            this.y = y;
        }
    }

    public void render(BufferBuilder builder){
        Minecraft.getInstance().getTextureManager().bindTexture(TEXT);
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int n=content.length();
        int shift = 0;
        for (int i=0;i<n;i++){
            char c0 = content.charAt(i);
            if (c0 == ' '){
                shift+=4*scale;
            }else {
                Letter l = new Letter(c0,x+shift,y);
                l.render(builder,color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha(), scale);
                shift+=l.length*scale;
                char followingChar = (i!=n-1)?content.charAt(i+1):' ';
                if (followingChar>97){
                    shift+=scale;
                }else if (followingChar != ' '){
                    shift+=2*scale;
                }
            }
        }

    }

    public void renderOnScreen(int guiLeft, int guiTop){
        Minecraft.getInstance().getTextureManager().bindTexture(TEXT);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int n=content.length();
        int shift = 0;
        for (int i=0;i<n;i++){
            char c0 = content.charAt(i);
            if (c0 == ' '){
                shift+=4* scale;
            }else {
                Letter l = new Letter(c0, x + shift, y);
                l.renderOnScreen(builder,color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha(),guiLeft,guiTop, scale);
                shift += l.length* scale;
                char followingChar = (i!=n-1)?content.charAt(i+1):' ';
                if (followingChar>97){
                    shift+= scale;
                }else if (followingChar != ' '){
                    shift+= scale *2;
                }
            }
        }
        tessellator.draw();
    }

    public boolean isIn(double mouseX,double mouseY,int guiLeft,int guiTop){
        int L = getLength();
        int h = getHeight();
        return (mouseX>guiLeft-1+x && mouseX<guiLeft+x+L+1) && (mouseY>guiTop-1+y && mouseY<guiTop+y+h+1);
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT txtNBT = new CompoundNBT();
        txtNBT.putInt("x_coor",getX());
        txtNBT.putInt("y_coor",getY());
        txtNBT.putString("text_content",getText());
        txtNBT.putInt("color",color.getRGB());
        txtNBT.putInt("scale",scale);
        return txtNBT;
    }


    public void writeText(PacketBuffer buf){
        byte[] bytes = new byte[2];
        bytes[0] = (byte) x;
        bytes[1] = (byte) y;
        buf.writeByteArray(bytes);
        buf.writeInt(color.getRGB());
        buf.writeString(content);
        buf.writeInt(scale);
    }

    public static Text readText(PacketBuffer buf){
        byte[] pos = buf.readByteArray();
        int color = buf.readInt();
        String content = buf.readString();
        int scale = buf.readInt();
        if (Functions.isValidCoordinate(pos[0],pos[1])){
            return new Text(pos[0],pos[1],content,new Color(color,true),scale);
        }
        throw new IllegalArgumentException("position are badly transmited : get text outside bound");
    }

    public static Text getTextFromNBT(CompoundNBT nbt){
        int x = nbt.getInt("x_coor");
        int y = nbt.getInt("y_coor");
        String content = nbt.getString("text_content");
        int color =nbt.getInt("color");
        int scale =nbt.getInt("scale");
        return new Text(x,y,content,new Color(color,true),scale);
    }

    public boolean isEmpty() {
        return this.content.isEmpty();
    }
}
