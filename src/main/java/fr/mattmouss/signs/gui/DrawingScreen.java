package fr.mattmouss.signs.gui;


import com.ibm.icu.lang.UCharacter;
import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.screenutils.PencilMode;
import fr.mattmouss.signs.gui.screenutils.PencilOption;
import fr.mattmouss.signs.gui.widget.ColorSlider;
import fr.mattmouss.signs.tileentity.DrawingSignTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class DrawingScreen extends Screen {
    private static final int LENGTH = 324;
    private static final int HEIGHT = 166;
    private static final int white = MathHelper.rgb(1.0F,1.0F,1.0F);

    Form form ;
    BlockPos panelPos;
    private static PencilOption option = PencilOption.getDefaultOption();
    ColorSlider RED_SLIDER,GREEN_SLIDER,BLUE_SLIDER;
    ImageButton[] pencil_button = new ImageButton[6];
    ImageButton chBgButton ;
    Button plusButton,moinsButton;


    ResourceLocation TRIANGLE = new ResourceLocation(SignMod.MODID,"textures/gui/triangle_gui.png");
    ResourceLocation CIRCLE = new ResourceLocation(SignMod.MODID,"textures/gui/circle_gui.png");
    ResourceLocation SQUARE = new ResourceLocation(SignMod.MODID,"textures/gui/square_gui.png");
    ResourceLocation DIAMOND = new ResourceLocation(SignMod.MODID,"textures/gui/diamond_gui.png");
    ResourceLocation PENCIL_BUTTONS = new ResourceLocation(SignMod.MODID,"textures/gui/pencil_buttons.png");

    protected DrawingScreen(Form form,BlockPos panelPos) {
        super(new StringTextComponent("Drawing Screen"));
        this.form = form;
        this.panelPos = panelPos;
    }

    @Override
    protected void init() {
        int BUTTON_LENGTH =25;
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        RED_SLIDER  =  new ColorSlider(relX+181,relY+7 ,option,ColorType.RED);
        GREEN_SLIDER = new ColorSlider(relX+181,relY+32,option,ColorType.GREEN);
        BLUE_SLIDER  = new ColorSlider(relX+181,relY+57,option,ColorType.BLUE);
        this.addButton(RED_SLIDER);
        this.addButton(BLUE_SLIDER);
        this.addButton(GREEN_SLIDER);
        for (int i=0;i<5;i++){
            int finalI = i;
            pencil_button[i] = new ImageButton(relX+4, //PosX on gui
                    relY+4+BUTTON_LENGTH*i, //PosY on gui
                    BUTTON_LENGTH, //width
                    BUTTON_LENGTH, //height
                    BUTTON_LENGTH*i, //PosX on button texture
                    0, //PosY on button texture
                    BUTTON_LENGTH, // y diff text when hovered
                    PENCIL_BUTTONS,
                    button -> {
                        this.changePencilMode(PencilMode.getPencilMode(finalI));
            });
            this.addButton(pencil_button[i]);
            chBgButton = new ImageButton(relX+75,
                    relY+135,
                    BUTTON_LENGTH,
                    BUTTON_LENGTH,
                    5*BUTTON_LENGTH,
                    0,BUTTON_LENGTH,
                    PENCIL_BUTTONS,
                    button -> {
                        this.chBgButton(option.getColor());
            });
            this.addButton(chBgButton);
            pencil_button[0].visible = false;
            pencil_button[0].active = false;
            plusButton = new Button(relX+290,relY+109,21,20,"+",button->{
                this.increaseLength();
            });
            moinsButton = new Button(relX+290,relY+129,21,20,"-",button->{
                this.decreaseLength();
            });
            this.addButton(plusButton);
            this.addButton(moinsButton);
        }
    }

    private void decreaseLength() {
    }

    private void increaseLength() {
    }

    private void chBgButton(int color) {
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        ResourceLocation location = getTexture(this.form);
        if (location == null){
            super.render(mouseX,mouseY,partialTicks);
            this.onClose();
            SignMod.LOGGER.warn("Error in form given to this code !! ");
            return;
        }
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        this.minecraft.getTextureManager().bindTexture(location);
        blit(relX, relY,this.blitOffset , 0.0F, 0.0F, LENGTH, HEIGHT, 256, 512);
        super.render(mouseX, mouseY, partialTicks);
        FontRenderer renderer = this.minecraft.fontRenderer;
        this.drawString(renderer,"Color of pencil :" ,relX+180,relY+93,white);
        this.drawString(renderer,"Length of pencil :",relX+160,relY+124,white);
        this.drawString(renderer,""+option.getLength(),relX+272,relY+124,white);
        try {
            DrawingSignTileEntity dste = getTileEntity();
            dste.renderOnScreen(relX+30,relY+4);
        }catch (Exception e){
            SignMod.LOGGER.warn("skip drawing screen : tile entity was not found and exception was raised : "+e.getMessage());
        }
        GlStateManager.enableBlend();
        if (option.getMode().enableSlider()) {
            AbstractGui.fill(relX + 271, relY + 93, relX + 271 + 9, relY + 93 + 9, option.getColor());
        }
    }

    private ResourceLocation getTexture(Form form) {
        switch (form){
            case CIRCLE:
                return CIRCLE;
            case SQUARE:
                return SQUARE;
            case DIAMOND:
                return DIAMOND;
            case TRIANGLE:
                return TRIANGLE;
            case ARROW:
            case OCTOGONE:
            case RECTANGLE:
            case PLAIN_SQUARE:
            case UPSIDE_TRIANGLE:
            default:
                return null;
        }
    }

    private void changePencilMode(PencilMode newMode){
        PencilMode oldMode = option.getMode();
        if (oldMode != newMode){
            int oldModeMeta = oldMode.getMeta();
            int newModeMeta = newMode.getMeta();
            pencil_button[oldModeMeta].visible = true;
            pencil_button[oldModeMeta].active = true;
            pencil_button[newModeMeta].visible = false;
            pencil_button[newModeMeta].active = false;
            option.changePencilMode(newMode);
        }
        //xor : mean that we change slider position
        if (oldMode.enableSlider() != newMode.enableSlider()){
            boolean enableSlider = newMode.enableSlider();
            this.RED_SLIDER.visible = enableSlider;
            this.BLUE_SLIDER.visible = enableSlider;
            this.GREEN_SLIDER.visible = enableSlider;
            this.chBgButton.visible = enableSlider;
        }

    }

    public static void open(Form form,BlockPos panelPos){
        Minecraft.getInstance().displayGuiScreen(new DrawingScreen(form,panelPos));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX,mouseY,button);
    }

    private DrawingSignTileEntity getTileEntity(){
        World world = this.minecraft.world;
        TileEntity te = world.getTileEntity(panelPos);
        if (te != null && te instanceof DrawingSignTileEntity){
            return (DrawingSignTileEntity)te;
        }
        if (te == null)throw new NullPointerException("Tile entity at panelPos is not in desired place !");
        throw new IllegalStateException("Drawing Screen need drawing sign tile entity in place !");
    }
}
