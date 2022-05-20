package fr.matt1999rd.signs.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fr.matt1999rd.signs.SignMod;
import net.minecraft.util.math.vector.Matrix4f;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TextStyles {
    private byte flag = 0x00000;
    private final Color Transparent = new Color(0,0,0,0);
    private Color highlightColor;
    public static final float italicOffset = 7.0F * 0.25F;
    public static final int sideFrameGap = 5;
    public static final int upFrameGap = 3;
    public static final int underLineGap = 2;
    private static final int overhangedGap = 2;
    private final Rectangle2D lineUVRectangle = new Rectangle2D.Float(8,37,1,1);
    private final Rectangle2D semiLeftCircleUVRectangle  = new Rectangle2D.Float(2, 37,7,15);
    private final Rectangle2D semiRightCircleUVRectangle = new Rectangle2D.Float(10,37,7,15);


    public TextStyles(){
        removeHighlightColor();
    }

    public static TextStyles defaultStyle(){
        TextStyles styles = new TextStyles();
        styles.addHighlightColor(Color.GREEN);
        return styles;
    }

    public TextStyles bold(){
        this.addFormat(Format.BOLD);
        return this;
    }

    public boolean isBold(){
        return is(Format.BOLD);
    }

    public TextStyles italic(){
        this.addFormat(Format.ITALIC);
        return this;
    }

    public boolean isItalic(){
        return is(Format.ITALIC);
    }

    public TextStyles withCurveFrame(){
        if (this.hasStraightFrame()){
            SignMod.LOGGER.warn("Try setting framed curve format to style that has frame straight!");
        }else {
            this.addFormat(Format.FRAMED_CURVE);
        }
        return this;
    }

    public boolean hasCurveFrame(){
        return is(Format.FRAMED_CURVE);
    }

    public TextStyles withStraightFrame(){
        if (this.hasCurveFrame()){
            // two framed style exists : this exception is thrown to avoid problem when rendering a text with multiple framed style
            SignMod.LOGGER.warn("Try setting framed straight format to style that has frame curve!");
        }else {
            this.addFormat(Format.FRAMED_STRAIGHT);
        }
        return this;
    }

    public boolean hasStraightFrame(){
        return is(Format.FRAMED_STRAIGHT);
    }

    public TextStyles underline(){
        this.addFormat(Format.UNDERLINE);
        return this;
    }

    public boolean isUnderline(){
        return is(Format.UNDERLINE);
    }

    public boolean is(Format format){
        int mask = 1<<format.offset;
        return (flag & mask) == mask;
    }

    public int getHighlightColor(){
        return highlightColor.getRGB();
    }

    public void addHighlightColor(Color color){
        highlightColor = color;
    }

    public boolean hasHighlightColor() { return highlightColor.getAlpha() != 0;}

    public void removeHighlightColor(){
        highlightColor = Transparent;
    }

    public void addFormat(Format format){
        int offset = format.offset;
        flag = (byte) (flag | 1<<offset);
    }

    public void render(MatrixStack stack, IVertexBuilder builder, Color color, int combinedLight,Text t,boolean isOnScreen) {
        // isOnScreen is mandatory because minecraft does not handle layering in screen -> if z != 0 it will render messing texture
        if (t.isEmpty())return;
        if (hasCurveFrame()){
            renderFrame(stack,builder,color,combinedLight,t,true);
        }
        if (hasStraightFrame()){
            renderFrame(stack,builder,color,combinedLight,t,false);
        }
        if (isUnderline()){
            renderBar(stack, builder, color, combinedLight, t);
        }

        if (hasHighlightColor()){
            renderHighlightColor(stack,builder,combinedLight,t,isOnScreen);
        }
    }

    private void renderFrame(MatrixStack stack,IVertexBuilder builder,Color color,int combinedLight,Text t,boolean isCurve){
        stack.pushPose();
        stack.translate(-sideFrameGap,-upFrameGap,0);
        Matrix4f matrix4f = stack.last().pose();
        float L = t.getLength(false);
        float H = t.getHeight();
        float sidePartWidth = isCurve ? sideFrameGap + overhangedGap : 1;
        float horLineWidth = L - 2*sidePartWidth;

        renderTexture(matrix4f,builder,new Rectangle2D.Float(0,0,sidePartWidth,H),isCurve?semiLeftCircleUVRectangle:lineUVRectangle,combinedLight,color,false); //left semi circle
        renderTexture(matrix4f,builder,new Rectangle2D.Float(sidePartWidth,0, horLineWidth,1),lineUVRectangle,combinedLight,color,false); //up horizontal line
        renderTexture(matrix4f,builder,new Rectangle2D.Float(sidePartWidth,H-1, horLineWidth,1),lineUVRectangle,combinedLight,color,false); //down horizontal line
        renderTexture(matrix4f,builder,new Rectangle2D.Float(L - sidePartWidth,0,sidePartWidth,H),semiRightCircleUVRectangle,combinedLight,color,false); //right semi circle

        stack.popPose();
    }

    private void renderBar(MatrixStack stack,IVertexBuilder builder,Color color,int combinedLight,Text t){
        stack.pushPose();
        Matrix4f matrix4f = stack.last().pose();
        renderTexture(matrix4f,builder,new Rectangle2D.Float(0,t.getHeight()+underLineGap-1,t.getLength(false),1),lineUVRectangle,combinedLight,color,false);
        stack.popPose();
    }

    private void renderHighlightColor(MatrixStack stack,IVertexBuilder builder,int combinedLight,Text t,boolean isOnScreen){
        stack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        Matrix4f matrix4f = stack.last().pose();
        // last boolean is the boolean isBackground which offset the rendering of the texture. It must be done in real rendering but mustn't be done on Screen (see comment in render function)
        renderTexture(matrix4f,builder,new Rectangle2D.Float(-1,-1,t.getLength(false)+2,t.getHeight()+2),lineUVRectangle,combinedLight,highlightColor,!isOnScreen);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        stack.popPose();
    }

    private void renderTexture(Matrix4f matrix4f, IVertexBuilder builder, Rectangle2D vertexRectangle, Rectangle2D uvRectangle, int light, Color color,boolean isBackground){
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();
        float z = isBackground ? 0.0005F : 0;
        builder.vertex(matrix4f, (float) vertexRectangle.getMinX(), (float) vertexRectangle.getMinY(),z).color(red,green,blue,alpha).uv((float) uvRectangle.getMinX()/256F, (float) uvRectangle.getMinY()/256F).uv2(light).endVertex();
        builder.vertex(matrix4f, (float) vertexRectangle.getMinX(), (float) vertexRectangle.getMaxY(),z).color(red,green,blue,alpha).uv((float) uvRectangle.getMinX()/256F, (float) uvRectangle.getMaxY()/256F).uv2(light).endVertex();
        builder.vertex(matrix4f, (float) vertexRectangle.getMaxX(), (float) vertexRectangle.getMaxY(),z).color(red,green,blue,alpha).uv((float) uvRectangle.getMaxX()/256F, (float) uvRectangle.getMaxY()/256F).uv2(light).endVertex();
        builder.vertex(matrix4f, (float) vertexRectangle.getMaxX(), (float) vertexRectangle.getMinY(),z).color(red,green,blue,alpha).uv((float) uvRectangle.getMaxX()/256F, (float) uvRectangle.getMinY()/256F).uv2(light).endVertex();
    }

    public float offsetX() {
        if (hasCurveFrame() || hasStraightFrame()){
            return -sideFrameGap;
        }
        return 0;
    }

    public float offsetY() {
        if (hasCurveFrame() || hasStraightFrame()){
            return -upFrameGap;
        }
        return 0;
    }

    public enum Format {
        BOLD(0),
        ITALIC(1),
        FRAMED_CURVE(2),
        FRAMED_STRAIGHT(3),
        UNDERLINE(4);
        final int offset;
        Format(int offset) {
            this.offset = offset;
        }
    }


}
