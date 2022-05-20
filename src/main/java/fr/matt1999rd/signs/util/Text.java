package fr.matt1999rd.signs.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.PSDisplayMode;
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
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;

import java.awt.*;

public class Text {
    private float x,y;
    private int scale;
    private String content;
    private Color color;
    private TextStyles styles = TextStyles.defaultStyle();
    public static final ResourceLocation TEXT = new ResourceLocation(SignMod.MODID,"textures/gui/letter.png");
    private static final RenderType textRenderType = RenderType.text(TEXT);
    public static final int UNSELECTED_TEXT_ID = -1;
    public static final int defaultHeight = 7;

    public Text(float x,float y,String txt,Color color,int scale){
        this.x = x;
        this.y = y;
        this.content = txt;
        this.color = color;
        this.scale = scale;
    }

    public Text(float x, float y, String txt, Color color, int scale, TextStyles styles){
        this(x, y, txt, color, scale);
        this.styles = styles;
    }

    public Text(int x,int y,String txt,Color color,int scale){
        this((float) x,(float) y,txt,color,scale);
    }

    public Text(Vector2i position,String txt,Color color,int scale){
        this(position.getX(),position.getY(),txt,color,scale);
    }

    public Text(Text t){
        this(t.getX(false), t.getY(false), t.getText(), t.color, t.getScale(),t.styles);
    }

    public static Text getDefaultText(Vector2i position,Color color){
        return new Text(position,"",color,1);
    }

    public static Text getDefaultText(){
        return new Text(0,0,"",Color.WHITE,1);
    }

    public float getX(boolean handleStyle){
        float offsetX = handleStyle ? styles.offsetX() : 0;
        return x+offsetX;
    }

    public float getY(boolean handleStyle){
        float offsetY = handleStyle ? styles.offsetY() : 0;
        return y + offsetY;
    }
    public String getText(){
        return content;
    }

    public int getColor(){ return this.color.getRGB(); }

    public float getLength(boolean scaled){
        return getLength(this.content,this.styles,scale,scaled);
    }

    public static float getLength(String content,TextStyles styles,float scale,boolean scaled){
        int n=content.length();
        float length = 0;
        for (int i=0;i<n;i++){
            char c0 = content.charAt(i);
            if (c0 == ' '){
                length+=4;
            }else {
                Letter l = new Letter(c0, 0,styles,scale);
                length += l.length;
                char followingChar = (i != n - 1) ? content.charAt(i + 1) : ' ';
                if (followingChar > 97) {
                    length += 1;
                } else if (followingChar != ' ') {
                    length += 2;
                }
            }
        }
        if (styles.isItalic()){
            length+=TextStyles.italicOffset;
        }
        if (styles.hasStraightFrame() || styles.hasCurveFrame()){
            length+=2*TextStyles.sideFrameGap;
        }
        return scaled ? (length * scale) : length;
    }

    public int getScale() {
        return scale;
    }

    public int getHeight(){
        if (styles.isUnderline()){
            return (defaultHeight + TextStyles.underLineGap) * scale;
        }else if (styles.hasCurveFrame() || styles.hasStraightFrame()){
            return (defaultHeight + TextStyles.upFrameGap * 2) * scale;
        }
        return defaultHeight * scale;
    }

    public TextStyles getStyles(){ return styles; }

    public void changeScale(int increment){
        scale += increment;
    }

    public void setPosition(int x,int y){
        setPosition((float) x,(float) y,true,true);
    }

    public void setPosition(float x,float y,boolean handleOffset,boolean validateCoordinate){
        if (Functions.isValidCoordinate(x,y) || !validateCoordinate){
            this.x = x - (handleOffset? styles.offsetX() : 0);
            this.y = y - (handleOffset? styles.offsetY() : 0);
        }
    }

    public void setText(String content){
        this.content = content;
    }

    public void render(MatrixStack stack, IRenderTypeBuffer buffer,Vector3f origin, Vector2f pixelDimension, int combinedLight){
        IVertexBuilder builder = buffer.getBuffer(textRenderType);
        render(stack,builder,origin,pixelDimension,combinedLight,false);
    }

    private void render(MatrixStack stack,IVertexBuilder builder,Vector3f origin, Vector2f pixelDimension,int combinedLight,boolean isOnScreen){
        stack.pushPose();
        stack.translate(origin.x(),origin.y(),origin.z());
        stack.scale((isOnScreen?1:-1)*pixelDimension.x,(isOnScreen?1:-1)*pixelDimension.y,1);
        int n=content.length();
        stack.translate(x,y,0);
        styles.render(stack,builder,color,combinedLight,this,isOnScreen);
        Matrix4f matrix4f = stack.last().pose();
        int shift = 0;
        for (int i=0;i<n;i++){
            char c0 = content.charAt(i);
            if (c0 == ' '){
                shift+=4*scale;
            }else {
                Letter l = new Letter(c0,shift,this.styles,scale);
                l.render(matrix4f,builder,color,combinedLight);
                shift+=l.length*scale;
                char followingChar = (i!=n-1)?content.charAt(i+1):' ';
                if (followingChar>97){
                    shift+=scale;
                }else if (followingChar != ' '){
                    shift+=2*scale;
                }
            }
        }
        stack.popPose();
    }

    public void renderOnScreen(MatrixStack stack,Vector2i origin,Vector2f pixelDimension,boolean isSelected,boolean needScale){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        Minecraft.getInstance().getTextureManager().bind(TEXT);
        render(stack,builder,new Vector3f(origin.getX(),origin.getY(),0),pixelDimension,
              15728880,true);
        tessellator.end();
        if (needScale) {
            //psste function
            Functions.renderTextLimit(
                    origin.getX() + this.getX(true) * pixelDimension.x,
                    origin.getY() + this.getY(true) * pixelDimension.y,
                    2 * this.getLength(true) * pixelDimension.x / pixelDimension.y,
                    2 * this.getHeight(), isSelected);
        }else {
            //dste function
            Functions.renderTextLimit(
                    origin.getX() + this.getX(true),
                    origin.getY() + this.getY(true),
                    this.getLength(true),
                    this.getHeight(), isSelected);
        }
    }

    public boolean isIn(double mouseX,double mouseY,int guiLeft,int guiTop,float scaleX,float scaleY){
        float L = getLength(true)*scaleX/scaleY;
        float h = getHeight();
        return (mouseX>guiLeft-1+getX(true)*scaleX && mouseX<guiLeft+getX(true)*scaleX+L+1) && (mouseY>guiTop-1+getY(true)*scaleY && mouseY<guiTop+getY(true)*scaleY+h+1);
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT txtNBT = new CompoundNBT();
        txtNBT.putFloat("x_coor",x);
        txtNBT.putFloat("y_coor",y);
        txtNBT.putString("text_content",content);
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

    public void cutText(int maxLength) {
        int stringLength = this.content.length();
        while (this.getLength(true)>maxLength){
            this.content = this.content.substring(0,stringLength - 1);
            stringLength = this.content.length();
        }
    }

    public void centerText(PSDisplayMode mode,int index) {
        int maxLength = mode.getMaxLength(index);
        Vector2i begPosition = mode.getTextBegPosition(index);
        float length = this.getLength(true);
        this.setPosition(begPosition.getX() + (maxLength - length) / 2.0F, this.getY(false),false,false);
    }
}
