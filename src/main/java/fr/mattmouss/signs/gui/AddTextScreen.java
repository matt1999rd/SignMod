package fr.mattmouss.signs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.screenutils.Option;
import fr.mattmouss.signs.gui.widget.ColorSlider;
import fr.mattmouss.signs.gui.widget.LimitSizeTextField;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Letter;
import fr.mattmouss.signs.util.Text;
import fr.mattmouss.signs.util.Vec2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

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
        this.DIMENSION = new Vec2i(197,203);
    }

    @Override
    protected void init() {
        int relX = (this.width - DIMENSION.getX()) / 2;
        int relY = (this.height - DIMENSION.getY()) / 2;
        Form f = parentScreen.getForm();
        assert this.minecraft != null;
        field = new LimitSizeTextField(this.minecraft,relX,relY,f,oldText);
        if (f.isForDirection()){
            boolean isEnd = ((DirectionScreen)parentScreen).isEndSelected();
            boolean isTextCentered = ((DirectionScreen)parentScreen).isTextCentered();
            field.defineVariableForDirection(isEnd,isTextCentered);
        }
        field.setValidator(s -> {
            int n= s.length();
            for (int i=0;i<n;i++){
                char c0 = s.charAt(i);
                if (!Letter.isIn(c0) && c0 != ' '){
                    return false;
                }
                if (f.isForDirection()){
                    DirectionScreen screen = (DirectionScreen)parentScreen;
                    if (screen.isEndSelected() && !Letter.isNumber(c0)){
                        return false;
                    }
                }
            }
            return true;
        });

        super.init();
        cancelButton = new Button(relX + 44, relY + 165, 74, 20, "Cancel", b -> cancel());
        addTextButton = new Button(relX + 44, relY + 142, 74, 20, "Done", b -> addText());
        plusButton = new Button(relX + 162,relY+109,21,20,"+",b->changeScale(true));
        minusButton = new Button(relX+162,relY+129,21,20,"-",b->changeScale(false));
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
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(relX, relY,this.blitOffset , 0.0F, 0.0F, DIMENSION.getX(), DIMENSION.getY(), 256, 256);
        super.render(mouseX, mouseY, partialTicks);
        //AbstractGui.fill(relX + 143, relY + 93, relX + 143 + 9, relY + 93 + 9, field.getColor());
        int gap = (field.getScale()>9) ? 6:0;
        this.drawString(Minecraft.getInstance().fontRenderer,""+field.getScale(),relX+144-gap,relY+126,white);
    }

    void fixColor(int color) {
        Option option = getColorOption();
        option.setColor(color,null);
        ColorSlider[] sliders = getActiveSliders();
        sliders[0].updateSlider(Functions.getRedValue(color));
        sliders[1].updateSlider(Functions.getGreenValue(color));
        sliders[2].updateSlider(Functions.getBlueValue(color));
    }

    //@Override
    Vec2i getDyeButtonsBeginning() {
        return new Vec2i(36,18);
    }

    public static void open(IWithEditTextScreen screen,Text text){
        Minecraft.getInstance().displayGuiScreen(new AddTextScreen(screen,text));
    }

    public static void open(IWithEditTextScreen screen){
        Minecraft.getInstance().displayGuiScreen(new AddTextScreen(screen,null));
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
    Vec2i getColorDisplayBeginning() {
        return new Vec2i(143,93);
    }

    private void cancel() {
        Minecraft.getInstance().displayGuiScreen(parentScreen.getScreen());
    }

    private void addText() {
        if (field.getText().equals("") && parentScreen instanceof DrawingScreen){
            cancel();
            return;
        }
        Text newText = new Text(field.getX(),
                field.getY(),
                field.getText(),
                new Color(field.getColor()),field.getScale());
        Minecraft.getInstance().displayGuiScreen(parentScreen.getScreen());
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
