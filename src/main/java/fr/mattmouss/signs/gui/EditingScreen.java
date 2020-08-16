package fr.mattmouss.signs.gui;

import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class EditingScreen extends Screen {

    Form form ;
    BlockPos panelPos;
    ResourceLocation LET_WAY = new ResourceLocation(SignMod.MODID,"textures/gui/let_way_gui.png");
    ResourceLocation STOP = new ResourceLocation(SignMod.MODID,"textures/gui/stop_gui.png");

    protected EditingScreen(Form form,BlockPos panelPos) {
        super(new StringTextComponent("Editing Screen"));
        this.form = form;
        this.panelPos = panelPos;
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        
    }

    public static void open(Form form,BlockPos panelPos){
        Minecraft.getInstance().displayGuiScreen(new EditingScreen(form, panelPos));
    }
}
