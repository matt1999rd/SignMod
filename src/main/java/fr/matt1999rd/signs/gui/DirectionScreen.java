package fr.matt1999rd.signs.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.gui.screenutils.ColorOption;
import fr.matt1999rd.signs.gui.screenutils.ColorType;
import fr.matt1999rd.signs.gui.screenutils.Option;
import fr.matt1999rd.signs.gui.widget.ColorSlider;
import fr.matt1999rd.signs.gui.widget.DirectionCursorButton;
import fr.matt1999rd.signs.gui.widget.DirectionPartBox;
import fr.matt1999rd.signs.networking.*;
import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import fr.matt1999rd.signs.util.Text;
import fr.matt1999rd.signs.util.Vector2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.awt.Color;
import java.util.List;

import static net.minecraft.util.text.ITextComponent.nullToEmpty;

import static fr.matt1999rd.signs.util.DirectionSignConstants.*;

public class DirectionScreen extends withColorSliderScreen implements IWithEditTextScreen {
    private int selTextInd = 4;
    private boolean isBgColorDisplayed = true;
    private boolean isTextCenter = false;
    private static ColorOption backgroundColorOption;
    private static ColorOption edgingColorOption;

    Form form ;
    BlockPos panelPos;
    DirectionPartBox[] changeBool = new DirectionPartBox[5];
    ColorSlider[] sliders = new ColorSlider[6];
    DirectionCursorButton[] arrowDirection = new DirectionCursorButton[3];
    Button applyColorButton,addOrSetTextButton;
    CheckboxButton centerText;

    ResourceLocation BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/gui/direction_gui.png");

    protected DirectionScreen(Form form,BlockPos panelPos) {
        super(new StringTextComponent("Direction Screen"));
        this.form = form;
        this.panelPos = panelPos;
        this.DIMENSION = new Vector2i(424,172);
    }

    /** initial function of opening **/

    public static void open(Form form, BlockPos panelPos){
        Minecraft.getInstance().setScreen(new DirectionScreen(form,panelPos));
    }

    @Override
    Vector2i getDyeButtonsBeginning() {
        return new Vector2i(321,83);
    }

    @Override
    Option getColorOption() {
        return (isBgColorDisplayed)? backgroundColorOption : edgingColorOption;
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
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        for (int i=0;i<6;i++){
            ColorOption opt = (i<3)? backgroundColorOption : edgingColorOption;
            sliders[i] = new ColorSlider(relX+323,relY+7+i%3*25,opt, ColorType.byIndex(i%3),93);
            addButton(sliders[i]);
        }
        updateSliderDisplay();
    }

    @Override
    boolean renderColor() {
        return true;
    }

    @Override
    Vector2i getColorDisplayBeginning() {
        return new Vector2i(395,93);
    }

    @Override
    protected void init() {
        backgroundColorOption = new ColorOption(Color.WHITE);
        edgingColorOption = new ColorOption(Color.BLACK);
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        for (int i=0;i<5;i++){
            boolean b = getPlacement(i);
            changeBool[i] = new DirectionPartBox(i,this,relX,relY,b);
            addButton(changeBool[i]);
        }
        DirectionSignTileEntity dste = getTileEntity();
        if (form == Form.ARROW) {
            for (int i = 0; i < 3; i++) {
                boolean bool = dste.isRightArrow(i*2);
                arrowDirection[i] = new DirectionCursorButton(relX, relY, b -> {
                }, this, i, bool);
                arrowDirection[i].active = dste.isCellPresent(2*i);
                addButton(arrowDirection[i]);
            }
        }
        applyColorButton = new Button(relX+330,relY+145,75,20,nullToEmpty("apply Color"),b->applyColor());
        addButton(applyColorButton);
        addOrSetTextButton = new Button(relX+70,relY+145,75,20,nullToEmpty("Add Text"),b->openTextGui());
        addButton(addOrSetTextButton);
        isTextCenter = dste.isTextCentered();
        centerText = new CheckboxButton(relX+196,relY+146,20,20,nullToEmpty("center_text"),isTextCenter);
        if (form == Form.RECTANGLE){
            addButton(centerText);
        }
        super.init();
    }

    private void updateSliderDisplay() {
        for (int k=0;k<6;k++){
            boolean visAndAct = (isBgColorDisplayed) == (k<3);
            sliders[k].visible = visAndAct;
            sliders[k].active = visAndAct;
        }
    }

    private void applyColor() {
        if (selTextInd == -1)return;
        DirectionSignTileEntity tileEntity = getTileEntity();
        int selPanel = selTextInd/4+1;
        tileEntity.setColor(selPanel,false,edgingColorOption.getColor());
        tileEntity.setColor(selPanel,true,backgroundColorOption.getColor());
        Networking.INSTANCE.sendToServer(
                new PacketChangeColor(panelPos,edgingColorOption.getColor(),false,selPanel));
        Networking.INSTANCE.sendToServer(
                new PacketChangeColor(panelPos,backgroundColorOption.getColor(),true,selPanel));
    }

