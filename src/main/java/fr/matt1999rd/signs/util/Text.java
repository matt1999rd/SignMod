package fr.matt1999rd.signs.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fr.matt1999rd.signs.SignMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.common.util.INBTSerializable;

import java.awt.*;

public class Text {
    private float x,y;
    private int scale;
    private String content;
    private Color color;
    public static final ResourceLocation TEXT = new ResourceLocation(SignMod.MODID,"textures/gui/letter.png");
    private static final RenderType textRenderType = RenderType.text(TEXT);

    public Text(float x,float y,String txt,Color color,int scale){
        this.x = x;
        this.y = y;
        this.content = txt;
        this.color = color;
        this.scale = scale;
    }

    public Text(int x,int y,String txt,Color color,int scale){
        this((float) x,(float) y,txt,color,scale);
    }

    public Text(Vector2i position,String txt,Color color,int scale){
        this(position.getX(),position.getY(),txt,color,scale);
    }

    public Text(Text t){
        this(t.getX(), t.getY(), t.getText(), new Color(t.getColor(),true), t.getScale());
    }

    public static Text getDefaultText(Vector2i position,Color color){
        return new Text(position,"",color,1);
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

    public void setText(String content){
        this.content = content;
    }

    public void render(MatrixStack stack, IRenderTypeBuffer buffer, float xOrigin, float yOrigin, float zOrigin, float pixelLength, float pixelHeight,int combinedLight){
        Matrix4f matrix4f = stack.last().pose();
        IVertexBuilder builder = buffer.getBuffer(textRenderType);
        int n=content.length();
        int shift = 0;
        for (int i=0;i<n;i++){
            char c0 = content.charAt(i);
            if (c0 == ' '){
                shift+=4*scale;
            }else {
                Letter l = new Letter(c0,x+shift,y);
                l.render(matrix4f,builder,color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha(), scale,xOrigin,yOrigin,zOrigin,pixelLength,pixelHeight,combinedLight);
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

    public void renderOnScreen(MatrixStack stack,int guiLeft, int guiTop,float xyScale,boolean positionUnchangedByScale){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        Matrix4f matrix4f = stack.last().pose();
        Minecraft.getInstance().getTextureManager().bind(TEXT);
        int n=content.length();
        float scaleX = scale*xyScale; //scale X is the length of a 1*1 unit pixel where scale is the height of this pixel
        float scaleForUnchangedPosition = (positionUnchangedByScale)?scaleX:1.0F;
        float shift = 0;
        for (int i=0;i<n;i++){
            char c0 = content.charAt(i);
            if (c0 == ' '){
                shift+=4*scaleForUnchangedPosition;
            }else {
                Letter l = new Letter(c0, x + shift, y);
                l.renderOnScreen(matrix4f,builder,guiLeft,guiTop,color, scaleX,scale,positionUnchangedByScale);
                shift += l.length*scaleForUnchangedPosition;
                char followingChar = (i!=n-1)?content.charAt(i+1):' ';
                if (followingChar>97){
                    shift+= 1*scaleForUnchangedPosition;
                }else if (followingChar != ' '){
                    shift+= 2*scaleForUnchangedPosition;
                }
            }
        }
        tessellator.end();
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
        buf.writeUtf(content);
        buf.writeInt(scale);
    }

    public static Text readText(PacketBuffer buf){
        float x,y;
        x=buf.readFloat();
        y=buf.readFloat();
        int color = buf.readInt();
        String content = buf.readUtf();
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

    public void setColor(int color) {
        this.color = new Color(color);
    }
}
