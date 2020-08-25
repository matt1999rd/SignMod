package fr.mattmouss.signs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.widget.ColorSlider;
import fr.mattmouss.signs.gui.widget.LimitSizeTextField;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Letter;
import fr.mattmouss.signs.util.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;

public class AddTextScreen extends Screen {
    IWithEditTextScreen parentScreen;
    Text oldText;
    private static final int LENGTH = 197;
    private static final int HEIGHT = 203;
    private static final int white =Color.WHITE.getRGB();
    ResourceLocation BACKGROUND = new ResourceLocation(SignMod.MODID, "textures/gui/add_text_gui.png");
    ResourceLocation DYE_BUTTON = new ResourceLocation(SignMod.MODID, "textures/gui/pencil_buttons.png");

    Button plusButton, moinsButton, addTextButton, cancelButton;
    LimitSizeTextField field;
    ColorSlider RED_SLIDER, GREEN_SLIDER, BLUE_SLIDER;
    ImageButton[] dye_color_button = new ImageButton[16];

    protected AddTextScreen(IWithEditTextScreen parentScreen,Text textToEdit) {
        super(new StringTextComponent("Drawing Screen"));
        this.parentScreen = parentScreen;
        oldText = textToEdit;
    }

    @Override
    protected void init() {
        int relX = (this.width - LENGTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        Form f = parentScreen.getForm();
        field = new LimitSizeTextField(this.minecraft,relX,relY,f,oldText);
        field.setValidator(s -> {
            int n= s.length();
            for (int i=0;i<n;i++){
                char c0 = s.charAt(i);
                if (!Letter.isIn(c0) && c0 != ' '){
                    return false;
                }
            }
            return true;
        });


        RED_SLIDER = new ColorSlider(relX + 53, relY + 7, field, ColorType.RED);
        GREEN_SLIDER = new ColorSlider(relX + 53, relY + 32, field, ColorType.GREEN);
        BLUE_SLIDER = new ColorSlider(relX + 53, relY + 57, field, ColorType.BLUE);

        int x_dye_begining = relX + 36;
        int y_dye_begining = relY + 18;
        int dye_button_length = 6;
        for (DyeColor color : DyeColor.values()) {
            int i = color.getId();
            dye_color_button[i] = new ImageButton(
                    x_dye_begining + i % 2 * 6,
                    y_dye_begining + i / 2 * 6,
                    dye_button_length, dye_button_length,
                    6 * 25 + i * dye_button_length,
                    0,
                    dye_button_length, DYE_BUTTON,
                    b -> {
                        fixColor(color.getColorValue());
                    });
            this.addButton(dye_color_button[i]);
        }
        cancelButton = new Button(relX + 44, relY + 165, 74, 20, "Cancel", b -> cancel());
        addTextButton = new Button(relX + 44, relY + 142, 74, 20, "Done", b -> addText());
        plusButton = new Button(relX + 162,relY+109,21,20,"+",b->changeScale(true));
        moinsButton = new Button(relX+162,relY+129,21,20,"-",b->changeScale(false));
        moinsButton.active = (field.getScale() != 1);
        if (oldText != null)field.updatePlusButton();
        this.addButton(cancelButton);
        this.addButton(addTextButton);
        this.addButton(RED_SLIDER);
        this.addButton(BLUE_SLIDER);
        this.addButton(GREEN_SLIDER);
        this.addButton(plusButton);
        this.addButton(moinsButton);
        this.addButton(field);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(relX, relY,this.blitOffset , 0.0F, 0.0F, LENGTH, HEIGHT, 256, 256);
        super.render(mouseX, mouseY, partialTicks);
        int gap = (field.getScale()>9) ? 6:0;
        this.drawString(Minecraft.getInstance().fontRenderer,""+field.getScale(),relX+144-gap,relY+126,white);
        AbstractGui.fill(relX + 143, relY + 93, relX + 143 + 9, relY + 93 + 9, field.getColor(null));
    }

    public static void open(IWithEditTextScreen screen,Text text){
        Minecraft.getInstance().displayGuiScreen(new AddTextScreen(screen,text));
    }

    public static void open(IWithEditTextScreen screen){
        Minecraft.getInstance().displayGuiScreen(new AddTextScreen(screen,null));
    }

    private void fixColor(int color) {
        field.setColor(color,null);
        RED_SLIDER.updateSlider(Functions.getRedValue(color));
        GREEN_SLIDER.updateSlider(Functions.getGreenValue(color));
        BLUE_SLIDER.updateSlider(Functions.getBlueValue(color));
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
                new Color(field.getColor(null)),field.getScale());
        parentScreen.addOrEditText(newText);
        Minecraft.getInstance().displayGuiScreen(parentScreen.getScreen());
    }

    private void changeScale(boolean incr){
        if (field.getScale() == 2 && !incr){
            moinsButton.active = false;
        }else if (field.getScale() == 1 && incr){
            moinsButton.active = true;
        }
        field.incrementScale(incr);
        field.updatePlusButton();
    }

    /** for textfield update done when text is written **/

    public void disablePlusButton() {
        if (plusButton.active)plusButton.active = false;
    }

    public void enablePlusButton(){
        if (!plusButton.active)plusButton.active = true;
    }
}