    private boolean getPlacement(int i) {
        DirectionSignTileEntity dste = getTileEntity();
        if (i==1)return dste.is12connected();
        if (i==3)return dste.is23connected();
        if (i<5) return dste.hasPanel((i+2)/2);
        //placement is the union of the 5 slot and the 3 slot that are reserved for arrow direction
        return dste.isRightArrow((i-5)*2);
    }

    @Override
    public void render(MatrixStack stack,int mouseX, int mouseY, float partialTicks) {
        int relX = getGuiStartXPosition();
        int relY = getGuiStartYPosition();
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bind(BACKGROUND);
        //display of background gui
        blit(stack,relX, relY ,this.getBlitOffset(),0.0F, 0.0F, DIMENSION.getX(), DIMENSION.getY(),256,512);
        int offset = (isBgColorDisplayed)? 25:0;
        //display of button for the color to define (using color slider) choice between background and foreground color
        blit(stack,relX+338,relY+85,this.getBlitOffset(),DIMENSION.getX()+offset,0,25,25,256,512);
        DirectionSignTileEntity dste = getTileEntity();
        if (form == Form.RECTANGLE){
            if (isTextCenter != centerText.selected()){
                onTextCenterChange();
            }
        }
        // 4 and 14 are offset to the place of rendering
        dste.renderOnScreen(stack,relX+4,relY+14);
        super.render(stack,mouseX, mouseY, partialTicks);

    }

    private void onTextCenterChange() {
        DirectionSignTileEntity dste = getTileEntity();
        isTextCenter = centerText.selected();
        dste.centerText(isTextCenter);
        dste.setCenterText(isTextCenter);
        Networking.INSTANCE.sendToServer(new PacketCenterText(panelPos,isTextCenter));
    }

    @Override
    public Form getForm() {
        return form;
    }

    public void updateBoolean(int ind){
        DirectionPartBox box = changeBool[ind];
        boolean newBool = box.selected();
        if (!newBool && selTextInd/2 == ind){
            //unselect text if we remove the panel
            selTextInd = -1;
        }
        DirectionSignTileEntity dste = getTileEntity();
        Networking.INSTANCE.sendToServer(new PacketSetBoolean(panelPos,ind,newBool));
        dste.updateBoolean(ind,newBool);
    }

    @Override
    public void addOrEditText(Text t) {
        float text_length = t.getLength(true);
        DirectionSignTileEntity dste = getTileEntity();
        int ind= (selTextInd/2);
        boolean isEndSelected = selTextInd%2 == 1;
        float x;
        if (isTextCenter){
            centerText.active = (text_length<begTextPixelNumber);
            if (isEndSelected){
                return;
            }
            x = (horPixelNumber - text_length) / 2.0F;
        } else if ((form == Form.ARROW && !dste.isRightArrow(ind)) != isEndSelected) {
            centerText.active = true;
            x = horPixelNumber - (text_length + sideGapPixelNumber) ; //writing on the right
        }else {
            centerText.active = true;
            x = sideGapPixelNumber; //writing on the left
        }
        int text_height = t.getHeight();
        int y_offset = 25 * ind + ind / 2;
        int height = (ind % 2 == 0) ? 25 : (ind == 1) ? 26 : 27;
        float y = (height - text_height) / 2.0F + y_offset;
        t.setPosition(x,y,false,false);
        dste.setText(ind,isEndSelected,t);
        Networking.INSTANCE.sendToServer(new PacketAddOrEditText(panelPos,t,selTextInd));
    }

    @Override
    public Screen getScreen() {
        return this;
    }

    private DirectionSignTileEntity getTileEntity(){
        assert this.minecraft != null;
        World world = this.minecraft.level;
        assert world != null;
        TileEntity te = world.getBlockEntity(panelPos);
        if (te instanceof DirectionSignTileEntity){
            return (DirectionSignTileEntity) te;
        }
        if (te == null)throw new NullPointerException("Tile entity at panelPos is not in desired place !");
        throw new IllegalStateException("Direction Screen need direction sign tile entity in place !");
    }

    public void changeArrowSide(int ind,boolean b){
        //this function is processing data in tile entity following click on right left button
        DirectionSignTileEntity dste = getTileEntity();
        dste.flipText(ind);
        Networking.INSTANCE.sendToServer(new PacketFlipText(panelPos,ind));
        dste.updateBoolean(ind+5,b);
        Networking.INSTANCE.sendToServer(new PacketSetBoolean(panelPos,ind+5,b));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int ind = getCellClickedInd(mouseX,mouseY);
        int guiLeft = getGuiStartXPosition();
        int guiTop = getGuiStartYPosition();
        if (ind != -1){
            selTextInd = ind;
            onChangeIndices();
        } else if (mouseX>guiLeft+338 && mouseX<guiLeft+362 && mouseY>guiTop+85 && mouseY<guiTop+109){
            isBgColorDisplayed = !isBgColorDisplayed;
            Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            updateSliderDisplay();
        }
        return super.mouseClicked(mouseX,mouseY,button);
    }

