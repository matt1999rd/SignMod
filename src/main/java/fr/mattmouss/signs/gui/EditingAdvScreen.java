package fr.mattmouss.signs.gui;

import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class EditingAdvScreen extends Screen {
    Form form ;
    BlockPos panelPos;
    ResourceLocation PLAIN_SQUARE = new ResourceLocation(SignMod.MODID,"textures/gui/plain_square_gui.png");
    ResourceLocation ARROW = new ResourceLocation(SignMod.MODID,"textures/gui/arrow_gui.png");
    ResourceLocation RECTANGLE = new ResourceLocation(SignMod.MODID,"textures/gui/rectangle_gui.png");

    protected EditingAdvScreen(Form form,BlockPos panelPos) {
        super(new StringTextComponent("Editing and colouring screen"));
        this.form = form;
        this.panelPos = panelPos;
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {

    }

    public static void open(Form form,BlockPos panelPos){
        Minecraft.getInstance().displayGuiScreen(new EditingAdvScreen(form,panelPos));
    }
}
