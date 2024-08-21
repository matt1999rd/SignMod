package fr.matt1999rd.signs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.enums.PSDisplayMode;
import fr.matt1999rd.signs.enums.PSPosition;
import fr.matt1999rd.signs.fixedpanel.panelblock.PanelBlock;
import fr.matt1999rd.signs.gui.screenutils.ColorOption;
import fr.matt1999rd.signs.gui.screenutils.ColorType;
import fr.matt1999rd.signs.gui.screenutils.Option;
import fr.matt1999rd.signs.gui.widget.ColorSlider;
import fr.matt1999rd.signs.gui.widget.LimitSizeTextField;
import fr.matt1999rd.signs.gui.widget.EnableImageButton;
import fr.matt1999rd.signs.networking.Networking;
import fr.matt1999rd.signs.networking.PacketAddOrEditText;
import fr.matt1999rd.signs.networking.PacketPSScreenOperation;
import fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity;
import fr.matt1999rd.signs.util.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import static fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity.*;
import static net.minecraft.network.chat.Component.nullToEmpty;
import java.util.Objects;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class PlainSquareScreen extends WithColorSliderScreen {
    private static final Rectangle displayModeBtnRectangle = new Rectangle(241,113,24,24);
    private static final Vector2i startDisplayModeBtnTexture = new Vector2i(315,64);

    //todo : change position of the text to ensure that styles do not overlap (especially underline and frame)
    private boolean isBgColorDisplayed = true;

    //the panelPos is the position of the DEFAULT_RIGHT_POSITION -> DOWN_RIGHT
    private BlockPos panelPos;
    private int selTextIndex = 0;
    private static ColorOption backgroundColorOption;
    private static ColorOption edgingColorOption;
    private boolean isRightReduceSelected = false;

    ColorSlider[] sliders = new ColorSlider[6];
    ResourceLocation PLAIN_SQUARE = new ResourceLocation(SignMod.MODID,"textures/gui/ps_gui.png");
    ImageButton[] psDisplayModeButton = new EnableImageButton[4];
    ImageButton[] arrowDirectionButton = new EnableImageButton[3];
    EnableImageButton leftReduceButton, rightReduceButton;
    Button applyColorButton;
    LimitSizeTextField textField;


    protected PlainSquareScreen(BlockPos panelPos) {
        super(new TextComponent("Plain Square screen"));
        this.panelPos = panelPos;
        this.DIMENSION = new Vector2i(315,199);
    }

    @Override
    Vector2i getDyeButtonsBeginning() {
        return new Vector2i(185,46);
    }

    @Override
    Option getColorOption() {
        return (isBgColorDisplayed)? backgroundColorOption : edgingColorOption;
    }

    private PlainSquareSignTileEntity getTileEntity(){
        assert this.minecraft != null;
        Level world = this.minecraft.level;
        assert world != null;
        BlockEntity te = world.getBlockEntity(panelPos);
        BlockState state = world.getBlockState(panelPos);
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (te instanceof PlainSquareSignTileEntity clickedPsste){
            PSPosition clickedPsstePosition = clickedPsste.getPosition();
            BlockPos realPanelPos = clickedPsstePosition.offsetPos(PanelBlock.DEFAULT_RIGHT_POSITION,panelPos,facing,clickedPsste.getMode().is2by2());
            te = world.getBlockEntity(realPanelPos);
            if (te instanceof PlainSquareSignTileEntity){
                return (PlainSquareSignTileEntity) te;
            }else {
                throw new IllegalStateException("The panel created has not the same number of tile entity as the number of block : the world may be corrupted !");
            }
        }
        if (te == null)throw new NullPointerException("Tile entity at panelPos is not in desired place !");
        throw new IllegalStateException("Plain Square Screen need plain square sign tile entity in place !");
    }

    @Override
    ColorSlider[] getActiveSliders() {
        if (isBgColorDisplayed){
            return new ColorSlider[]{sliders[0], sliders[1], sliders[2]};
        }else {
            return new ColorSlider[]{sliders[3], sliders[4], sliders[5]};
        }
    }

    @Override
    void initSlider() {
        PlainSquareSignTileEntity psste = getTileEntity();
        backgroundColorOption = new ColorOption(psste.getBackgroundColor());
        edgingColorOption = new ColorOption(psste.getForegroundColor());
        int guiLeft = getGuiStartXPosition();
        int guiTop = getGuiStartYPosition();
        for (int i=0;i<6;i++){
            ColorOption opt = (i<3)? backgroundColorOption : edgingColorOption;
            sliders[i] = new ColorSlider(guiLeft+211,guiTop+35+i%3*25,opt, ColorType.byIndex(i%3),93);
            addRenderableWidget(sliders[i]);
        }

        updateSliderDisplay();
    }

    @Override
    boolean renderColor() {
        return true;
    }

    @Override
    Vector2i getColorDisplayBeginning() {
        return new Vector2i(283,121);
    }

    @Override
    protected void init(){
        super.init();
        int BUTTON_LENGTH = 25;
        int ARROW_BUTTON_LENGTH = BUTTON_LENGTH -1;
        int guiLeft = getGuiStartXPosition();
        int guiTop = getGuiStartYPosition();
        PlainSquareSignTileEntity psste = getTileEntity();
        assert this.minecraft != null;
        byte authoring = Functions.getAuthoring(
                this.minecraft.level,panelPos,
                psste.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING),true);
        boolean isDisplayModeEnabled = !psste.getMode().is2by2() || authoring >= Functions.ONLY_3BY2_PANEL_IN_LEFT;
        for (PSDisplayMode mode : PSDisplayMode.values()){
            int i=mode.getMeta();
            psDisplayModeButton[i] =  new EnableImageButton(
                    guiLeft+149, guiTop+6+BUTTON_LENGTH*i, //position on gui
                    BUTTON_LENGTH, BUTTON_LENGTH, //dimension of the button
                    startDisplayModeBtnTexture.getX()+i*BUTTON_LENGTH,startDisplayModeBtnTexture.getY(),
                    startDisplayModeBtnTexture.getX()+i*BUTTON_LENGTH,startDisplayModeBtnTexture.getY()+2*BUTTON_LENGTH,BUTTON_LENGTH,//mapping on button texture (uv and v for hovered mode)
                    PLAIN_SQUARE, // texture resource
                    button -> changeDisplayMode(mode) ) ; //the action to do when clicking on the button
            this.addRenderableWidget(psDisplayModeButton[i]);
            psDisplayModeButton[i].active = isDisplayModeEnabled;
            if (isDisplayModeEnabled){
                psDisplayModeButton[i].visible = (psste.getMode() != mode);
            }
        }

        boolean isArrowDirectionEnabled = (psste.getMode() == PSDisplayMode.DIRECTION);
        for (int i=0;i<3;i++){
            int finalI = i;
            arrowDirectionButton[i] = new EnableImageButton(
                    guiLeft+211+i*(BUTTON_LENGTH+8),guiTop+6,
                    BUTTON_LENGTH, ARROW_BUTTON_LENGTH,
                    startDisplayModeBtnTexture.getX() +100+i*BUTTON_LENGTH,startDisplayModeBtnTexture.getY(),
                    startDisplayModeBtnTexture.getX() +100+i*BUTTON_LENGTH,startDisplayModeBtnTexture.getY()+2*ARROW_BUTTON_LENGTH,ARROW_BUTTON_LENGTH,
                    PLAIN_SQUARE,
                    button -> changeArrowDirection(finalI) );
            this.addRenderableWidget(arrowDirectionButton[i]);
            arrowDirectionButton[i].active = isArrowDirectionEnabled;
            if (isArrowDirectionEnabled){
                arrowDirectionButton[i].visible = (psste.getArrowId() != i);
            }
        }

        applyColorButton = new Button(guiLeft+148,guiTop+116,74,20,nullToEmpty("Apply Color"), button->applyColor());
        this.addRenderableWidget(applyColorButton);
        textField = new LimitSizeTextField(this.minecraft,guiLeft+10,guiTop+176,Form.PLAIN_SQUARE,Text.getDefaultText());
        textField.setFilter(Letter.VALIDATOR_FOR_TEXT_DISPLAY);
        textField.setResponder(this::onTextWritten);
        this.selectText(0);
        this.addRenderableWidget(textField);
        int SMALL_BUTTON_LENGTH = 9;
        int SMALL_BUTTON_HEIGHT = 13;
        leftReduceButton = new EnableImageButton(guiLeft+174,guiTop+6,
                SMALL_BUTTON_LENGTH,SMALL_BUTTON_HEIGHT,
                DIMENSION.getX(),BUTTON_LENGTH, DIMENSION.getX(), BUTTON_LENGTH+2*SMALL_BUTTON_HEIGHT,SMALL_BUTTON_HEIGHT,
                PLAIN_SQUARE, b->selectReducePosition(false));
        rightReduceButton = new EnableImageButton(guiLeft+174+SMALL_BUTTON_LENGTH, guiTop+6,
                SMALL_BUTTON_LENGTH,SMALL_BUTTON_HEIGHT,
                DIMENSION.getX()+SMALL_BUTTON_LENGTH, BUTTON_LENGTH, DIMENSION.getX()+SMALL_BUTTON_LENGTH,BUTTON_LENGTH+2*SMALL_BUTTON_HEIGHT,SMALL_BUTTON_HEIGHT,
                PLAIN_SQUARE, b->selectReducePosition(true));
        this.addRenderableWidget(leftReduceButton);
        this.addRenderableWidget(rightReduceButton);
        boolean isReduceButtonEnabled = !psste.getMode().is2by2() || authoring == Functions.ALL_PANEL;
        leftReduceButton.visible =  isReduceButtonEnabled;
        rightReduceButton.visible = isReduceButtonEnabled;
        isRightReduceSelected = (authoring>=Functions.ONLY_3BY2_PANEL_IN_RIGHT); // authoring = 3 means only right possible and authoring = 4 means both side so when all side is possible the default value is true
        leftReduceButton.active = isRightReduceSelected;
        rightReduceButton.active = !isRightReduceSelected;
    }

    public void onTextWritten(String text){
        if (selTextIndex != -1){
            PlainSquareSignTileEntity psste = getTileEntity();
            Text t = psste.getText(selTextIndex);
            if (t.getText().equals(text))return;
            t.setText(text);
            PSDisplayMode mode = psste.getMode();
            if (!mode.is2by2()) {
                t.centerText(mode,selTextIndex);
            }
            psste.setText(t,selTextIndex);
            Networking.INSTANCE.sendToServer(new PacketAddOrEditText(panelPos,t,selTextIndex));
        }
    }

    public void setStyles(TextStyles styles){
        if (selTextIndex != -1){
            PlainSquareSignTileEntity psste = getTileEntity();
            Text t = psste.getText(selTextIndex);
            t.setStyles(styles);
            psste.setText(t,selTextIndex);
            Networking.INSTANCE.sendToServer(new PacketAddOrEditText(panelPos,t,selTextIndex));
        }
    }

    private void selectReducePosition(boolean isRight){
        EnableImageButton buttonClicked = (isRight)? rightReduceButton : leftReduceButton;
        EnableImageButton buttonClickable = (isRight) ? leftReduceButton : rightReduceButton;
        isRightReduceSelected = isRight;
        buttonClicked.active = false;
        buttonClickable.active = true;
    }

    private void changeDisplayMode(PSDisplayMode newMode){
        // the function to modify the display mode of the plain square (not available for 2*2 panel)
        PlainSquareSignTileEntity psste = getTileEntity();
        PSDisplayMode oldMode = psste.getMode();
        int meta = newMode.getMeta();
        Direction facing = psste.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean needBlockModification = oldMode.is2by2() != newMode.is2by2();

        // action for toggle of the display mode button -> re-enable the actual display mode button
        psDisplayModeButton[oldMode.getMeta()].visible = true;
        // action to disable the arrow button as display mode is no longer "direction"
        if (oldMode == PSDisplayMode.DIRECTION){
            for (int j=0;j<3;j++) {
                arrowDirectionButton[j].active = false;
                arrowDirectionButton[j].visible = true;
            }
        }
        //action to enable or disable left and right reduce button on change of display mode
        if (needBlockModification) {
            int authoring = oldMode.is2by2() ? Functions.ALL_PANEL : Functions.getAuthoring(
                    Objects.requireNonNull(this.minecraft).level,
                    panelPos,
                    facing,
                    true);
            boolean enableReduceButton = oldMode.is2by2() || (authoring == Functions.ALL_PANEL);
            rightReduceButton.visible = enableReduceButton;
            leftReduceButton.visible = enableReduceButton;
        }
        doOperationOnTE(isRightReduceSelected?SET_MODE_WITH_RIGHT_SELECTED:SET_MODE_WITH_LEFT_SELECTED,newMode.getMeta());

        //action on PanelPos to ensure that this variable is always the position of the down right part of the panel
        if (needBlockModification && isRightReduceSelected){
            if (oldMode.is2by2()){ //add two blocks on the right and move the pos to the right
                panelPos = panelPos.relative(facing.getCounterClockWise());
            }else { //remove two blocks on the right and move the pos to the left
                panelPos = panelPos.relative(facing.getClockWise());
            }
        }

        // action to enable the arrow button as display mode become "direction"
        if (newMode == PSDisplayMode.DIRECTION){
            for (int j=0;j<3;j++){
                arrowDirectionButton[j].active = true;
                arrowDirectionButton[j].visible = (psste.getArrowId() != j);
            }
        }
        // action for toggle of the display mode button -> disable button clicked
        psDisplayModeButton[meta].visible = false;
    }

    private void changeArrowDirection(int i){
        //the function to modify the arrow for the display mode
        for (int j=0;j<3;j++){
            arrowDirectionButton[j].visible = true;
        }
        doOperationOnTE(SET_ARROW_ID,i);
        arrowDirectionButton[i].visible = false;
    }

    private void selectText(int newSelTextIndex){
        boolean wasNotVisible = this.selTextIndex == -1;
        this.selTextIndex = newSelTextIndex;
        if (newSelTextIndex == -1){
            textField.visible = false;
        }else {
            if (wasNotVisible)textField.visible = true;
            PlainSquareSignTileEntity psste = getTileEntity();
            textField.setText(psste.getText(newSelTextIndex));
            textField.setLengthLimit(getMaxLength());
        }
    }

    private void applyColor(){
        //the function to modify the color of background or foreground
        if (isBgColorDisplayed){
            doOperationOnTE(CHANGE_BG_COLOR,backgroundColorOption.getColor());
        }else {
            doOperationOnTE(CHANGE_EDGING_COLOR,edgingColorOption.getColor());
        }
    }

    @Override
    public void render(PoseStack stack,int mouseX, int mouseY, float partialTicks) {
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        RenderSystem.setShaderTexture(0,PLAIN_SQUARE);
        blit(stack,relX, relY,this.getBlitOffset() , 0.0F, 0.0F, DIMENSION.getX(), DIMENSION.getY(), 256, 512);
        int offset = (isBgColorDisplayed)? 25:0;
        //display of button for the color to define (using color slider) choice between background and foreground color
        blit(stack,relX+241,relY+113,this.getBlitOffset(),DIMENSION.getX()+offset,0,25,25,256,512);
        PlainSquareSignTileEntity psste = getTileEntity();
        psste.renderOnScreen(stack,relX+10,relY+10,selTextIndex);
        super.render(stack,mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiLeft = getGuiStartXPosition();
        int guiTop = getGuiStartYPosition();
        Rectangle specificRectangle = new Rectangle(displayModeBtnRectangle);
        specificRectangle.translate(guiLeft,guiTop);
        if (specificRectangle.contains(mouseX,mouseY)){
            isBgColorDisplayed = !isBgColorDisplayed;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            updateSliderDisplay();
        }
        int newSelTextIndex = getTextSelected(mouseX,mouseY);
        if (newSelTextIndex != -1){
            selectText(newSelTextIndex);
        }else if (button == 1){
            selectText(-1);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }



    private int getTextSelected(double mouseX, double mouseY){
        int guiLeft = getGuiStartXPosition() + 10;
        int guiTop = getGuiStartYPosition() + 10;
        PlainSquareSignTileEntity psste = getTileEntity();
        PSDisplayMode mode = psste.getMode();
        int nbText = mode.getTotalText();
        float scaleX = SCREEN_LENGTH/(mode.is2by2()?64.0F:96.0F);
        float scaleY = SCREEN_LENGTH/64.0F;
        for (int j=0;j<nbText;j++){
            Text t=new Text(psste.getText(j));
            if (t.isEmpty()){
                Rectangle2D rectangle = psste.getTextArea(guiLeft,guiTop,j,scaleX,scaleY);
                if (rectangle.contains(mouseX,mouseY)){
                    return j;
                }
            }else {
                t.changeScale(1); // text are 2 times bigger in gui
                if (t.isIn(mouseX, mouseY, guiLeft, guiTop, scaleX, scaleY)) {
                    return j;
                }
            }
        }
        return -1;
    }
    private void updateSliderDisplay() {
        for (int k=0;k<6;k++){
            boolean visAndAct = (isBgColorDisplayed) == (k<3);
            sliders[k].visible = visAndAct;
            sliders[k].active = visAndAct;
        }
    }

    public static void open(BlockPos panelPos){
        Minecraft minecraft = Minecraft.getInstance();
        assert minecraft.level != null;
        BlockEntity tile = minecraft.level.getBlockEntity(panelPos);
        if (tile instanceof PlainSquareSignTileEntity psste){
            Direction facing = psste.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            BlockPos defaultPSPosBlockPos = psste.getPosition().offsetPos(PanelBlock.DEFAULT_RIGHT_POSITION,panelPos,facing,psste.getMode().is2by2());
            minecraft.setScreen(new PlainSquareScreen(defaultPSPosBlockPos));
        }else {
            throw new IllegalStateException("The panel created has not the same number of tile entity as the number of block : the world may be corrupted !");
        }
    }

    /*
    @Override
    public void addOrEditText(Text t) {
        PlainSquareSignTileEntity psste = getTileEntity();
        psste.setText(t,selTextIndex);
        Networking.INSTANCE.sendToServer(new PacketAddOrEditText(panelPos,t,selTextIndex));
    }

     */

    public int getMaxLength(){
        PlainSquareSignTileEntity psste = getTileEntity();
        return psste.getMode().getMaxLength(selTextIndex);
    }

    public void doOperationOnTE(int operationId,int colorModeOrArrowDir){
        PlainSquareSignTileEntity psste = getTileEntity();
        psste.doOperation(operationId, colorModeOrArrowDir);
        Networking.INSTANCE.sendToServer(new PacketPSScreenOperation(panelPos,operationId,colorModeOrArrowDir));
    }
}