    private void onChangeIndices() {
        Text text = getText();
        if (selTextInd == -1){
            addOrSetTextButton.active = false;
            addOrSetTextButton.visible = false;
        } else {
            assert text != null;
            if (!text.isEmpty()){
                addOrSetTextButton.setMessage(nullToEmpty("Set Text"));
            }else {
                addOrSetTextButton.setMessage(nullToEmpty("Add Text"));
            }
        }
    }

    private Text getText() {
        DirectionSignTileEntity dste = getTileEntity();
        if (selTextInd == -1)return null;
        return dste.getText(selTextInd/2,(selTextInd%2 == 1));
    }

    public boolean isTextCentered(){
        return isTextCenter;
    }

    private int getCellClickedInd(double mouseX, double mouseY) {
        DirectionSignTileEntity dste = getTileEntity();
        for (int i=0;i<10;i++){
            if (isInCell(i,mouseX,mouseY) && dste.isCellPresent(i/2)){
                return i;
            }
        }
        return -1;
    }

    private boolean isInCell(int i,double mouseX,double mouseY) {
        int guiLeft = getGuiStartXPosition();
        int guiTop = getGuiStartYPosition();
        boolean isEnd = (i%2 == 1);
        DirectionSignTileEntity dste =getTileEntity();
        int x1;
        int ind = i/2;
        int length;
        if (isTextCenter){
            if (isEnd){
                return false;
            }
            x1 = guiLeft+sideGapPixelNumber;
            length = horPixelNumber-2*sideGapPixelNumber;
        } else {
            if (form == Form.RECTANGLE || dste.isRightArrow(ind)) {
                x1 = guiLeft + ((isEnd) ? horPixelNumber-sideGapPixelNumber-endTextPixelNumber : sideGapPixelNumber);
            } else {
                x1 = guiLeft + ((isEnd) ? sideGapPixelNumber : endTextPixelNumber+sideGapPixelNumber+centerGapPixelNumber);
            }
            length = (isEnd) ? endTextPixelNumber : horPixelNumber-endTextPixelNumber-8; //todo : add more constant in this function
        }
        //a gap of 25 and then 26
        int y1 = guiTop+16+(25*ind)+ind-(ind==0?0:1);

        return (mouseX>x1 && mouseX<x1+length) && (mouseY>y1 && mouseY<y1+21);
    }

    private void openTextGui() {
        Minecraft.getInstance().setScreen(null);
        if (selTextInd != -1){
            Text t = getText();
            AddTextScreen.open(this,t);
        }
    }

    public boolean isEndSelected(){
        return selTextInd%2 == 1;
    }

    //when ticking one of the 5 part direction we need to update cursor possibility
    public void updateCursorAuthorisation(int ind,boolean selected) {
        if (ind == 0){
            arrowDirection[0].active = selected;
        }else if (ind == 2){
            arrowDirection[1].active = selected;
        }else if (ind == 4){
            arrowDirection[2].active = selected;
        }
        DirectionSignTileEntity dste = getTileEntity();
        boolean tryUpdateThirdButton = (ind == 3);
        if (selected){
            if (ind == 1 && dste.isRightArrow(0) != dste.isRightArrow(2)){
                arrowDirection[1].rawOnPress();
                if (dste.is23connected()){
                    tryUpdateThirdButton = true;
                }
            }
            if (dste.isRightArrow(2) != dste.isRightArrow(4) && tryUpdateThirdButton){
                arrowDirection[2].rawOnPress();
            }
        }
    }

    public void updateOtherArrowSide(int ind){
        DirectionSignTileEntity dste = getTileEntity();
        if (ind == 0){
            if (dste.is12connected() && dste.isCellPresent(2)){
                arrowDirection[1].rawOnPress();
                if (dste.is23connected() && dste.isCellPresent(4)){
                    arrowDirection[2].rawOnPress();
                }
            }
        }else if (ind == 1){
            if (dste.is12connected() && dste.isCellPresent(0)){
                arrowDirection[0].rawOnPress();
            }
            if (dste.is23connected() && dste.isCellPresent(4)){
                arrowDirection[2].rawOnPress();
            }
        }else if (ind == 2) {
            if (dste.is23connected() && dste.isCellPresent(2)) {
                arrowDirection[1].rawOnPress();
                if (dste.is12connected() && dste.isCellPresent(0)) {
                    arrowDirection[0].rawOnPress();
                }
            }
        }
    }
}
