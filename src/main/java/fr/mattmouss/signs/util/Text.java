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
    private float x,y;
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

    public Text(float x,float y,String txt,Color color,int scale){
        this.x = x;
        this.y = y;
        this.content = txt;
        this.color = color;
        this.scale = scale;
    }

    public Text(Text t){
        this.x = t.getX();
        this.y = t.getY();
        this.content = t.getText();
        this.color = new Color(t.getColor(),true);
        this.scale = t.getScale();
    }

    public static Text getDefaultText(){
        return new Text(0,0,"",Color.WHITE,1);
    }

    public float getX(){
        return x;
    }

    public float getY(){
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

    public void setPosition(int x,int y){
        if (Functions.isValidCoordinate(x,y)){
            this.x = x;
            this.y = y;
        }
    }

    public void setPosition(float x,float y){
        this.x = x;
        this.y = y;
    }

    public void render(BufferBuilder builder,float xOrigin, float yOrigin, float zOrigin, float pixelLength, float pixelHeight){
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
                l.render(builder,color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha(), scale,xOrigin,yOrigin,zOrigin,pixelLength,pixelHeight);
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

    public void renderOnScreen(int guiLeft, int guiTop,float xyScale,boolean positionUnchangedByScale){
        Minecraft.getInstance().getTextureManager().bindTexture(TEXT);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int n=content.length();
        float scaleX = scale*xyScale; //scale X is the length of a 1*1 unit pixel where scale is the heigth of this pixel
        float scaleForUnchangedPosition = (positionUnchangedByScale)?scaleX:1.0F;
        float shift = 0;
        for (int i=0;i<n;i++){
            char c0 = content.charAt(i);
            if (c0 == ' '){
                shift+=4*scaleForUnchangedPosition;
            }else {
                Letter l = new Letter(c0, x + shift, y);
                l.renderOnScreen(builder,color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha(),guiLeft,guiTop, scaleX,scale,positionUnchangedByScale);
                shift += l.length*scaleForUnchangedPosition;
                char followingChar = (i!=n-1)?content.charAt(i+1):' ';
                if (followingChar>97){
                    shift+= 1*scaleForUnchangedPosition;
                }else if (followingChar != ' '){
                    shift+= 2*scaleForUnchangedPosition;
                }
            }
        }
        tessellator.draw();
    }

    public boolean isIn(double mouseX,double mouseY,int guiLeft,int guiTop,float scaleX,float scaleY){
        float L = getLength()*scaleX/scaleY;
        float h = getHeight();
        return (mouseX>guiLeft-1+x*scaleX && mouseX<guiLeft+x*scaleX+L+1) && (mouseY>guiTop-1+y*scaleY && mouseY<guiTop+y*scaleY+h+1);
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT txtNBT = new CompoundNBT();
        txtNBT.putFloat("x_coor",getX());
        txtNBT.putFloat("y_coor",getY());
        txtNBT.putString("text_content",getText());
        txtNBT.putInt("color",color.getRGB());
        txtNBT.putInt("scale",scale);
        return txtNBT;
    }


    public void writeText(PacketBuffer buf){
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeInt(color.getRGB());
        buf.writeString(content);
        buf.writeInt(scale);
    }

    public static Text readText(PacketBuffer buf){
        float x,y;
        x=buf.readFloat();
        y=buf.readFloat();
        int color = buf.readInt();
        String content = buf.readString();
        int scale = buf.readInt();
        return new Text(x,y,content,new Color(color,true),scale);
    }

    public static Text getTextFromNBT(CompoundNBT nbt){
        float x = nbt.getFloat("x_coor");
        float y = nbt.getFloat("y_coor");
        String content = nbt.getString("text_content");
        int color =nbt.getInt("color");
        int scale =nbt.getInt("scale");
        return new Text(x,y,content,new Color(color,true),scale);
    }

    public boolean isEmpty() {
        return this.content.isEmpty();
    }
}
