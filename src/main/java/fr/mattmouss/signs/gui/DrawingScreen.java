package fr.mattmouss.signs.gui;


import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.ClientAction;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.screenutils.Option;
import fr.mattmouss.signs.gui.screenutils.PencilMode;
import fr.mattmouss.signs.gui.screenutils.PencilOption;
import fr.mattmouss.signs.gui.widget.ColorSlider;
import fr.mattmouss.signs.networking.Networking;
import fr.mattmouss.signs.networking.PacketAddOrEditText;
import fr.mattmouss.signs.networking.PacketDelText;
import fr.mattmouss.signs.networking.PacketDrawingAction;
import fr.mattmouss.signs.tileentity.DrawingSignTileEntity;
import fr.mattmouss.signs.util.Text;
import fr.mattmouss.signs.util.Vec2i;
import net.minecraft.client.Minecraft;
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

public class DrawingScreen extends withColorSliderScreen implements IWithEditTextScreen {
    private static final int white = MathHelper.rgb(1.0F,1.0F,1.0F);

    Form form ;
    BlockPos panelPos;
    private static PencilOption option ;
    ColorSlider RED_SLIDER,GREEN_SLIDER,BLUE_SLIDER;
    ImageButton[] pencil_button = new ImageButton[6];
    ImageButton chBgButton ;
    Button plusButton, minusButton,addTextButton,delTextButton;

    ResourceLocation BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/gui/drawing_gui.png");
    ResourceLocation PENCIL_BUTTONS = new ResourceLocation(SignMod.MODID,"textures/gui/buttons.png");

    protected DrawingScreen(Form form,BlockPos panelPos) {
        super(new StringTextComponent("Drawing Screen"));
        this.form = form;
        this.panelPos = panelPos;
        this.DIMENSION = new Vec2i(325,203);
        option = PencilOption.getDefaultOption();
    }

    /** initial function of opening **/

    public static void open(Form form,BlockPos panelPos){
        Minecraft.getInstance().displayGuiScreen(new DrawingScreen(form,panelPos));
    }

