package fr.mattmouss.signs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.networking.Networking;
import fr.mattmouss.signs.networking.PacketPlacePSPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class PSDisplayModeScreen extends Screen{
    private static final int LENGTH = 162;
    private static final int BUTTON_LENGTH = 77;
    private final BlockPos futurePanelPos ;
    private final Direction futureFacing ;
    private final boolean rotated;
    private byte autorisation; //code byte : 0 -> nothing can be placed, 1-> only 2 by 2, 2-> all panel can be placed
    private ImageButton[] buttons = new ImageButton[4];

    private ResourceLocation GUI = new ResourceLocation(SignMod.MODID,"textures/gui/ps_display_screen.png");
    public PSDisplayModeScreen(BlockPos futurePanelPos, Direction futureFacing, boolean rotated, byte autorisation) {
        super(new StringTextComponent("Choose display for plain square sign"));
        this.futurePanelPos = futurePanelPos;
        this.futureFacing = futureFacing;
        this.rotated = rotated;
        this.autorisation = autorisation;
    }

    @Override
    protected void init() {
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-LENGTH) / 2;
        for (int i=0;i<2;i++){
            for (int j=0;j<2;j++){
                int k = 2*i+j;
                buttons[k] = new ImageButton(relX + 4 + BUTTON_LENGTH * i, //PosX on gui
                        relY + 4 + BUTTON_LENGTH * j, //PosY on gui
                        BUTTON_LENGTH, //width
                        BUTTON_LENGTH, //height
                        162*(1-i)+i*j*(BUTTON_LENGTH+1), //PosX on button texture
                        162*i+(1-i)*j*(BUTTON_LENGTH+1), //PosY on button texture
                        0, // y diff text when hovered
                        GUI,
                        button -> place(k));
                addButton(buttons[k]);
            }
        }
        int k = 0;
        for (ImageButton button : buttons){
            button.active = (k == 0 | autorisation == 2);
            k++;
        }
    }

    private void place(int displayMode){
        Networking.INSTANCE.sendToServer(new PacketPlacePSPanel(futurePanelPos,displayMode,futureFacing,rotated));
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

    public static void open(BlockPos futurePanelPos, Direction futureFacing, boolean rotated,byte autorisation){
        if (autorisation == 0)return;
        Minecraft.getInstance().displayGuiScreen(new PSDisplayModeScreen(futurePanelPos,futureFacing,rotated,autorisation));
    }

}
