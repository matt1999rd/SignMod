package fr.matt1999rd.signs.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fr.matt1999rd.signs.SignMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import com.mojang.math.Matrix4f;

import javax.annotation.Nullable;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TextStyles {
    private final Color Transparent = new Color(0,0,0,0);
    private final Color defaultColor = Color.RED;
    private byte flag = 0x00000;
    private Color highlightColor;
    public static final float italicOffset = 7.0F * 0.25F;
    public static final int sideFrameGap = 5;
    public static final int upFrameGap = 2;
    public static final float underLineGap = 0.5F;
    private static final int overhangGap = 2;
    private static final int uvCircleDiameter = 15;
    public static final int highlightGap = 1;

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

    private TextStyles(byte flag,int color){
        this.setFlag(flag);
        this.setHighlightColor(new Color(color,true));
    }

    public static TextStyles defaultStyle(){
        return new TextStyles();
    }

    public void setFlag(byte flag){
        this.flag = flag;
    }

    public static TextStyles copy(TextStyles styles) {
        TextStyles copy = TextStyles.defaultStyle();
        copy.flag = styles.flag;
        if (styles.hasHighlightColor()){
            copy.setHighlightColor(styles.getHighlightColor());
        }
        return copy;
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
            SignMod.LOGGER.warn("Try removing framed straight format to style that has not this frame!");
        }else {
            this.removeFormat(Format.FRAMED_STRAIGHT);
        }
        return this;
    }

    public boolean hasStraightFrame(){
        return is(Format.FRAMED_STRAIGHT);
    }

    public TextStyles withoutFrame(){
        TextStyles copy = TextStyles.copy(this);
        if (this.hasCurveFrame())return copy.withoutCurveFrame();
        else if (this.hasStraightFrame())return copy.withoutStraightFrame();
        return copy;
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

    public void render(PoseStack stack, VertexConsumer builder, Color color, int combinedLight,Text t,boolean isOnScreen) {
        // isOnScreen is mandatory because minecraft does not handle layering in screen -> if z != 0 it will render messing texture
        if (t.isEmpty())return;
        for (Format format : Format.formatByRenderOrder){
            if (is(format)){
                format.render(stack,builder,color,combinedLight,t,isOnScreen,this);
            }
        }
    }

    public float offsetX(int scale) {
        float offset = 0;
        if (hasHighlightColor()){
            offset -= highlightGap; //negative value
        }
        if (hasCurveFrame() || hasStraightFrame()){
            offset -= sideFrameGap * scale; //negative value
        }
        return offset;
    }

    public float offsetY(int scale) {
        float offset = 0;
        if (hasHighlightColor()){
            offset -= highlightGap; //negative value
        }
        if (hasCurveFrame() || hasStraightFrame()){
            offset -= upFrameGap * scale; //negative value
        }
        return offset;
    }

    public CompoundTag serializeNBT(){
        CompoundTag nbt = new CompoundTag();
        nbt.putByte("flag",flag);
        nbt.putInt("color",highlightColor.getRGB());
        return nbt;
    }

    public static TextStyles getStylesFromNBT(CompoundTag nbt) {
        byte flag = nbt.getByte("flag");
        int color = nbt.getInt("color");
        return new TextStyles(flag,color);
    }

    public void writeStyle(FriendlyByteBuf buf) {
        buf.writeByte(flag);
        buf.writeInt(highlightColor.getRGB());
    }

    public static TextStyles readStyles(FriendlyByteBuf buf) {
        byte flag = buf.readByte();
        int color = buf.readInt();
        return new TextStyles(flag,color);
    }

    public float getLength(float scale) {
        float length=0.0F;
        if (this.hasStraightFrame() || this.hasCurveFrame()){
            length += 2*TextStyles.sideFrameGap;
        }
        if (this.hasHighlightColor()){
            length += 2.0F*TextStyles.highlightGap/scale;
        }
        return length;
    }

    public float getHeight(float scale) {
        float height = 0;
        if (this.isUnderline()){
            height += 2*TextStyles.underLineGap/scale;
        }
        if (this.hasCurveFrame() || this.hasStraightFrame()){
            height += 2*TextStyles.upFrameGap;
        }
        if (this.hasHighlightColor()){
            height += 2*TextStyles.highlightGap/scale;
        }
        return height;
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
        private final Rectangle2D semiLeftCircleUVRectangle  = new Rectangle2D.Float(2, 37,(float) (uvCircleDiameter/2),uvCircleDiameter);
        private final Rectangle2D semiRightCircleUVRectangle = new Rectangle2D.Float(10,37,(float) (uvCircleDiameter/2),uvCircleDiameter);

        Format(int offset,int renderOrder) {
            this.offset = offset;
            this.renderOrder = renderOrder;
        }

        public void render(PoseStack stack, VertexConsumer builder, Color color, int combinedLight, Text t, boolean isOnScreen,TextStyles styles) {
            stack.pushPose();
            Matrix4f matrix4f = stack.last().pose();
            switch (this){
                case UNDERLINE:
                    renderTexture(matrix4f,builder,new Rectangle2D.Float(0,t.getHeight(false)+underLineGap,
                            t.getLength(true,false),underLineGap),
                            lineUVRectangle,combinedLight,color,0);
                    break;
                case HIGHLIGHT:
                    RenderSystem.enableBlend();
                    RenderSystem.disableTexture();
                    RenderSystem.defaultBlendFunc();
                    // last parameter is for background layering.
                    // It must be done in real rendering but mustn't be done on Screen (see comment in render function)
                    float length = t.getLength(true,true);
                    float height = t.getHeight(true);
                    renderTexture(matrix4f,builder,
                            new Rectangle2D.Float(
                                    t.offsetX(),
                                    t.offsetY(),
                                    length,
                                    height),
                            lineUVRectangle,combinedLight,styles.highlightColor,isOnScreen?0:2);
                    RenderSystem.disableBlend();
                    RenderSystem.enableTexture();
                    break;
                case FRAMED_CURVE:
                    renderFrame(stack,builder,color,combinedLight,t,true,styles.hasHighlightColor());
                    break;
                case FRAMED_STRAIGHT:
                    renderFrame(stack,builder,color,combinedLight,t,false,styles.hasHighlightColor());
                    break;
            }
            stack.popPose();
        }

        private void renderTexture(Matrix4f matrix4f, VertexConsumer builder, Rectangle2D vertexRectangle, Rectangle2D uvRectangle, int light, Color color,int layer){
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            int alpha = color.getAlpha();
            float z = 0.00001F * layer;
            builder.vertex(matrix4f, (float) vertexRectangle.getMinX(), (float) vertexRectangle.getMinY(),z).color(red,green,blue,alpha).uv((float) uvRectangle.getMinX()/256F, (float) uvRectangle.getMinY()/256F).uv2(light).endVertex();
            builder.vertex(matrix4f, (float) vertexRectangle.getMinX(), (float) vertexRectangle.getMaxY(),z).color(red,green,blue,alpha).uv((float) uvRectangle.getMinX()/256F, (float) uvRectangle.getMaxY()/256F).uv2(light).endVertex();
            builder.vertex(matrix4f, (float) vertexRectangle.getMaxX(), (float) vertexRectangle.getMaxY(),z).color(red,green,blue,alpha).uv((float) uvRectangle.getMaxX()/256F, (float) uvRectangle.getMaxY()/256F).uv2(light).endVertex();
            builder.vertex(matrix4f, (float) vertexRectangle.getMaxX(), (float) vertexRectangle.getMinY(),z).color(red,green,blue,alpha).uv((float) uvRectangle.getMaxX()/256F, (float) uvRectangle.getMinY()/256F).uv2(light).endVertex();
        }

        private void renderFrame(PoseStack stack,VertexConsumer builder,Color color,int combinedLight,Text t,boolean isCurve,boolean hasHighlight){
            stack.translate(-sideFrameGap*t.getScale(),-upFrameGap*t.getScale(),0);
            Matrix4f matrix4f = stack.last().pose();
            float L = t.getLength(true,false) + 2*sideFrameGap*t.getScale(); // sideFrameGap is included in the function todo : change this calculation
            float H = t.getHeight(true)-(hasHighlight ? 2*TextStyles.highlightGap:0); //upFrameGap is included in the function
            float heightGap = isCurve ? H/uvCircleDiameter : 1;
            float sidePartWidth = (isCurve ? (sideFrameGap + overhangGap)*t.getScale() : 1);
            float horLineWidth = L - 2*sidePartWidth;

            renderTexture(matrix4f,builder,new Rectangle2D.Float(0,0,sidePartWidth,H),isCurve?semiLeftCircleUVRectangle:lineUVRectangle,combinedLight,color,1); //left semi circle
            renderTexture(matrix4f,builder,new Rectangle2D.Float(sidePartWidth,0, horLineWidth,heightGap),lineUVRectangle,combinedLight,color,1); //up horizontal line
            renderTexture(matrix4f,builder,new Rectangle2D.Float(sidePartWidth,H-heightGap, horLineWidth,heightGap),lineUVRectangle,combinedLight,color,1); //down horizontal line
            renderTexture(matrix4f,builder,new Rectangle2D.Float(L - sidePartWidth,0,sidePartWidth,H),isCurve?semiRightCircleUVRectangle:lineUVRectangle,combinedLight,color,1); //right semi circle

            stack.translate(sideFrameGap*t.getScale(),upFrameGap*t.getScale(),0);
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
