package fr.matt1999rd.signs.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fr.matt1999rd.signs.SignMod;
import net.minecraft.util.math.vector.Matrix4f;

import javax.annotation.Nullable;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TextStyles {
    private byte flag = 0x00000;
    private final Color Transparent = new Color(0,0,0,0);
    private final Color defaultColor = Color.RED;
    private Color highlightColor;
    public static final float italicOffset = 7.0F * 0.25F;
    public static final int sideFrameGap = 5;
    public static final int upFrameGap = 3;
    public static final float underLineGap = 1F;
    private static final int overhangGap = 2;

    // all style possible :
    // BOLD -> just an offset and a second rendering
    // ITALIC -> mapping with offset
    // HIGHLIGHT (with a color) -> rendering a background color
    // UNDERLINE -> rendering a bar below
    // CIRCLE FRAME -> rendering a bar below and above + circle on right and left
    // RECTANGLE FRAME -> rendering a bar below and above and on the left and right


    public TextStyles(){
        removeHighlightColor();
    }

    public static TextStyles defaultStyle(){
        return new TextStyles();
    }

    //BOLD FORMAT

    public boolean isBold(){
        return is(Format.BOLD);
    }

    //ITALIC FORMAT

    public boolean isItalic(){
        return is(Format.ITALIC);
    }

    //CURVE FRAME FORMAT

    public TextStyles withCurveFrame(){
        if (this.hasStraightFrame()){
            SignMod.LOGGER.warn("Try setting framed curve format to style that has frame straight!");
        }else {
            this.addFormat(Format.FRAMED_CURVE);
        }
        return this;
    }

    public TextStyles withoutCurveFrame(){
        if (!this.hasCurveFrame()){
            SignMod.LOGGER.warn("Try removing framed curve format to style that has not this frame!");
        }else {
            this.removeFormat(Format.FRAMED_CURVE);
        }
        return this;
    }

    public boolean hasCurveFrame(){
        return is(Format.FRAMED_CURVE);
    }

    //STRAIGHT FRAME FORMAT

    public TextStyles withStraightFrame(){
        if (this.hasCurveFrame()){
            // two framed style exists : this exception is thrown to avoid problem when rendering a text with multiple framed style
            SignMod.LOGGER.warn("Try setting framed straight format to style that has frame curve!");
        }else {
            this.addFormat(Format.FRAMED_STRAIGHT);
        }
        return this;
    }

    public TextStyles withoutStraightFrame(){
        if (!this.hasStraightFrame()){
            SignMod.LOGGER.warn("Try removing framed curve format to style that has not this frame!");
        }else {
            this.removeFormat(Format.FRAMED_STRAIGHT);
        }
        return this;
    }

    public boolean hasStraightFrame(){
        return is(Format.FRAMED_STRAIGHT);
    }

    //UNDERLINE FORMAT

    public boolean isUnderline(){
        return is(Format.UNDERLINE);
    }

    //HIGHLIGHT FORMAT

    public Color getHighlightColor(){
        return highlightColor;
    }

    public void setHighlightColor(Color color){
        highlightColor = color;
    }

    public void removeHighlightColor(){
        highlightColor = Transparent;
    }

    public boolean hasHighlightColor() { return highlightColor.getAlpha() != 0;}

    //GENERIC FUNCTION FOR FORMAT

    public void addFormat(Format format){
        if (format == Format.HIGHLIGHT){
            addFormat(format,defaultColor); //default color
        }else {
            addFormat(format,null);
        }
    }

    public void addFormat(Format format, @Nullable Color color){
        // flag = 0xABCDE
        // when we have bold for instance,
        // we do operation : 0xABCDE or 0x00001 -> 0xABCD1
        if (format == Format.HIGHLIGHT){
            setHighlightColor(color);
        }
        int offset = format.offset;
        flag = (byte) (flag | 1<<offset);
    }

    public void removeFormat(Format format){
        // flag = 0xABCDE
        // when we have bold for instance,
        // we do operation : 0xABCDE and not(0x00001) [=0x11110] -> 0xABCD0
        if (format == Format.HIGHLIGHT){
            removeHighlightColor();
        }
        int offset = format.offset;
        flag = (byte) (flag & ~(1<<offset));
    }

    public boolean is(Format format){
        if (format == Format.HIGHLIGHT){
            return hasHighlightColor();
        }
        int mask = 1<<format.offset;
        return (flag & mask) == mask;
    }

    //RENDERING FUNCTION

    public void render(MatrixStack stack, IVertexBuilder builder, Color color, int combinedLight,Text t,boolean isOnScreen) {
        // isOnScreen is mandatory because minecraft does not handle layering in screen -> if z != 0 it will render messing texture
        if (t.isEmpty())return;
        for (Format format : Format.formatByRenderOrder){
            if (is(format)){
                format.render(stack,builder,color,combinedLight,t,isOnScreen,this);
            }
        }
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
        ITALIC(0,5),
        BOLD(1,4),
        UNDERLINE(2,3),
        HIGHLIGHT(3,0),
        FRAMED_STRAIGHT(4,1),
        FRAMED_CURVE(5,2);
        final int offset;
        final int renderOrder;
        public static final List<Format> formatByRenderOrder = Arrays.stream(Format.values())
                        .sorted(Comparator.comparingInt(format -> format.renderOrder))
                        .collect(Collectors.toList());
        private final Rectangle2D lineUVRectangle = new Rectangle2D.Float(8,37,1,1);
        private final Rectangle2D semiLeftCircleUVRectangle  = new Rectangle2D.Float(2, 37,7,15);
        private final Rectangle2D semiRightCircleUVRectangle = new Rectangle2D.Float(10,37,7,15);

        Format(int offset,int renderOrder) {
            this.offset = offset;
            this.renderOrder = renderOrder;
        }

        public void render(MatrixStack stack, IVertexBuilder builder, Color color, int combinedLight, Text t, boolean isOnScreen,TextStyles styles) {
            stack.pushPose();
            Matrix4f matrix4f = stack.last().pose();
            switch (this){
                case UNDERLINE:
                    renderTexture(matrix4f,builder,new Rectangle2D.Float(0,t.getHeight(false),t.getLength(false,false),underLineGap),lineUVRectangle,combinedLight,color,false);
                    break;
                case HIGHLIGHT:
                    RenderSystem.enableBlend();
                    RenderSystem.disableTexture();
                    RenderSystem.defaultBlendFunc();
                    boolean hasFrame = styles.hasCurveFrame() || styles.hasStraightFrame();
                    // last boolean is the boolean isBackground which offset the rendering of the texture.
                    // It must be done in real rendering but mustn't be done on Screen (see comment in render function)
                    renderTexture(matrix4f,builder,
                            new Rectangle2D.Float(
                                    -1-(hasFrame?sideFrameGap:0),
                                    -1-(hasFrame?upFrameGap:0),
                                    t.getLength(false,false)+2+(hasFrame?2*sideFrameGap:0),
                                    t.getHeight(false)+2+(hasFrame?2*upFrameGap:0)),
                            lineUVRectangle,combinedLight,styles.highlightColor,!isOnScreen);
                    RenderSystem.disableBlend();
                    RenderSystem.enableTexture();
                    break;
                case FRAMED_CURVE:
                    renderFrame(stack,builder,color,combinedLight,t,true);
                    break;
                case FRAMED_STRAIGHT:
                    renderFrame(stack,builder,color,combinedLight,t,false);
                    break;
            }
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

        private void renderFrame(MatrixStack stack,IVertexBuilder builder,Color color,int combinedLight,Text t,boolean isCurve){
            stack.translate(-sideFrameGap,-upFrameGap,0);
            Matrix4f matrix4f = stack.last().pose();
            float L = t.getLength(false);
            float H = t.getHeight();
            float sidePartWidth = isCurve ? sideFrameGap + overhangGap : 1;
            float horLineWidth = L - 2*sidePartWidth;

            renderTexture(matrix4f,builder,new Rectangle2D.Float(0,0,sidePartWidth,H),isCurve?semiLeftCircleUVRectangle:lineUVRectangle,combinedLight,color,false); //left semi circle
            renderTexture(matrix4f,builder,new Rectangle2D.Float(sidePartWidth,0, horLineWidth,1),lineUVRectangle,combinedLight,color,false); //up horizontal line
            renderTexture(matrix4f,builder,new Rectangle2D.Float(sidePartWidth,H-1, horLineWidth,1),lineUVRectangle,combinedLight,color,false); //down horizontal line
            renderTexture(matrix4f,builder,new Rectangle2D.Float(L - sidePartWidth,0,sidePartWidth,H),isCurve?semiRightCircleUVRectangle:lineUVRectangle,combinedLight,color,false); //right semi circle

            stack.translate(sideFrameGap,upFrameGap,0);
        }

        public int getUXOffset() {
            if (this.isFrame())return 80;
            return 10*offset;
        }

        public int getVOffset(TextStyles styles){
            if ((styles.is(this) && !this.isFrame()) || this == FRAMED_CURVE) {
                return 10;
            }
            return 0;
        }

        public boolean isFrame(){
            return this == FRAMED_CURVE || this == FRAMED_STRAIGHT;
        }
    }


}
