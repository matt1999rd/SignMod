package fr.mattmouss.signs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.screenutils.TextOption;
import fr.mattmouss.signs.gui.widget.ColorSlider;
import fr.mattmouss.signs.gui.widget.LimitSizeTextField;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;

public class AddTextScreen extends Screen {
    DrawingScreen parentScreen;
    private static final int LENGTH = 197;
    private static final int HEIGHT = 203;
    ResourceLocation BACKGROUND = new ResourceLocation(SignMod.MODID, "textures/gui/add_text_gui.png");
    ResourceLocation DYE_BUTTON = new ResourceLocation(SignMod.MODID, "textures/gui/pencil_buttons.png");

    Button plusButton, moinsButton, addTextButton, cancelButton;
    LimitSizeTextField field;
    ColorSlider RED_SLIDER, GREEN_SLIDER, BLUE_SLIDER;
    ImageButton[] dye_color_button = new ImageButton[16];
    private static TextOption option;

    protected AddTextScreen(DrawingScreen parentScreen) {
        super(new StringTextComponent("Drawing Screen"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        option = TextOption.getDefaultOption();
        int relX = (this.width - LENGTH) / 2;
        int relY = (this.height - HEIGHT) / 2;
        RED_SLIDER = new ColorSlider(relX + 53, relY + 7, option, ColorType.RED);
        GREEN_SLIDER = new ColorSlider(relX + 53, relY + 32, option, ColorType.GREEN);
        BLUE_SLIDER = new ColorSlider(relX + 53, relY + 57, option, ColorType.BLUE);
        this.addButton(RED_SLIDER);
        this.addButton(BLUE_SLIDER);
        this.addButton(GREEN_SLIDER);
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
        cancelButton = new Button(relX + 44, relY + 165, 73, 20, "Cancel", b -> cancel());
        addTextButton = new Button(relX + 44, relY + 142, 73, 20, "Done", b -> addText());
        this.addButton(cancelButton);
        this.addButton(addTextButton);
        super.init();
        field = new LimitSizeTextField(this.minecraft,relX,relY);
        this.addButton(field);
    }



    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(relX, relY,this.blitOffset , 0.0F, 0.0F, LENGTH, HEIGHT, 256, 256);
        super.render(mouseX, mouseY, partialTicks);
    }

    public static void open(DrawingScreen screen){
        Minecraft.getInstance().displayGuiScreen(new AddTextScreen(screen));
    }

    private void fixColor(int color) {
        option.setColor(color,null);
        RED_SLIDER.updateSlider(Functions.getRedValue(color));
        GREEN_SLIDER.updateSlider(Functions.getGreenValue(color));
        BLUE_SLIDER.updateSlider(Functions.getBlueValue(color));
    }

    private void cancel() {
        Minecraft.getInstance().displayGuiScreen(parentScreen);
    }

    private void addText() {
        Form f =parentScreen.form;
        Text newText = new Text(f.getXBegining(option.getLength()),
                f.getYBegining(option.getLength()),
                field.getText(),
                new Color(option.getColor()));
        parentScreen.addOrEditText(newText);
        Minecraft.getInstance().displayGuiScreen(parentScreen);
    }


}