    @Override
    protected void init() {
        int BUTTON_LENGTH =25;
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
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
                    button -> this.changePencilMode(PencilMode.getPencilMode(finalI)));
            this.addButton(pencil_button[i]);
        }
        chBgButton = new ImageButton(relX+75,
                relY+135,
                BUTTON_LENGTH,
                BUTTON_LENGTH,
                5*BUTTON_LENGTH,
                0,BUTTON_LENGTH,
                PENCIL_BUTTONS,
                button -> this.chBgButton(option.getColor()));
        this.addButton(chBgButton);
        //todo : init is redone when exiting the gui addtext -> correct bug due to this action
        int modeIndice = option.getMode().getMeta();
        pencil_button[modeIndice].visible = false;
        pencil_button[modeIndice].active = false;
        plusButton = new Button(relX+290,relY+109,21,20,"+",button-> this.increaseLength());
        minusButton = new Button(relX+290,relY+129,21,20,"-", button-> this.decreaseLength());
        this.addButton(plusButton);
        this.addButton(minusButton);

        addTextButton = new Button(relX+132,relY+142,74,20,"Add Text",b->openTextGui());
        delTextButton = new Button(relX+132,relY+165,74,20,"Delete Text",b->deleteSelText());
        this.addButton(addTextButton);
        this.addButton(delTextButton);
        addTextButton.active = option.getMode() == PencilMode.SELECT && option.isTextSelected();
        delTextButton.active = option.getMode() == PencilMode.SELECT && option.isTextSelected();
        minusButton.active = option.getLength() != 1;
        plusButton.active = option.getLength() != 64;
        if (option.isTextSelected()){
            addTextButton.setMessage("Edit Text");
        }
        super.init();
        changeSliderDisplay(option.getMode().enableSlider());
    }

    /** display function **/

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(relX, relY,this.blitOffset , 0.0F, 0.0F, DIMENSION.getX(), DIMENSION.getY(), 256, 512);
        super.render(mouseX, mouseY, partialTicks);
        // rendering of color is done before following action -> not the case before
        FontRenderer renderer = this.minecraft.fontRenderer;
        int length = option.getLength();
        int gap = (length>9) ? 6:0;
        this.drawString(renderer,"Color of pencil :" ,relX+180,relY+93,white);
        this.drawString(renderer,"Length of pencil :",relX+160,relY+124,white);
        this.drawString(renderer,""+length,relX+272-gap,relY+126,white);
        DrawingSignTileEntity dste = getTileEntity();
        dste.renderOnScreen(relX+30,relY+4,option.getTextIndice());
    }

    /** IPressable Consumer object for button in drawing screen **/

    private void deleteSelText() {
        int n = option.getTextIndice();
        if (option.isTextSelected()) {
            delText(n);
            option.unselectText();
            addTextButton.setMessage("Add Text");
        }
        delTextButton.active = false;
    }

    private void openTextGui() {
        Minecraft.getInstance().displayGuiScreen(null);
        DrawingSignTileEntity dste = getTileEntity();
        if (option.isTextSelected()){
            Text t = dste.getText(option.getTextIndice());
            AddTextScreen.open(this,t);
        }else {
            AddTextScreen.open(this);
        }
    }

    @Override
    Vec2i getDyeButtonsBeginning() {
        return new Vec2i(164,18);
    }

    @Override
    Option getColorOption() {
        return option;
    }

    @Override
    ColorSlider[] getActiveSliders() {
        return new ColorSlider[]{ RED_SLIDER, GREEN_SLIDER, BLUE_SLIDER};
    }

    @Override
    void initSlider() {
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        RED_SLIDER  =  new ColorSlider(relX+181,relY+7 ,option,ColorType.RED,135);
        GREEN_SLIDER = new ColorSlider(relX+181,relY+32,option,ColorType.GREEN,135);
        BLUE_SLIDER  = new ColorSlider(relX+181,relY+57,option,ColorType.BLUE,135);
        this.addButton(RED_SLIDER);
        this.addButton(BLUE_SLIDER);
        this.addButton(GREEN_SLIDER);
    }

    @Override
    boolean renderColor() {
        return option.getMode().enableSlider();
    }

    @Override
    Vec2i getColorDisplayBeginning() {
        return new Vec2i(271,93);
    }

    private void decreaseLength() {
        if (option.getLength() == 2){
            minusButton.active = false;
        }else if (option.getLength() == 64){
            plusButton.active = true;
        }
        option.incrementLength(false);
    }

    private void increaseLength() {
        if (option.getLength() == 1){
            minusButton.active = true;
        }else if (option.getLength() == 63){
            plusButton.active = false;
        }
        option.incrementLength(true);
    }

    private void chBgButton(int color) {
        transferActionToTE(ClientAction.SET_BG,-1,-1,color,-1);
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
            if (newMode == PencilMode.SELECT){
                addTextButton.active = true;
                if (option.isTextSelected()){
                    delTextButton.active = true;
                }
            }else if (oldMode == PencilMode.SELECT){
                addTextButton.active = false;
                if (option.isTextSelected()){
                    delTextButton.active = false;
                }
            }
        }
        //xor : mean that we change slider position
        if (oldMode.enableSlider() != newMode.enableSlider()){
            changeSliderDisplay(newMode.enableSlider());
        }

    }

    private void changeSliderDisplay(boolean enableSlider){
        this.RED_SLIDER.visible = enableSlider;
        this.BLUE_SLIDER.visible = enableSlider;
        this.GREEN_SLIDER.visible = enableSlider;
        this.chBgButton.visible = enableSlider;
        for (DyeColor dyeColor : DyeColor.values()){
            this.dye_color_button[dyeColor.getId()].visible = enableSlider;
        }
    }


    /** mouse clicked override function  **/

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
        int color,length;
        boolean makeAction = true;
        if (option.getMode() == PencilMode.PICK){
            DrawingSignTileEntity te = getTileEntity();
            color = te.getPixelColor(x,y);
            if (color != option.getColor()){
                super.fixColor(color);
            }
            length = 0;
            makeAction = false;
        }else if (option.getMode() != PencilMode.SELECT){
            color = option.getColor();
            length = option.getLength();
        }else {
            int newInd = getTextClicked(mouseX,mouseY);
            if (!option.isTextSelected() && newInd != -1 && button == 0){
                option.selectText(newInd);
                addTextButton.setMessage("Edit Text");
                delTextButton.active = true;
            }else if (option.isTextSelected() && newInd == -1 && button == 1){
                option.unselectText();
                addTextButton.setMessage("Add Text");
                delTextButton.active = false;
            }
            makeAction = option.isTextSelected();
            color = option.getTextIndice();
            length = 1;
        }
        if (makeAction)transferActionToTE(ClientAction.getActionFromMode(option.getMode()),x,y,color,length);
    }

    /**  screen test for mouse **/

    private int getTextClicked(double mouseX, double mouseY) {
        DrawingSignTileEntity dste = getTileEntity();
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        int n= dste.getNumberOfText();
        for (int i=0;i<n;i++){
            Text t = dste.getText(i);
            if (t.isIn(mouseX,mouseY,relX+30,relY+4)){
                return i;
            }
        }
        return -1;
    }

    private int getYOnScreen(double mouseY) {
        int relY = getGuiStartYPosition();
        float screenTop = relY + 4;
        double offsetMouseY = mouseY-screenTop;
        return MathHelper.fastFloor(offsetMouseY);
    }

    private int getXOnScreen(double mouseX) {
        int relX = getGuiStartXPosition();
        float screenLeft = relX + 30;
        double offsetMouseX = mouseX-screenLeft;
        return MathHelper.fastFloor(offsetMouseX);
    }

    private boolean mouseOnScreen(double mouseX, double mouseY) {
        int x = getXOnScreen(mouseX);
        int y = getYOnScreen(mouseY);
        return form.isIn(x,y);
    }

    /** key pressed override function **/

    @Override
    public boolean keyPressed(int button, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (option.getMode() == PencilMode.SELECT && button>259 && button<266) {
            if (button == 260) { // button insert
                openTextGui();
            }else if (option.isTextSelected()) {
                if (button == 261) { // button suppr
                    deleteSelText();
                } else { //pad button /|\ : 265  \|/ : 264  <- : 263 -> : 262
                    button-=262;
                    DrawingSignTileEntity dste = getTileEntity();
                    Text t = dste.getText(option.getTextIndice());
                    int x_offset = (button == 0)? 1 : (button == 1) ? -1 : 0;
                    int y_offset = (button == 2)? 1 : (button == 3) ? -1 : 0;
                    int x = (int)t.getX() + x_offset;
                    int y = (int)t.getY() + y_offset;
                    transferActionToTE(ClientAction.MOVE_TEXT,x,y,option.getTextIndice(),1);
                }
            }
        }
        return super.keyPressed(button,p_keyPressed_2_,p_keyPressed_3_);
    }





    private DrawingSignTileEntity getTileEntity(){
        assert this.minecraft != null;
        World world = this.minecraft.world;
        TileEntity te = world.getTileEntity(panelPos);
        if (te instanceof DrawingSignTileEntity){
            return (DrawingSignTileEntity)te;
        }
        if (te == null)throw new NullPointerException("Tile entity at panelPos is not in desired place !");
        throw new IllegalStateException("Drawing Screen need drawing sign tile entity in place !");
    }



    /**             networking task             **/
    private void transferActionToTE(ClientAction action,int x,int y,int color,int length){
        DrawingSignTileEntity dste = getTileEntity();
        dste.makeOperationFromScreen(action,x,y,color,length);
        Networking.INSTANCE.sendToServer(new PacketDrawingAction(panelPos,action,x,y,color,length));
    }

    @Override
    public Form getForm() {
        return form;
    }

    public void addOrEditText(Text t){
        int ind = option.getTextIndice();
        DrawingSignTileEntity dste = getTileEntity();
        Networking.INSTANCE.sendToServer(new PacketAddOrEditText(panelPos,t,ind));
        dste.addOrEditText(t,ind);
        if (ind ==-1){
            int k=dste.getNumberOfText();
            option.selectText(k-1);
        }
    }

    @Override
    public Screen getScreen() {
        return this;
    }

    private void delText(int ind){
        DrawingSignTileEntity dste = getTileEntity();
        Networking.INSTANCE.sendToServer(new PacketDelText(panelPos,ind));
        dste.delText(ind);
    }

}
