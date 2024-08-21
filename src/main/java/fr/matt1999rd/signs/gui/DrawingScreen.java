package fr.matt1999rd.signs.gui;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.ClientAction;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.gui.screenutils.ColorType;
import fr.matt1999rd.signs.gui.screenutils.Option;
import fr.matt1999rd.signs.gui.screenutils.PencilMode;
import fr.matt1999rd.signs.gui.screenutils.PencilOption;
import fr.matt1999rd.signs.gui.widget.ColorSlider;
import fr.matt1999rd.signs.networking.Networking;
import fr.matt1999rd.signs.networking.PacketAddOrEditText;
import fr.matt1999rd.signs.networking.PacketDelText;
import fr.matt1999rd.signs.networking.PacketDrawingAction;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import fr.matt1999rd.signs.util.Text;
import fr.matt1999rd.signs.util.Vector2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static net.minecraft.network.chat.Component.nullToEmpty;

public class DrawingScreen extends WithColorSliderScreen implements IWithEditTextScreen {
    private static final int white = Mth.color(1.0F,1.0F,1.0F);

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
        super(new TextComponent("Drawing Screen"));
        this.form = form;
        this.panelPos = panelPos;
        this.DIMENSION = new Vector2i(325,203);
        option = PencilOption.getDefaultOption();
    }

    /** initial function of opening **/

    public static void open(Form form,BlockPos panelPos){
        Minecraft.getInstance().setScreen(new DrawingScreen(form,panelPos));
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
            this.addRenderableWidget(pencil_button[i]);
        }
        chBgButton = new ImageButton(relX+75,
                relY+135,
                BUTTON_LENGTH,
                BUTTON_LENGTH,
                5*BUTTON_LENGTH,
                0,BUTTON_LENGTH,
                PENCIL_BUTTONS,
                button -> this.chBgButton(option.getColor()));
        this.addRenderableWidget(chBgButton);
        //todo : init is redone when exiting the gui add text -> correct bug due to this action
        int modeIndices = option.getMode().getMeta();
        pencil_button[modeIndices].visible = false;
        pencil_button[modeIndices].active = false;
        plusButton = new Button(relX+290,relY+109,21,20,nullToEmpty("+"),button-> this.increaseLength());
        minusButton = new Button(relX+290,relY+129,21,20,nullToEmpty("-"), button-> this.decreaseLength());
        this.addRenderableWidget(plusButton);
        this.addRenderableWidget(minusButton);

        addTextButton = new Button(relX+132,relY+142,74,20,nullToEmpty("Add Text"),b->openTextGui());
        delTextButton = new Button(relX+132,relY+165,74,20,nullToEmpty("Delete Text"),b->deleteSelText());
        this.addRenderableWidget(addTextButton);
        this.addRenderableWidget(delTextButton);
        addTextButton.active = option.getMode() == PencilMode.SELECT;
        delTextButton.active = option.getMode() == PencilMode.SELECT && option.isTextSelected();
        minusButton.active = option.getLength() != 1;
        plusButton.active = option.getLength() != 64;
        if (option.isTextSelected()){
            addTextButton.setMessage(nullToEmpty("Edit Text"));
        }
        super.init();
        changeSliderDisplay(option.getMode().enableSlider());
    }

    /** display function **/

    @Override
    public void render(PoseStack stack,int mouseX, int mouseY, float partialTicks) {
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        RenderSystem.setShaderTexture(0,BACKGROUND);
        blit(stack,relX, relY,this.getBlitOffset() , 0.0F, 0.0F, DIMENSION.getX(), DIMENSION.getY(), 256, 512);
        DrawingSignTileEntity dste = getTileEntity();
        dste.renderOnScreen(stack,relX+30,relY+4,option.getTextIndices());
        super.render(stack,mouseX, mouseY, partialTicks);
        // rendering of color is done before following action -> not the case before
        assert this.minecraft != null;
        Font renderer = this.minecraft.font;
        int length = option.getLength();
        int gap = (length>9) ? 6:0;
        drawString(stack,renderer,"Color of pencil :" ,relX+180,relY+93,white);
        drawString(stack,renderer,"Length of pencil :",relX+160,relY+124,white);
        if (option.getMode().enablePencilLength()) drawString(stack,renderer,""+length,relX+272-gap,relY+126,white);
        if (option.getMode() != PencilMode.SELECT) GuiComponent.fill(stack,relX+83,relY+171,relX+83+9,relY+171+9,dste.getBGColor());
    }

    /** IPressable Consumer object for button in drawing screen **/

    private void deleteSelText() {
        int n = option.getTextIndices();
        if (option.isTextSelected()) {
            delText(n);
            option.unselectText();
            addTextButton.setMessage(nullToEmpty("Add Text"));
        }
        delTextButton.active = false;
    }

    private void openTextGui() {
        Minecraft.getInstance().setScreen(null);
        DrawingSignTileEntity dste = getTileEntity();
        if (option.isTextSelected()){
            Text t = dste.getText(option.getTextIndices());
            AddTextScreen.open(this,t);
        }else {
            AddTextScreen.open(this);
        }
    }

    @Override
    Vector2i getDyeButtonsBeginning() {
        return new Vector2i(164,18);
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
        this.addRenderableWidget(RED_SLIDER);
        this.addRenderableWidget(BLUE_SLIDER);
        this.addRenderableWidget(GREEN_SLIDER);
    }

    @Override
    boolean renderColor() {
        return option.getMode().enableSlider();
    }

    @Override
    Vector2i getColorDisplayBeginning() {
        return new Vector2i(271,93);
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

        if (oldMode.enablePencilLength() != newMode.enablePencilLength()){
            changePencilLengthButtonDisplay(newMode.enablePencilLength());
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

    private void changePencilLengthButtonDisplay(boolean enablePencilLength){
        this.plusButton.visible = enablePencilLength;
        this.minusButton.visible = enablePencilLength;
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
        int length,color;
        boolean makeAction = true;
        if (option.getMode() == PencilMode.PICK){
            DrawingSignTileEntity te = getTileEntity();
            color = te.getPixelColor(x,y);
            if (color != option.getColor()){
                super.fixColor(new Color(color));
            }
            length = 0;
            makeAction = false;
        }else if (option.getMode() != PencilMode.SELECT){
            color = option.getColor();
            length = option.getLength();
        }else {
            Pair<Integer,Text> indexAndText = getTextClicked(mouseX,mouseY);
            int newInd = indexAndText.getFirst();
            // if we have no text selected, and we click on left mouse button to select a text with success
            if ((!option.isTextSelected() && newInd != Text.UNSELECTED_TEXT_ID && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) ||
                    // if we have a text selected, and we click on right button to unselect the text with success
                    (option.isTextSelected() && newInd == Text.UNSELECTED_TEXT_ID && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                onTextSelected(newInd);
            }
            Text t = indexAndText.getSecond();
            if (t != null){
                //the text is moved so that the position where the mouse was clicked is the center of the rectangle formed by text limit
                x = getXOnScreen(mouseX - t.getLength(true,false)/2F);
                y = getYOnScreen(mouseY - t.getHeight()/2F);
            }
            makeAction = option.isTextSelected();
            color = option.getTextIndices();
            length = 1;
        }
        if (makeAction)transferActionToTE(ClientAction.getActionFromMode(option.getMode()),x,y,color,length);
    }

    private void onTextSelected(int newIndex){
        if (newIndex == Text.UNSELECTED_TEXT_ID){ //unselect a text
            option.unselectText();
            addTextButton.setMessage(nullToEmpty("Add Text"));
            delTextButton.active = false;
        }else { //select text
            option.selectText(newIndex);
            addTextButton.setMessage(nullToEmpty("Edit Text"));
            delTextButton.active = true;
        }
    }

    /**  screen test for mouse **/

    private Pair<Integer,Text> getTextClicked(double mouseX, double mouseY) {
        DrawingSignTileEntity dste = getTileEntity();
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        int n= dste.getNumberOfText();
        for (int i=0;i<n;i++){
            Text t = dste.getText(i);
            if (t.isIn(mouseX,mouseY,relX+30,relY+4,1.0F,1.0F)){
                return new Pair<>(i,t);
            }
        }
        return new Pair<>(Text.UNSELECTED_TEXT_ID,null);
    }

    private int getYOnScreen(double mouseY) {
        int relY = getGuiStartYPosition();
        float screenTop = relY + 4;
        double offsetMouseY = mouseY-screenTop;
        return Mth.fastFloor(offsetMouseY);
    }

    private int getXOnScreen(double mouseX) {
        int relX = getGuiStartXPosition();
        float screenLeft = relX + 30;
        double offsetMouseX = mouseX-screenLeft;
        return Mth.fastFloor(offsetMouseX);
    }

    private boolean mouseOnScreen(double mouseX, double mouseY) {
        int x = getXOnScreen(mouseX);
        int y = getYOnScreen(mouseY);
        return form.isIn(x,y);
    }

    /** key pressed override function **/

    @Override
    public boolean keyPressed(int button, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (option.getMode() == PencilMode.SELECT && button>GLFW.GLFW_KEY_INSERT - 1 && button<GLFW.GLFW_KEY_UP + 1) {
            if (button == GLFW.GLFW_KEY_INSERT) { // button insert
                openTextGui();
            }else if (option.isTextSelected()) {
                if (button == GLFW.GLFW_KEY_DELETE) { // button del
                    deleteSelText();
                } else { //pad button ↑ : 265  ↓ : 264  ← : 263 → : 262
                    button-=GLFW.GLFW_KEY_RIGHT;
                    DrawingSignTileEntity dste = getTileEntity();
                    Text t = dste.getText(option.getTextIndices());
                    int x_offset = (button == 0)? 1 : (button == 1) ? -1 : 0;
                    int y_offset = (button == 2)? 1 : (button == 3) ? -1 : 0;
                    int x = (int)t.getX(true) + x_offset; //todo : solve the problem : "handle the offset position will lead to define a false position"
                    int y = (int)t.getY(true) + y_offset;
                    transferActionToTE(ClientAction.MOVE_TEXT,x,y,option.getTextIndices(),1);
                }
            }
        }
        return super.keyPressed(button,p_keyPressed_2_,p_keyPressed_3_);
    }


    private DrawingSignTileEntity getTileEntity(){
        assert this.minecraft != null;
        Level world = this.minecraft.level;
        assert world != null;
        BlockEntity te = world.getBlockEntity(panelPos);
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
        int ind = option.getTextIndices();
        DrawingSignTileEntity dste = getTileEntity();
        Networking.INSTANCE.sendToServer(new PacketAddOrEditText(panelPos,t,ind));
        dste.addOrEditText(t,ind);
        if (ind ==Text.UNSELECTED_TEXT_ID){ //before this text was added, there was no text selected
            int k=dste.getNumberOfText();
            onTextSelected(k-1);
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
