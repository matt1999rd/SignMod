package fr.matt1999rd.signs.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.gui.AddTextScreen;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.gui.PlainSquareScreen;
import fr.matt1999rd.signs.gui.screenutils.ColorType;
import fr.matt1999rd.signs.gui.screenutils.Option;
import fr.matt1999rd.signs.util.Letter;
import fr.matt1999rd.signs.util.Text;
import fr.matt1999rd.signs.util.TextStyles;
import net.minecraft.client.Minecraft;
import static fr.matt1999rd.signs.util.Functions.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LimitSizeTextField extends TextFieldWidget implements Option {
    float xText,yText;
    Form form;
    int scale;
    Color color;
    TextStyles styles ;
    //boolean isEnd,isTextCentered;
    int limitLength;
    ResourceLocation BUTTON = new ResourceLocation(SignMod.MODID, "textures/gui/buttons.png");

    public LimitSizeTextField(Minecraft mc, int relX, int relY, Form form,@Nullable Text oldText) {
        super(mc.font, relX, relY, 90, 12, ITextComponent.nullToEmpty(" "));
        float xText= (oldText != null) ? oldText.getX(false) : form.getXBeginning(7); //todo : same problem as found in keyPressed function in DrawingScreen
        float yText= (oldText != null) ? oldText.getY(false) : form.getYBeginning(7);
        int scale = (oldText != null) ? oldText.getScale() : 1;
        String text = (oldText != null) ? oldText.getText() : "";
        color = (oldText != null) ? new Color(oldText.getColor(),true) : Color.WHITE;
        styles = (oldText != null) ? oldText.getStyles() : TextStyles.defaultStyle();
        this.setValue(text);
        this.xText = xText;
        this.yText = yText;
        this.form = form ;
        this.scale = scale;

        this.setCanLoseFocus(false);
        this.setFocus(true);
        onUpdate(true);
    }


    public void setLengthLimit(int maxLength){
        this.limitLength = maxLength;
    }

    public float getX() {
        return xText;
    }

    public float getY() {
        return yText;
    }

    public int getScale() {
        return scale;
    }

    public TextStyles getStyles() { return styles; }

    public void incrementScale(boolean increase) {
        if (increase){
            scale++;
        }else {
            scale--;
        }
        if (form == Form.UPSIDE_TRIANGLE){
            xText = 5+MathHelper.ceil(3.5f*scale-0.5);
        }
    }


    @Override
    public boolean charTyped(char c, int p_charTyped_2_) {
        String text = this.getValue();
        if (Letter.isIn(c) || c == ' '){
            String newText = text+c;
            boolean charIsWritable = checkLimit(newText,styles);
            if (charIsWritable)return super.charTyped(c,p_charTyped_2_);
        }
        return false;
    }

    public boolean checkLimit(String newText,TextStyles newStyles){
        float length = Text.getLength(newText,newStyles,scale,true);
        int height = 7*scale;
        if (form.hasLengthPredefinedLimit()) {
            if (length <= limitLength) {
                return true;
            }
        } else if (form.rectangleIsIn(xText+newStyles.offsetX(scale),xText+newStyles.offsetX(scale)+length-1,yText+newStyles.offsetY(scale),yText+newStyles.offsetY(scale)+height-1)){
            return true;
        }
        return false;
    }

    @Override
    public void insertText(String textToWrite) {
        super.insertText(textToWrite);
        onUpdate(false);
    }

    @Override
    public void deleteChars(int num) {
        super.deleteChars(num);
        onUpdate(false);
    }

    @Override
    public void deleteWords(int num) {
        super.deleteWords(num);
        onUpdate(false);
    }



    public void setColor(int newColor,ColorType type) {
        int rColor = color.getRed();
        int gColor = color.getGreen();
        int bColor = color.getBlue();
        if (type != null) {
            if (newColor > 255) {
                newColor = 255;
            }
            switch (type) {
                case RED:
                    rColor = newColor;
                    break;
                case BLUE:
                    bColor = newColor;
                    break;
                case GREEN:
                    gColor = newColor;
                    break;
                default:
                    break;
            }
        }else {
            rColor = getRedValue(newColor);
            if (rColor>255)rColor=255;
            gColor = getGreenValue(newColor);
            if (gColor>255)gColor=255;
            bColor = getBlueValue(newColor);
            if (bColor>255)bColor=255;
        }
        this.color = new Color(rColor, gColor, bColor, 255);
    }

    public int getColor(ColorType type){
        switch (type){
            case RED:
                return color.getRed();
            case BLUE:
                return color.getBlue();
            case GREEN:
                return color.getGreen();
            default:
                return -1;
        }
    }

    @Override
    public int getColor() {
        return color.getRGB();
    }

    public void onUpdate(boolean init){
        if (!init)updatePlusButton();
        updateTextPosition(init);
    }

    private void onStyleChanged(){
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof PlainSquareScreen){
            ((PlainSquareScreen) screen).setStyles(styles);
        }
    }

    public void updatePlusButton() {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AddTextScreen){
            float length = Text.getLength(this.getValue(),styles,scale,true);
            float upperLength = (scale+1)*length/scale;
            int upperHeight = (scale+1)*7/scale;
            AddTextScreen addTextScreen = (AddTextScreen)screen;
            if (form.hasLengthPredefinedLimit()){
                if (scale == 3 || upperLength> limitLength){
                    addTextScreen.disablePlusButton();
                } else {
                    addTextScreen.enablePlusButton();
                }
            } else if (!form.rectangleIsIn(xText + styles.offsetX(scale),xText+styles.offsetX(scale) + length-1,
                    yText+ styles.offsetY(scale),yText+styles.offsetY(scale) + 7*scale-1) && form != Form.PLAIN_SQUARE){
                this.setValue("");
            }else if (!form.rectangleIsIn(xText + styles.offsetX(scale),xText+styles.offsetX(scale)+upperLength-1,
                    yText + styles.offsetY(scale),yText + styles.offsetY(scale) +upperHeight-1)){
                addTextScreen.disablePlusButton();
            }else {
                addTextScreen.enablePlusButton();
            }
        }
    }

    public void updateTextPosition(boolean init){
        if (form.isNotForEditing())return;
        float L = Text.getLength(this.getValue(),styles,scale,true);
        if (form == Form.OCTAGON) {
            int H = 7*scale;
            xText = 64 - L / 2F;
            yText = 64 - H / 2F;
        }else { //let way form
            xText = 64 - L / 2F;
            if (init) yText = Form.offsetLetWay;
        }
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bind(BUTTON);
        for (TextStyles.Format format : TextStyles.Format.values()){
            if (!format.isFrame() || this.styles.is(format)) {
                blit(stack, x + format.getUXOffset(), y - 11, format.getUXOffset(), 88 + format.getVOffset(styles), 10, 10);
            }
        }
        if (styles.hasHighlightColor()) {
            //button highlight color slider
            blit(stack, x + 40, y - 31, 40, 88, 40, 30);
            Color highlightColor = styles.getHighlightColor();
            //slider part
            blit(stack, x + 41 + 36*highlightColor.getRed()  /255, y - 30,90,88,3,8); //red
            blit(stack, x + 41 + 36*highlightColor.getGreen()/255, y - 20,90,88,3,8); //green
            blit(stack, x + 41 + 36*highlightColor.getBlue() /255, y - 10,90,88,3,8); //blue
            //background color rendering
            Rectangle2D hRectangle = new Rectangle2D.Float(x+TextStyles.Format.HIGHLIGHT.getUXOffset() + 1,y-10,8,8);
            //vertical lines
            fill(stack,           (int)hRectangle.getMinX()  ,(int)hRectangle.getMinY(),(int)hRectangle.getMinX()+2,(int)hRectangle.getMaxY(),highlightColor.getRGB());
            fill(stack,(int)hRectangle.getMaxX()-2,(int)hRectangle.getMinY(),            (int)hRectangle.getMaxX()  ,(int)hRectangle.getMaxY(),highlightColor.getRGB());
            //horizontal lines
            fill(stack,(int)hRectangle.getMinX(),            (int)hRectangle.getMinY(),(int)hRectangle.getMaxX(),(int)hRectangle.getMinY()+1,highlightColor.getRGB());
            fill(stack,(int)hRectangle.getMinX(),(int)hRectangle.getMaxY()-1,(int)hRectangle.getMaxX(),           (int)hRectangle.getMaxY() ,highlightColor.getRGB());
            //H gaps
            fill(stack,(int)hRectangle.getMinX()+3, (int)hRectangle.getMinY()+1,(int)hRectangle.getMaxX()-3, (int)hRectangle.getMinY()+3,highlightColor.getRGB());
            fill(stack,(int)hRectangle.getMinX()+3, (int)hRectangle.getMaxY()-3,(int)hRectangle.getMaxX()-3, (int)hRectangle.getMaxY()-1,highlightColor.getRGB());
        }
        super.renderButton(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected boolean clicked(double p_230992_1_, double p_230992_3_) {
        return this.visible && this.active;
    }

    private boolean onSliderClicked(double mouseX, double mouseY){
        Rectangle2D sliderButton = new Rectangle2D.Float( x + 41,y-30,38,28);
        if (sliderButton.contains(mouseX,mouseY)){
            double relMouseX = mouseX - sliderButton.getMinX();
            double relMouseY = mouseY - sliderButton.getMinY();
            int index = MathHelper.clamp(MathHelper.fastFloor(relMouseX),1,37) - 1;
            int component = (int) (index/36.0F*255);
            Color color = styles.getHighlightColor();
            if (relMouseY <= 8){
                styles.setHighlightColor(new Color(component,color.getGreen(),color.getBlue()));
            }else if (relMouseY <= 18 && relMouseY >= 10){
                styles.setHighlightColor(new Color(color.getRed(),component,color.getBlue()));
            }else if (relMouseY >= 20){
                styles.setHighlightColor(new Color(color.getRed(),color.getGreen(),component));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Rectangle2D frameButton = new Rectangle2D.Float(x + TextStyles.Format.FRAMED_CURVE.getUXOffset(),y-11,10,10);
        boolean formatChanged = false;
        for (TextStyles.Format format : Arrays.stream(TextStyles.Format.values()).filter(format -> !format.isFrame()).collect(Collectors.toList())){
            Rectangle2D formatButton = new Rectangle2D.Float(x+format.getUXOffset(),y-11,10,10);
            if (formatButton.contains(mouseX,mouseY)){
                if (styles.is(format))styles.removeFormat(format);
                else styles.addFormat(format);
                formatChanged = true;
            }
        }
        if (frameButton.contains(mouseX,mouseY)){
            if (styles.hasStraightFrame()){
                styles = styles.withoutStraightFrame().withCurveFrame();
            }else if(styles.hasCurveFrame()){
                styles = styles.withoutCurveFrame();
            }else {
                if (checkLimit(this.getValue(),TextStyles.copy(styles).withStraightFrame())){
                    //todo : function not working correctly : no offset taken into account
                    styles = styles.withStraightFrame();
                }
            }
            formatChanged = true;
        }
        if (formatChanged || onSliderClicked(mouseX,mouseY))onStyleChanged();
        super.mouseClicked(mouseX, mouseY,button);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY,int button, double oldMouseX, double oldMouseY) {
        if (onSliderClicked(mouseX,mouseY))onStyleChanged();
        return super.mouseDragged(mouseX, mouseY, button, oldMouseX, oldMouseY);
    }

    public void setText(Text text){
        this.setValue(text.getText());
        this.styles = text.getStyles();
    }
}
