package fr.matt1999rd.signs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.gui.screenutils.ColorType;
import fr.matt1999rd.signs.gui.screenutils.Option;
import fr.matt1999rd.signs.gui.widget.ColorSlider;
import fr.matt1999rd.signs.gui.widget.LimitSizeTextField;
import fr.matt1999rd.signs.util.Letter;
import fr.matt1999rd.signs.util.Text;
import fr.matt1999rd.signs.util.Vector2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import static fr.matt1999rd.signs.util.DirectionSignConstants.*;
import static net.minecraft.util.text.ITextComponent.nullToEmpty;

import java.awt.*;

public class AddTextScreen extends withColorSliderScreen {
    IWithEditTextScreen parentScreen;
    Text oldText;
    private static final int white =Color.WHITE.getRGB();
    ResourceLocation BACKGROUND = new ResourceLocation(SignMod.MODID, "textures/gui/add_text_gui.png");

    Button plusButton, minusButton, addTextButton, cancelButton;
    LimitSizeTextField field;
    ColorSlider RED_SLIDER, GREEN_SLIDER, BLUE_SLIDER;

    protected AddTextScreen(IWithEditTextScreen parentScreen,Text textToEdit) {
        super(new StringTextComponent("Add Text Screen"));
        this.parentScreen = parentScreen;
        oldText = textToEdit;
        this.DIMENSION = new Vector2i(197,203);
    }

    @Override
    protected void init() {
        int relX = (this.width - DIMENSION.getX()) / 2;
        int relY = (this.height - DIMENSION.getY()) / 2;
        Form f = parentScreen.getForm();
        assert this.minecraft != null;
        field = new LimitSizeTextField(this.minecraft,relX+30,relY+118,f,oldText);
        if (f.hasLengthPredefinedLimit()){
            int maxLength ;
            if (f.isForDirection()){
                boolean isTextCentered = ((DirectionScreen)parentScreen).isTextCentered();
                boolean isEnd = ((DirectionScreen)parentScreen).isEndSelected();
                maxLength = (isTextCentered) ? horPixelNumber-2*sideGapPixelNumber : (isEnd) ? endTextPixelNumber : begTextPixelNumber;
            } else {
                maxLength = ((PlainSquareScreen) parentScreen).getMaxLength();
            }
            field.setLengthLimit(maxLength);
        }
        field.setFilter(s -> {
            if (!Letter.VALIDATOR_FOR_TEXT_DISPLAY.test(s)){
                return false;
            }
            if (f.isForDirection()){
                int n= s.length();
                for (int i=0;i<n;i++){
                    char c0 = s.charAt(i);
                    DirectionScreen screen = (DirectionScreen)parentScreen;
                    if (screen.isEndSelected() && !Letter.isNumber(c0)){
                        return false;
                    }
                }
            }
            return true;
        });

        super.init();
        cancelButton = new Button(relX + 44, relY + 165, 74, 20, nullToEmpty("Cancel"), b -> cancel());
        addTextButton = new Button(relX + 44, relY + 142, 74, 20, nullToEmpty("Done"), b -> addText());
        plusButton = new Button(relX + 162,relY+109,21,20,nullToEmpty("+"),b->changeScale(true));
        minusButton = new Button(relX+162,relY+129,21,20,nullToEmpty("-"),b->changeScale(false));
        minusButton.active = (field.getScale() != 1);
        if (oldText != null)field.updatePlusButton();
        this.addButton(cancelButton);
        this.addButton(addTextButton);
        this.addButton(plusButton);
        this.addButton(minusButton);
        this.addButton(field);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(MatrixStack stack,int mouseX, int mouseY, float partialTicks) {
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bind(BACKGROUND);
        blit(stack,relX, relY,this.getBlitOffset() , 0.0F, 0.0F, DIMENSION.getX(), DIMENSION.getY(), 256, 256);
        super.render(stack,mouseX, mouseY, partialTicks);
        //AbstractGui.fill(relX + 143, relY + 93, relX + 143 + 9, relY + 93 + 9, field.getColor());
        int gap = (field.getScale()>9) ? 6:0;
        drawString(stack,Minecraft.getInstance().font,""+field.getScale(),relX+144-gap,relY+126,white);
    }

    //@Override
    Vector2i getDyeButtonsBeginning() {
        return new Vector2i(36,18);
    }

    public static void open(IWithEditTextScreen screen,Text text){
        Minecraft.getInstance().setScreen(new AddTextScreen(screen,text));
    }

    public static void open(IWithEditTextScreen screen){
        Minecraft.getInstance().setScreen(new AddTextScreen(screen,null));
    }

    Option getColorOption() {
        return field;
    }

    @Override
    ColorSlider[] getActiveSliders() {
        return new ColorSlider[]{RED_SLIDER, GREEN_SLIDER, BLUE_SLIDER};
    }

    @Override
    void initSlider() {
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        RED_SLIDER = new ColorSlider(relX + 53, relY + 7, field, ColorType.RED,135);
        GREEN_SLIDER = new ColorSlider(relX + 53, relY + 32, field, ColorType.GREEN,135);
        BLUE_SLIDER = new ColorSlider(relX + 53, relY + 57, field, ColorType.BLUE,135);
        this.addButton(RED_SLIDER);
        this.addButton(BLUE_SLIDER);
        this.addButton(GREEN_SLIDER);
    }

    protected int getGuiStartXPosition(){
        return (this.width-DIMENSION.getX()) / 2;
    }

    protected int getGuiStartYPosition(){
        return (this.height-DIMENSION.getY()) / 2;
    }

    @Override
    boolean renderColor() {
        return true;
    }

    @Override
    Vector2i getColorDisplayBeginning() {
        return new Vector2i(143,93);
    }

    private void cancel() {
        Minecraft.getInstance().setScreen(parentScreen.getScreen());
    }

    private void addText() {
        if (field.getValue().equals("") && parentScreen instanceof DrawingScreen){
            cancel();
            return;
        }
        Text newText = new Text(field.getX(),
                field.getY(),
                field.getValue(),
                new Color(field.getColor()),field.getScale());
        Minecraft.getInstance().setScreen(parentScreen.getScreen());
        parentScreen.addOrEditText(newText);
    }

    private void changeScale(boolean increment){
        if (field.getScale() == 2 && !increment){
            minusButton.active = false;
        }else if (field.getScale() == 1 && increment){
            minusButton.active = true;
        }
        field.incrementScale(increment);
        field.updatePlusButton();
    }

    /** for text file update done when text is written **/

    public void disablePlusButton() {
        if (plusButton.active)plusButton.active = false;
    }

    public void enablePlusButton(){
        if (!plusButton.active)plusButton.active = true;
    }
}
