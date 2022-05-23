package fr.matt1999rd.signs.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.gui.AddTextScreen;
import fr.matt1999rd.signs.enums.Form;
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
            float length = Text.getLength(newText,styles,scale,true);
            int height = 7*scale;
            if (form.hasLengthPredefinedLimit()) {
                if (length <= limitLength) {
                    return super.charTyped(c, p_charTyped_2_);
                }
            } else if (form.rectangleIsIn(xText,xText+length-1,yText,yText+height-1)){
                return super.charTyped(c,p_charTyped_2_);
            }
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

    public void updatePlusButton() {
        float length = Text.getLength(this.getValue(),styles,scale,true);
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AddTextScreen){
            float upperLength = (scale+1)*length/scale;
            int upperHeight = (scale+1)*7/scale;
            AddTextScreen addTextScreen = (AddTextScreen)screen;
            if (form.hasLengthPredefinedLimit()){
                if (scale == 3 || upperLength> limitLength){
                    addTextScreen.disablePlusButton();
                } else {
                    addTextScreen.enablePlusButton();
                }
            } else if (!form.rectangleIsIn(xText,xText+length-1,yText,yText+7*scale-1) && form != Form.PLAIN_SQUARE){
                this.setValue("");
            }else if (!form.rectangleIsIn(xText,xText+upperLength-1,yText,yText+upperHeight-1)){
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
        }else {
            xText = 64 - L / 2F;
            if (init) yText = Form.offsetLetWay;
        }
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bind(BUTTON);
        //button italic
        blit(stack,x,y-11,0,88 + (styles.isItalic()? 10 : 0),10,10);
        //button bold
        blit(stack,x+10,y-11,10,88 + (styles.isBold()? 10 : 0),10,10);
        //button underline
        blit(stack,x+20,y-11,20,88 + (styles.isUnderline()? 10 : 0),10,10);
        //button highlight
        blit(stack,x+30,y-11,30,88 + (styles.hasHighlightColor()? 10 : 0),10,10);
        if (styles.hasHighlightColor()) {
            //button highlight color slider
            blit(stack, x + 40, y - 31, 40, 88, 40, 30);
        }
        //button frames
        blit(stack,x+80,y-11,80,88 + (styles.hasCurveFrame()? 20 : styles.hasStraightFrame()? 10 : 0),10,10);
        super.renderButton(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean mouseClicked = super.mouseClicked(mouseX, mouseY, button);
        Rectangle2D italicButton = new Rectangle2D.Float(x,y-11,10,10);
        Rectangle2D boldButton = new Rectangle2D.Float(x+10,y-11,10,10);
        Rectangle2D underlineButton = new Rectangle2D.Float(x+20,y-11,10,10);
        Rectangle2D highlightButton = new Rectangle2D.Float(x+30,y-11,10,10);
        Rectangle2D frameButton = new Rectangle2D.Float(x+80,y-11,10,10);
        if (!mouseClicked){
            if (italicButton.contains(mouseX,mouseY)){
                if (styles.isItalic())styles = styles.unItalic();
                else styles = styles.italic();
            }
            if (boldButton.contains(mouseX,mouseY)){
                if (styles.isBold())styles = styles.unBold();
                else styles = styles.bold();
            }
            if (underlineButton.contains(mouseX,mouseY)){
                if (styles.isUnderline())styles = styles.unUnderLine();
                else styles = styles.underline();
            }
            if (highlightButton.contains(mouseX,mouseY)){
                if (styles.hasHighlightColor())styles.removeHighlightColor();
                else styles.addHighlightColor(Color.GREEN);
            }
            if (frameButton.contains(mouseX,mouseY)){
                if (styles.hasStraightFrame()){
                    styles = styles.withoutStraightFrame().withCurveFrame();
                }
                else if (styles.hasCurveFrame()){
                    styles = styles.withoutCurveFrame();
                }
                else styles = styles.withStraightFrame();
            }
            System.out.println("mouse clicked action !");
        }
        return mouseClicked;
    }
}
