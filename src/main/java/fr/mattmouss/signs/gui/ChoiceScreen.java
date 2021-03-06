package fr.mattmouss.signs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.networking.Networking;
import fr.mattmouss.signs.networking.PacketPlacePanel;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;

public class ChoiceScreen extends Screen {
    private static final int LENGTH = 161;
    private static final int BUTTON_LENGTH = 51;
    private final BlockPos futurePanelPos ;
    private final Direction futureFacing ;
    private final boolean rotated;
    private final boolean isGrid;
    private final boolean has4Grid;

    private ResourceLocation GUI = new ResourceLocation(SignMod.MODID,"textures/gui/choice_gui.png");
    private ResourceLocation FORM = new ResourceLocation(SignMod.MODID, "textures/gui/choice_button.png");

    public ChoiceScreen(BlockPos futurePanelPos,Direction futureFacing,boolean rotated,boolean isGrid,boolean has4Grid) {
        super(new StringTextComponent("Choose form of signs"));
        this.futurePanelPos = futurePanelPos;
        this.futureFacing = futureFacing;
        this.rotated = rotated;
        this.isGrid = isGrid;
        this.has4Grid = has4Grid;
    }

    @Override
    protected void init() {
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-LENGTH) / 2;
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                int k = 3*i+j;
                Form f = Form.byIndex(k);
                if (!(isGrid && f.isForDirection()) && ((isGrid&&has4Grid)||f!=Form.PLAIN_SQUARE)) {
                    addButton(new ImageButton(relX + 4 + BUTTON_LENGTH * i, //PosX on gui
                            relY + 4 + BUTTON_LENGTH * j, //PosY on gui
                            BUTTON_LENGTH, //width
                            BUTTON_LENGTH, //height
                            BUTTON_LENGTH * (k % 5), //PosX on button texture
                            (k > 4) ? BUTTON_LENGTH * 2 : 0, //PosY on button texture
                            BUTTON_LENGTH, // y diff text when hovered
                            FORM,
                            button -> place(k)
                    ));
                }
            }
        }
    }




    private void place(int form){
        Form f = Form.byIndex(form);
        System.out.println("Form selected : "+f.toString());
        //put here the code for placing block into world
        Networking.INSTANCE.sendToServer(new PacketPlacePanel(futurePanelPos,form,futureFacing,rotated));
        minecraft.displayGuiScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-LENGTH) / 2;
        this.blit(relX,relY,0,0,LENGTH, LENGTH);
        super.render(mouseX, mouseY, partialTicks);
    }

    public static void open(BlockPos futurePanelPos, Direction futureFacing, boolean rotated,boolean isGrid,boolean has4Grid){
        Minecraft.getInstance().displayGuiScreen(new ChoiceScreen(futurePanelPos,futureFacing,rotated,isGrid,has4Grid));
    }


}

