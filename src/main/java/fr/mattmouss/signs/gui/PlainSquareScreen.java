package fr.mattmouss.signs.gui;

import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.gui.screenutils.ColorOption;
import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.screenutils.Option;
import fr.mattmouss.signs.gui.widget.ColorSlider;
import fr.mattmouss.signs.util.Vec2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;

public class PlainSquareScreen extends withColorSliderScreen {
    //protected static final Vec2i DIMENSION = new Vec2i(424,148);
    private static final int[] bgEdgingColorModeButtonXLimit= new int[]{241,265};
    private static final int[] bgEdgingColorModeButtonYLimit= new int[]{113,137};
    private boolean isBgColorDisplayed = true;

    BlockPos panelPos;
    private static ColorOption backgroundColorOption;
    private static ColorOption edgingColorOption;

    ColorSlider[] sliders = new ColorSlider[6];
    ResourceLocation PLAIN_SQUARE = new ResourceLocation(SignMod.MODID,"textures/gui/ps_gui.png");
    ImageButton[] psDisplayModeButton = new ImageButton[4];
    ImageButton[] arrowDirectionButton = new ImageButton[3];
    Button applyColorButton,DoneButton;


    protected PlainSquareScreen(BlockPos panelPos) {
        super(new StringTextComponent("Plain Square screen"));
        this.panelPos = panelPos;
        this.DIMENSION = new Vec2i(424,148);

    }

    @Override
    Vec2i getDyeButtonsBeginning() {
        return new Vec2i(185,46);
    }

    @Override
    Option getColorOption() {
        return (isBgColorDisplayed)? backgroundColorOption : edgingColorOption;
    }

    @Override
    ColorSlider[] getActiveSliders() {
        if (isBgColorDisplayed){
            return new ColorSlider[]{sliders[0], sliders[1], sliders[2]};
        }else {
            return new ColorSlider[]{sliders[3], sliders[4], sliders[5]};
        }
    }

    @Override
    void initSlider() {
        backgroundColorOption = new ColorOption(Color.WHITE);
        edgingColorOption = new ColorOption(Color.BLACK);
        int guiLeft = getGuiStartXPosition();
        int guiTop = getGuiStartYPosition();
        for (int i=0;i<6;i++){
            ColorOption opt = (i<3)? backgroundColorOption : edgingColorOption;
            sliders[i] = new ColorSlider(guiLeft+323,guiTop+7+i%3*25,opt, ColorType.byIndex(i%3),93);
            addButton(sliders[i]);
        }

        updateSliderDisplay();
    }

    @Override
    boolean renderColor() {
        return true;
    }

    @Override
    Vec2i getColorDisplayBeginning() {
        return new Vec2i(283,121);
    }

    @Override
    protected void init(){
        super.init();
        int BUTTON_LENGTH = 25;
        int guiLeft = getGuiStartXPosition();
        int guiTop = getGuiStartYPosition();
        for (int i=0;i<4;i++){
            int finalI = i;
            psDisplayModeButton[i] =  new ImageButton(
                    guiLeft+149, guiTop+6, //position on gui
                    BUTTON_LENGTH, BUTTON_LENGTH, //dimension of the button
                    0,172,BUTTON_LENGTH,//mapping on button texture (uv and v for hovered mode)
                    PLAIN_SQUARE, // texture resource
                    button -> changeDisplayMode(finalI) ) ; //the action to do when clicking on the button
            this.addButton(psDisplayModeButton[i]);
        }
        for (int i=0;i<3;i++){
            arrowDirectionButton[i] = new ImageButton(
                    guiLeft+
            )
        }
    }

    private void changeDisplayMode(int i){
        // the function to modify the display mode of the plain square (not available for 2*2 panel)
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiLeft = getGuiStartXPosition();
        int guiTop = getGuiStartYPosition();
        if (mouseX>guiLeft+bgEdgingColorModeButtonXLimit[0] &&
                mouseX<guiLeft+bgEdgingColorModeButtonXLimit[1] &&
                mouseY>guiTop+bgEdgingColorModeButtonYLimit[0] &&
                mouseY<guiTop+bgEdgingColorModeButtonYLimit[1]){
            isBgColorDisplayed = !isBgColorDisplayed;
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            updateSliderDisplay();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    private void updateSliderDisplay() {
        for (int k=0;k<6;k++){
            boolean visAndAct = (isBgColorDisplayed) == (k<3);
            sliders[k].visible = visAndAct;
            sliders[k].active = visAndAct;
        }
    }

    public static void open(BlockPos panelPos){
        Minecraft.getInstance().displayGuiScreen(new PlainSquareScreen(panelPos));
    }
}
