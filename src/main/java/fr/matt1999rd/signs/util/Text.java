package fr.matt1999rd.signs.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.PSDisplayMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec2;
import com.mojang.math.Vector3f;

import java.awt.*;

public class Text {
    private float x,y;
    private int scale;
    private String content;
    private Color color;
    private TextStyles styles = TextStyles.defaultStyle();
    public static final ResourceLocation TEXT = new ResourceLocation(SignMod.MODID,"textures/gui/letter.png");
    private static final RenderType textRenderType = RenderType.text(TEXT); // text function is for texture !!
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

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX(boolean handleStyle){
        float offsetX = handleStyle ? offsetX() : 0;
        return x + offsetX;
    }

    public float getY(boolean handleStyle){
        float offsetY = handleStyle ? offsetY() : 0;
        return y + offsetY;
    }
    public String getText(){
        return content;
    }

    public int getColor(){ return this.color.getRGB(); }

    //used for rendering and authorisation after creating text
    public float getLength(boolean scaled,boolean handleStyle){
        return getLength(this.content,this.styles,scale,scaled,handleStyle);
    }

    //used for action before creating text
    public static float getLength(String content,TextStyles styles,float scale){
        return getLength(content,styles,scale,true,true);
    }

    private static float getLength(String content,TextStyles styles,float scale,boolean scaled,boolean handleStyle){
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
        if (handleStyle){
            length += styles.getLength(scale);
        }
        return scaled ? (length * scale) : length;
    }

    public int getScale() {
        return scale;
    }

    public float getHeight(){
        return getHeight(true);
    }

    public float getHeight(boolean handleStyle){
        float height = defaultHeight;
        if (handleStyle){
            height += styles.getHeight(scale);
        }

        return height * scale;
    }

    public TextStyles getStyles(){ return styles; }

    public void changeScale(int increment){
        scale += increment;
    }

    public void setPosition(int x,int y){
        setPosition((float) x,(float) y,true,true);
    }

    public void setPosition(float x,float y){
        setPosition(x, y,true,true);
    }

    public void setPosition(float x,float y,boolean handleOffset,boolean validateCoordinate){
        if (Functions.isValidCoordinate(x,y) || !validateCoordinate){
            this.x = x - (handleOffset? offsetX() : 0);
            this.y = y - (handleOffset? offsetY() : 0);
        }
    }

    public void setStyles(TextStyles styles){
        this.styles = TextStyles.copy(styles);
    }

    public void setText(String content){
        this.content = content;
    }

    public void render(PoseStack stack, MultiBufferSource buffer,Vector3f origin, Vec2 pixelDimension, int combinedLight){
        VertexConsumer builder = buffer.getBuffer(textRenderType);
        render(stack,builder,origin,pixelDimension,combinedLight,false);
    }

    private void render(PoseStack stack,VertexConsumer builder,Vector3f origin, Vec2 pixelDimension,int combinedLight,boolean isOnScreen){
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

    public void renderOnScreen(PoseStack stack,Vector2i origin,Vec2 pixelDimension,boolean isSelected,boolean needScale){

        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        RenderSystem.setShaderTexture(0,TEXT);
        render(stack,builder,new Vector3f(origin.getX(),origin.getY(),0),pixelDimension,
              15728880,true);
        tesselator.end();
        if (needScale) {
            //psste function
            Functions.renderTextLimit(
                    origin.getX() + this.getX(true) * pixelDimension.x,
                    origin.getY() + this.getY(true) * pixelDimension.y,
                    2 * this.getLength(true,true) * pixelDimension.x / pixelDimension.y,
                    2 * this.getHeight(), isSelected);
        }else {
            //dste function
            Functions.renderTextLimit(
                    origin.getX() + this.getX(true),
                    origin.getY() + this.getY(true),
                    this.getLength(true,true),
                    this.getHeight(), isSelected);
        }
    }

    public boolean isIn(double mouseX,double mouseY,int guiLeft,int guiTop,float scaleX,float scaleY){
        float L = getLength(true,true)*scaleX/scaleY;
        float h = getHeight();
        return (mouseX>guiLeft-1+getX(true)*scaleX && mouseX<guiLeft+getX(true)*scaleX+L+1) && (mouseY>guiTop-1+getY(true)*scaleY && mouseY<guiTop+getY(true)*scaleY+h+1);
    }

    public CompoundTag serializeNBT() {
        CompoundTag txtNBT = new CompoundTag();
        txtNBT.putFloat("x_coor",x);
        txtNBT.putFloat("y_coor",y);
        txtNBT.putString("text_content",content);
        txtNBT.putInt("color",color.getRGB());
        txtNBT.putInt("scale",scale);
        txtNBT.put("styles",styles.serializeNBT());
        return txtNBT;
    }


    public void writeText(FriendlyByteBuf buf){
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeInt(color.getRGB());
        buf.writeUtf(content);
        buf.writeInt(scale);
        styles.writeStyle(buf);
    }

    public static Text readText(FriendlyByteBuf buf){
        float x,y;
        x=buf.readFloat();
        y=buf.readFloat();
        int color = buf.readInt();
        String content = buf.readUtf();
        int scale = buf.readInt();
        TextStyles styles = TextStyles.readStyles(buf);
        return new Text(x,y,content,new Color(color,true),scale,styles);
    }

    public static Text getTextFromNBT(CompoundTag nbt){
        float x = nbt.getFloat("x_coor");
        float y = nbt.getFloat("y_coor");
        String content = nbt.getString("text_content");
        int color =nbt.getInt("color");
        int scale =nbt.getInt("scale");
        TextStyles styles = TextStyles.getStylesFromNBT(nbt.getCompound("styles"));
        return new Text(x,y,content,new Color(color,true),scale,styles);
    }

    public boolean isEmpty() {
        return this.content.isEmpty();
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }

    public void cutText(int maxLength) {
        while (this.getLength(true,true)>maxLength){
            int stringLength = this.content.length();
            this.content = this.content.substring(0,stringLength - 1);
        }
    }

    public void centerText(PSDisplayMode mode,int index) {
        int maxLength = mode.getMaxLength(index);
        Vector2i begPosition = mode.getTextBegPosition(index);
        float length = this.getLength(true,false);
        this.setPosition(begPosition.getX() + (maxLength - length) / 2.0F, this.getY(false),false,false);
    }

    public float offsetX(){
        return this.getStyles().offsetX(this.scale);
    }

    public float offsetY(){
        return this.getStyles().offsetY(this.scale);
    }
}
