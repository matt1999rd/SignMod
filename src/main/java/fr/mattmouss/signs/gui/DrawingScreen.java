package fr.mattmouss.signs.gui;


import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.ClientAction;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.screenutils.PencilMode;
import fr.mattmouss.signs.gui.screenutils.PencilOption;
import fr.mattmouss.signs.gui.widget.ColorSlider;
import fr.mattmouss.signs.networking.Networking;
import fr.mattmouss.signs.networking.PacketDrawingAction;
import fr.mattmouss.signs.tileentity.DrawingSignTileEntity;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.DyeColor;
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
    private static PencilOption option ;
    ColorSlider RED_SLIDER,GREEN_SLIDER,BLUE_SLIDER;
    ImageButton[] pencil_button = new ImageButton[6];
    ImageButton[] dye_color_button = new ImageButton[16];
    ImageButton chBgButton ;
    Button plusButton,moinsButton;


    ResourceLocation BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/gui/drawing_gui.png");
    ResourceLocation PENCIL_BUTTONS = new ResourceLocation(SignMod.MODID,"textures/gui/pencil_buttons.png");

    protected DrawingScreen(Form form,BlockPos panelPos) {
        super(new StringTextComponent("Drawing Screen"));
        this.form = form;
        this.panelPos = panelPos;
    }

    @Override
    protected void init() {
        option = PencilOption.getDefaultOption();
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
        }
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
        int x_dye_begining = relX+164;
        int y_dye_begining = relY+18;
        int dye_button_length = 6;
        for (DyeColor color : DyeColor.values()){
            int i=color.getId();
            dye_color_button[i] = new ImageButton(
                    x_dye_begining+i%2*6,
                    y_dye_begining+i/2*6,
                    dye_button_length,dye_button_length,
                    6*BUTTON_LENGTH+i*dye_button_length,
                    0,
                    dye_button_length,PENCIL_BUTTONS,
                    b->{
                        fixColor(color.getColorValue());
            });
            this.addButton(dye_color_button[i]);
        }
        moinsButton.active = false;
    }

    private void fixColor(int color) {
        option.setColor(color,null);
        RED_SLIDER.updateSlider(Functions.getRedValue(color));
        GREEN_SLIDER.updateSlider(Functions.getGreenValue(color));
        BLUE_SLIDER.updateSlider(Functions.getBlueValue(color));
    }

    private void decreaseLength() {
        if (option.getLength() == 2){
            moinsButton.active = false;
        }else if (option.getLength() == 64){
            plusButton.active = true;
        }
        option.incrementLength(false);
    }

    private void increaseLength() {
        if (option.getLength() == 1){
            moinsButton.active = true;
        }else if (option.getLength() == 63){
            plusButton.active = false;
        }
        option.incrementLength(true);
    }

    private void chBgButton(int color) {
        transferActionToTE(ClientAction.SET_BG,-1,-1,color,-1);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(relX, relY,this.blitOffset , 0.0F, 0.0F, LENGTH, HEIGHT, 256, 512);
        super.render(mouseX, mouseY, partialTicks);
        FontRenderer renderer = this.minecraft.fontRenderer;
        int length = option.getLength();
        int gap = (length>9) ? 6:0;
        this.drawString(renderer,"Color of pencil :" ,relX+180,relY+93,white);
        this.drawString(renderer,"Length of pencil :",relX+160,relY+124,white);
        this.drawString(renderer,""+length,relX+272-gap,relY+126,white);
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
            for (DyeColor dyeColor : DyeColor.values()){
                this.dye_color_button[dyeColor.getId()].visible = enableSlider;
            }
        }

    }

    public static void open(Form form,BlockPos panelPos){
        Minecraft.getInstance().displayGuiScreen(new DrawingScreen(form,panelPos));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseOnScreen(mouseX,mouseY)){
            manageClickOnScreen(mouseX,mouseY,button);
        }
        return super.mouseClicked(mouseX,mouseY,button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double mouseXf, double mouseYf) {
        if (mouseOnScreen(mouseX,mouseY)){
            manageClickOnScreen(mouseX,mouseY,button);
        }
        return super.mouseDragged(mouseX,mouseY,button,mouseXf,mouseYf);
    }

    private void manageClickOnScreen(double mouseX, double mouseY, int button) {
        int x = getXOnScreen(mouseX);
        int y = getYOnScreen(mouseY);
        if (option.getMode() == PencilMode.PICK){
            DrawingSignTileEntity te = getTileEntity();
            int color = te.getPixelColor(x,y);
            if (color != option.getColor()){
                fixColor(color);
            }
        }else {
            int color = option.getColor();
            int length = option.getLength();
            transferActionToTE(ClientAction.getActionFromMode(option.getMode()),x,y,color,length);
        }
    }

    private int getYOnScreen(double mouseY) {
        int relY = (this.height-HEIGHT) / 2;
        float screenTop = relY + 4;
        double offsetMouseY = mouseY-screenTop;
        return MathHelper.fastFloor(offsetMouseY);
    }

    private int getXOnScreen(double mouseX) {
        int relX = (this.width-LENGTH) / 2;
        float screenLeft = relX + 30;
        double offsetMouseX = mouseX-screenLeft;
        return MathHelper.fastFloor(offsetMouseX);
    }

    private boolean mouseOnScreen(double mouseX, double mouseY) {
        int x = getXOnScreen(mouseX);
        int y = getYOnScreen(mouseY);
        return form.isIn(x,y);
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

    private void transferActionToTE(ClientAction action,int x,int y,int color,int length){
        DrawingSignTileEntity dste = getTileEntity();
        dste.makeOperationFromScreen(action,x,y,color,length);
        Networking.INSTANCE.sendToServer(new PacketDrawingAction(panelPos,action,x,y,color,length));
    }
}
