package fr.mattmouss.signs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.PSDisplayMode;
import fr.mattmouss.signs.gui.screenutils.ColorOption;
import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.screenutils.Option;
import fr.mattmouss.signs.gui.widget.ColorSlider;
import fr.mattmouss.signs.tileentity.primary.PlainSquareSignTileEntity;
import fr.mattmouss.signs.util.Vec2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.awt.Color;

public class PlainSquareScreen extends withColorSliderScreen {
    private static final Vec2i displayModeBtnStart = new Vec2i(241,113);
    private static final Vec2i displayModeBtnStop = new Vec2i(265,137);

    private boolean isBgColorDisplayed = true;

    BlockPos panelPos;
    private static ColorOption backgroundColorOption;
    private static ColorOption edgingColorOption;

    ColorSlider[] sliders = new ColorSlider[6];
    ResourceLocation PLAIN_SQUARE = new ResourceLocation(SignMod.MODID,"textures/gui/ps_gui.png");
    ResourceLocation PS_SCHEME = new ResourceLocation(SignMod.MODID,"textures/gui/display_mode_scheme.png");
    ImageButton[] psDisplayModeButton = new ImageButton[4];
    ImageButton[] arrowDirectionButton = new ImageButton[3];
    Button applyColorButton;


    protected PlainSquareScreen(BlockPos panelPos) {
        super(new StringTextComponent("Plain Square screen"));
        this.panelPos = panelPos;
        this.DIMENSION = new Vec2i(424,149);
    }

    @Override
    Vec2i getDyeButtonsBeginning() {
        return new Vec2i(185,46);
    }

    @Override
    Option getColorOption() {
        return (isBgColorDisplayed)? backgroundColorOption : edgingColorOption;
    }

    private PlainSquareSignTileEntity getTileEntity(){
        assert this.minecraft != null;
        World world = this.minecraft.world;
        TileEntity te = world.getTileEntity(panelPos);
        if (te instanceof PlainSquareSignTileEntity){
            return (PlainSquareSignTileEntity) te;
        }
        if (te == null)throw new NullPointerException("Tile entity at panelPos is not in desired place !");
        throw new IllegalStateException("Plain Square Screen need plain square sign tile entity in place !");
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
            sliders[i] = new ColorSlider(guiLeft+211,guiTop+35+i%3*25,opt, ColorType.byIndex(i%3),93);
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
        int ARROW_BUTTON_LENGTH = BUTTON_LENGTH -1;
        int guiLeft = getGuiStartXPosition();
        int guiTop = getGuiStartYPosition();
        for (int i=0;i<4;i++){
            int finalI = i;
            psDisplayModeButton[i] =  new ImageButton(
                    guiLeft+149, guiTop+6+BUTTON_LENGTH*i, //position on gui
                    BUTTON_LENGTH, BUTTON_LENGTH, //dimension of the button
                    i*BUTTON_LENGTH,150,BUTTON_LENGTH,//mapping on button texture (uv and v for hovered mode)
                    PLAIN_SQUARE, // texture resource
                    512,256, //texture total length
                    button -> changeDisplayMode(finalI) ) ; //the action to do when clicking on the button
            this.addButton(psDisplayModeButton[i]);
        }

        for (int i=0;i<3;i++){
            int finalI = i;
            arrowDirectionButton[i] = new ImageButton(
                    guiLeft+211+i*(BUTTON_LENGTH+8),guiTop+6,
                    BUTTON_LENGTH, ARROW_BUTTON_LENGTH,
                    100+i*BUTTON_LENGTH,150,ARROW_BUTTON_LENGTH,
                    PLAIN_SQUARE,
                    512,256,
                    button -> changeArrowDirection(finalI) );
            this.addButton(arrowDirectionButton[i]);
        }

        applyColorButton = new Button(guiLeft+148,guiTop+116,74,20,"Apply Color", button->applyColor());
        this.addButton(applyColorButton);
    }

    private void changeDisplayMode(int i){
        // the function to modify the display mode of the plain square (not available for 2*2 panel)
        System.out.println("changing display mode : "+ i);
    }

    private void changeArrowDirection(int i){
        //the function to modify the arrow for the display mode
        System.out.println("changing arrow direction : "+ i);
    }

    private void applyColor(){
        //the function to modify the color of background or foreground
        System.out.println("Apply the color !");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(PLAIN_SQUARE);
        blit(relX, relY,this.blitOffset , 0.0F, 0.0F, DIMENSION.getX(), DIMENSION.getY(), 256, 512);
        int offset = (isBgColorDisplayed)? 25:0;
        //display of button for the color to define (using color slider) choice between background and foreground color
        blit(relX+241,relY+113,this.blitOffset,DIMENSION.getX()+offset,0,25,25,256,512);
        super.render(mouseX, mouseY, partialTicks);
        PlainSquareSignTileEntity psste = getTileEntity();
        psste.renderOnScreen(relX+10,relY+10);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiLeft = getGuiStartXPosition();
        int guiTop = getGuiStartYPosition();
        if (mouseX>guiLeft+ displayModeBtnStart.getX() &&
                mouseX<guiLeft+ displayModeBtnStop.getX() &&
                mouseY>guiTop+ displayModeBtnStart.getY() &&
                mouseY<guiTop+ displayModeBtnStop.getY()){
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
