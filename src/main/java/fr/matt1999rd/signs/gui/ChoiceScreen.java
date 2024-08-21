package fr.matt1999rd.signs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.networking.Networking;
import fr.matt1999rd.signs.networking.PacketPlacePSPanel;
import fr.matt1999rd.signs.networking.PacketPlacePanel;
import fr.matt1999rd.signs.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;

import java.util.Arrays;
import java.util.Objects;

public class ChoiceScreen extends Screen {
    private static final int LENGTH = 161;
    private static final int MAIN_MENU_BUTTON_LENGTH = 51;
    private static final int SUB_MENU_TEX_WIDTH = 256;
    private static final int SUB_MENU_TEX_HEIGHT = 316;
    private static final int SUB_MENU_BUTTON_LENGTH = 77;
    private final BlockPos futurePanelPos ;
    private final Direction futureFacing ;
    private final boolean rotated;
    private final boolean isGrid;
    private final boolean has4Grid;
    private boolean isSubMenu = false;
    private final ImageButton[] subMenuButtons = new ImageButton[4];
    private final ImageButton[] mainMenuButtons = new ImageButton[9];

    private final ResourceLocation MAIN_MENU_GUI = new ResourceLocation(SignMod.MODID,"textures/gui/choice_gui.png");
    private final ResourceLocation SUB_MENU_GUI = new ResourceLocation(SignMod.MODID,"textures/gui/ps_display_screen.png");
    private final ResourceLocation FORM = new ResourceLocation(SignMod.MODID, "textures/gui/choice_button.png");

    public ChoiceScreen(BlockPos futurePanelPos,Direction futureFacing,boolean rotated,boolean isGrid,boolean has4Grid) {
        super(new TextComponent("Choose form of signs"));
        this.futurePanelPos = futurePanelPos;
        this.futureFacing = futureFacing;
        this.rotated = rotated;
        this.isGrid = isGrid;
        this.has4Grid = has4Grid;
    }

    public void openSubMenu(){
        isSubMenu = true;
        changeButtonDisplayed();
    }

    @Override
    protected void init() {
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-LENGTH) / 2;
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                int k = 3*i+j;
                this.mainMenuButtons[k] = new ImageButton(relX + 4 + MAIN_MENU_BUTTON_LENGTH * i, //PosX on gui
                            relY + 4 + MAIN_MENU_BUTTON_LENGTH * j, //PosY on gui
                            MAIN_MENU_BUTTON_LENGTH, //width
                            MAIN_MENU_BUTTON_LENGTH, //height
                            MAIN_MENU_BUTTON_LENGTH * (k % 5), //PosX on button texture
                            (k > 4) ? MAIN_MENU_BUTTON_LENGTH * 2 : 0, //PosY on button texture
                            MAIN_MENU_BUTTON_LENGTH, // y diff text when hovered
                            FORM,
                            button -> place(k)
                );
                addRenderableWidget(mainMenuButtons[k]);
            }
        }
        for (int i=0;i<2;i++){
            for (int j=0;j<2;j++){
                int k = 2*i+j;
                subMenuButtons[k] = new ImageButton(relX + 4 + SUB_MENU_BUTTON_LENGTH * i, //PosX on gui
                        relY + 4 + SUB_MENU_BUTTON_LENGTH * j, //PosY on gui
                        SUB_MENU_BUTTON_LENGTH, //width
                        SUB_MENU_BUTTON_LENGTH, //height
                        162*(1-i)+i*j*(SUB_MENU_BUTTON_LENGTH+1), //PosX on button texture
                        162*i+(1-i)*j*(SUB_MENU_BUTTON_LENGTH+1), //PosY on button texture
                        SUB_MENU_BUTTON_LENGTH*(2-i)+2*(1-i), // y diff text when hovered = b if hor and 2b+2 if vert
                        SUB_MENU_GUI,
                        SUB_MENU_TEX_WIDTH,
                        SUB_MENU_TEX_HEIGHT,
                        button -> placePSPanel(k));
                addRenderableWidget(subMenuButtons[k]);
            }
        }
        changeButtonDisplayed();
    }

    private void place(int form){
        Form f = Form.byIndex(form);
        assert f != null;
        System.out.println("Form selected : "+ f);
        //put here the code for placing block into world
        if (f != Form.PLAIN_SQUARE){
            Networking.INSTANCE.sendToServer(new PacketPlacePanel(futurePanelPos,form,futureFacing,rotated));
            assert minecraft != null;
            minecraft.setScreen(null);
        }else {
            assert minecraft != null;
            byte authoring = Functions.getAuthoring(minecraft.level,futurePanelPos,futureFacing,false);
            int k = 0;
            for (ImageButton button : subMenuButtons){
                button.active = (k == 0 || authoring == Functions.ALL_PANEL);
                k++;
            }
            if (authoring != Functions.NO_PANEL)this.openSubMenu();
            else this.onClose();
        }
    }

    private void placePSPanel(int displayMode){
        Networking.INSTANCE.sendToServer(new PacketPlacePSPanel(futurePanelPos,displayMode,futureFacing,rotated));
        assert minecraft != null;
        minecraft.setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(PoseStack stack,int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0,getGUI());
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-LENGTH) / 2;
        if (isSubMenu){
            blit(stack,relX,relY,0,0,LENGTH, LENGTH,SUB_MENU_TEX_WIDTH,SUB_MENU_TEX_HEIGHT);
        }else {
            blit(stack,relX,relY,0,0,LENGTH, LENGTH);
        }
        super.render(stack,mouseX, mouseY, partialTicks);
    }

    public void changeButtonDisplayed(){
        if (isSubMenu){
            Arrays.stream(subMenuButtons).forEach(imageButton ->  imageButton.visible=true);
            Arrays.stream(mainMenuButtons).forEach(imageButton -> imageButton.visible=false);
        }else {
            Arrays.stream(subMenuButtons).forEach(imageButton -> imageButton.visible=false);
            for (int i=0;i<9;i++){
                Form f= Form.byIndex(i);
                mainMenuButtons[i].visible = !(isGrid && Objects.requireNonNull(f).isForDirection()) && ((isGrid&&has4Grid)||f!=Form.PLAIN_SQUARE);
            }
        }
    }

    public ResourceLocation getGUI(){
        if (isSubMenu)return SUB_MENU_GUI;
        else return MAIN_MENU_GUI;
    }

    public static void open(BlockPos futurePanelPos, Direction futureFacing, boolean rotated,boolean isGrid,boolean has4Grid){
        Minecraft.getInstance().setScreen(new ChoiceScreen(futurePanelPos,futureFacing,rotated,isGrid,has4Grid));
    }


}

