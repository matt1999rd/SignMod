package fr.matt1999rd.signs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.networking.Networking;
import fr.matt1999rd.signs.networking.PacketPlacePSPanel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

public class PSDisplayModeScreen extends Screen{
    private static final int LENGTH = 161;
    private static final int TEX_WIDTH = 256;
    private static final int TEX_HEIGHT = 316;
    private static final int BUTTON_LENGTH = 77;
    private final BlockPos futurePanelPos ;
    private final Direction futureFacing ;
    private final boolean rotated;
    private final byte authorisation; //code byte : 0 -> nothing can be placed, 1-> only 2 by 2, 2-> all panel can be placed
    private final ImageButton[] buttons = new ImageButton[4];
    private final ResourceLocation GUI = new ResourceLocation(SignMod.MODID,"textures/gui/ps_display_screen.png");


    public PSDisplayModeScreen(BlockPos futurePanelPos, Direction futureFacing, boolean rotated, byte authorisation) {
        super(new TextComponent("Choose display for plain square sign"));
        this.futurePanelPos = futurePanelPos;
        this.futureFacing = futureFacing;
        this.rotated = rotated;
        this.authorisation = authorisation;
    }

    @Override
    protected void init() {
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-LENGTH) / 2;
        for (int i=0;i<2;i++){
            for (int j=0;j<2;j++){
                int k = 2*i+j;
                //TODO : why this part of the function was commented ? -> code was added (GUI variable)
                buttons[k] = new ImageButton(relX + 4 + BUTTON_LENGTH * i, //PosX on gui
                        relY + 4 + BUTTON_LENGTH * j, //PosY on gui
                        BUTTON_LENGTH, //width
                        BUTTON_LENGTH, //height
                        162*(1-i)+i*j*(BUTTON_LENGTH+1), //PosX on button texture
                        162*i+(1-i)*j*(BUTTON_LENGTH+1), //PosY on button texture
                        BUTTON_LENGTH*(2-i)+2*(1-i), // y diff text when hovered = b if hor and 2b+2 if vert
                        GUI,
                        TEX_WIDTH,
                        TEX_HEIGHT,
                        button -> place(k));


                addRenderableWidget(buttons[k]);
            }
        }
        int k = 0;
        for (ImageButton button : buttons){
            button.active = (k == 0 | authorisation == 2);
            k++;
        }
    }

    private void place(int displayMode){
        Networking.INSTANCE.sendToServer(new PacketPlacePSPanel(futurePanelPos,displayMode,futureFacing,rotated));
        assert minecraft != null;
        minecraft.setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(PoseStack stack,int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0,GUI);
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-LENGTH) / 2;
        blit(stack,relX,relY,0,0,LENGTH, LENGTH,TEX_WIDTH,TEX_HEIGHT);
        super.render(stack,mouseX, mouseY, partialTicks);
    }


}
