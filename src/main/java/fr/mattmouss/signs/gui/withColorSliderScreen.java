package fr.mattmouss.signs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.gui.screenutils.Option;
import fr.mattmouss.signs.gui.widget.ColorSlider;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Vec2i;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class withColorSliderScreen extends Screen {
    ImageButton[] dye_color_button= new ImageButton[16];
    ResourceLocation COLOR_BUTTONS = new ResourceLocation(SignMod.MODID,"textures/gui/buttons.png");
    protected Vec2i DIMENSION;
    protected withColorSliderScreen(ITextComponent titleIn) {
        super(titleIn);
    }

    void fixColor(int color) {
        Option option = getColorOption();
        option.setColor(color,null);
        ColorSlider[] sliders = getActiveSliders();
        sliders[0].updateSlider(Functions.getRedValue(color));
        sliders[1].updateSlider(Functions.getGreenValue(color));
        sliders[2].updateSlider(Functions.getBlueValue(color));
    }

    abstract Vec2i getDyeButtonsBeginning();
    abstract Option getColorOption();
    abstract ColorSlider[] getActiveSliders();
    abstract void initSlider();
    abstract boolean renderColor();
    abstract Vec2i getColorDisplayBeginning();
    protected Vec2i getGuiDimension(){
        return DIMENSION;
    }

    protected int getGuiStartXPosition(){
        return (this.width-getGuiDimension().getX()) / 2;
    }

    protected int getGuiStartYPosition(){
        return (this.height-getGuiDimension().getY()) / 2;
    }



    @Override
    protected void init() {
        // making the dye button : a shortcut that move slider
        // to define the classic 16 dyes colors found in minecraft
        int dye_button_length = 6;
        Vec2i dye_button_beginning = getDyeButtonsBeginning();
        int LENGTH = getGuiDimension().getX();
        int HEIGHT = getGuiDimension().getY();
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        for (DyeColor color : DyeColor.values()){
            int i=color.getId();
            dye_color_button[i] = new ImageButton(
                    relX + dye_button_beginning.getX()+i%2*6,
                    relY + dye_button_beginning.getY()+i/2*6,
                    dye_button_length,dye_button_length,
                    6*25+i*dye_button_length,
                    0,
                    dye_button_length,COLOR_BUTTONS,
                    b-> fixColor(color.getColorValue()));
            this.addButton(dye_color_button[i]);
        }
        // making the three sliders Red Blue Green that can be moved on screen
        // to apply a color in the dedicated tile entity
        initSlider();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        Vec2i guiDimension = getGuiDimension();
        int colorDisplayLength = 9;
        int relX = (this.width-guiDimension.getX()) / 2;
        int relY = (this.height-guiDimension.getY()) / 2;
        Vec2i colorDisplayBeginning = getColorDisplayBeginning();
        int xColorDisplayBeginning = colorDisplayBeginning.getX();
        int yColorDisplayBeginning = colorDisplayBeginning.getY();
        Option option = getColorOption();
        if (renderColor()){
            AbstractGui.fill(
                    relX + xColorDisplayBeginning,
                    relY + yColorDisplayBeginning,
                    relX + xColorDisplayBeginning + colorDisplayLength,
                    relY + yColorDisplayBeginning + colorDisplayLength,
                    option.getColor()
                    );
        }
    }
}
