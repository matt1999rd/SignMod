package fr.mattmouss.signs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.screenutils.ColorOption;
import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.widget.ColorSlider;
import fr.mattmouss.signs.gui.widget.DirectionCursorButton;
import fr.mattmouss.signs.gui.widget.DirectionPartBox;
import fr.mattmouss.signs.networking.*;
import fr.mattmouss.signs.tileentity.DirectionSignTileEntity;
import static fr.mattmouss.signs.util.Functions.*;
import fr.mattmouss.signs.util.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.awt.*;

public class DirectionScreen extends Screen implements IWithEditTextScreen {

    private static final int LENGTH = 424;
    private static final int HEIGHT = 171;
    private int selTextInd = 4;
    private boolean isBgColorDisplayed = true;
    private boolean isTextCenter = false;
    private static final int white = MathHelper.rgb(1.0F,1.0F,1.0F);
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
    }

    /** initial function of opening **/

    public static void open(Form form, BlockPos panelPos){
        Minecraft.getInstance().displayGuiScreen(new DirectionScreen(form,panelPos));
    }

    @Override
    protected void init() {
        backgroundColorOption = new ColorOption(Color.WHITE);
        edgingColorOption = new ColorOption(Color.BLACK);
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
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
                addButton(arrowDirection[i]);
            }
        }
        for (int i=0;i<6;i++){
            ColorOption opt = (i<3)? backgroundColorOption : edgingColorOption;
            sliders[i] = new ColorSlider(relX+323,relY+7+i%3*25,opt, ColorType.byIndex(i%3),93);
            addButton(sliders[i]);
        }
        updateSliderDisplay();
        applyColorButton = new Button(relX+304,relY+119,74,20,"apply Color",b->applyColor());
        addButton(applyColorButton);
        addOrSetTextButton = new Button(relX+70,relY+145,75,20,"Add Text",b->openTextGui());
        addButton(addOrSetTextButton);
        isTextCenter = dste.isTextCentered();
        centerText = new CheckboxButton(relX+196,relY+146,20,20,"center_text",isTextCenter);
        if (form == Form.RECTANGLE){
            addButton(centerText);
        }

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
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(relX, relY ,this.blitOffset,0.0F, 0.0F, LENGTH, HEIGHT,256,512);
        GlStateManager.enableBlend();
        int offset = (isBgColorDisplayed)? 25:0;
        blit(relX+338,relY+85,this.blitOffset,LENGTH+offset,0,25,25,256,512);
        super.render(mouseX, mouseY, partialTicks);
        DirectionSignTileEntity dste = getTileEntity();
        if (form == Form.RECTANGLE){
            if (isTextCenter != centerText.func_212942_a()){
                onTextCenterChange();
            }
        }
        ColorOption option = (isBgColorDisplayed)? backgroundColorOption : edgingColorOption;
        AbstractGui.fill(relX+395,relY+93,relX+395+9,relY+93+9,option.getColor());
        dste.renderOnScreen(relX+4,relY+14,selTextInd);
    }

    private void onTextCenterChange() {
        DirectionSignTileEntity dste = getTileEntity();
        isTextCenter = centerText.func_212942_a();
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
        boolean newBool = box.func_212942_a();
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
        int text_length = t.getLength();
        DirectionSignTileEntity dste = getTileEntity();
        int ind= (selTextInd/2);
        float x;
        if (isTextCenter){
            centerText.active = (text_length<begTextLength);

            if (isEndSelected()){
                return;
            }
            x = (panelLength - text_length) / 2.0F;
        } else if (form == Form.RECTANGLE || dste.isRightArrow(ind)) {
            centerText.active = true;
            x = (selTextInd % 2) * (panelLength - text_length - 2*st_gap) ;
        }else {
            x = ((selTextInd+1)%2)* (panelLength - text_length - 2*st_gap) ;
        }
        x+=xOrigin+st_gap;
        int text_height = t.getHeight();
        int y_offset = 25 * ind + ind / 2;
        int height = (ind % 2 == 0) ? 25 : (ind == 1) ? 26 : 27;
        float y = (height - text_height) / 2.0F + y_offset;
        t.setPosition(x,y);
        dste.setText(selTextInd/2,isEndSelected(),t);
        Networking.INSTANCE.sendToServer(new PacketAddOrEditText(panelPos,t,selTextInd));
    }

    @Override
    public Screen getScreen() {
        return this;
    }

    private DirectionSignTileEntity getTileEntity(){
        World world = this.minecraft.world;
        TileEntity te = world.getTileEntity(panelPos);
        if (te != null && te instanceof DirectionSignTileEntity){
            return (DirectionSignTileEntity) te;
        }
        if (te == null)throw new NullPointerException("Tile entity at panelPos is not in desired place !");
        throw new IllegalStateException("Direction Screen need direction sign tile entity in place !");
    }

    public void changeArrowSide(int ind,boolean b){
        DirectionSignTileEntity dste = getTileEntity();
        dste.flipText(ind);
        Networking.INSTANCE.sendToServer(new PacketFlipText(panelPos,ind));
        dste.updateBoolean(ind+5,b);
        Networking.INSTANCE.sendToServer(new PacketSetBoolean(panelPos,ind+5,b));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int ind = getCellClickedInd(mouseX,mouseY,button);
        int guiLeft = (this.width-LENGTH) / 2;
        int guiTop = (this.height-HEIGHT) / 2;
        if (ind != -1){
            SignMod.LOGGER.info("click on a gray cell : "+ ind);
            selTextInd = ind;
            onChangeIndice();
        } else if (mouseX>guiLeft+338 && mouseX<guiLeft+362 && mouseY>guiTop+85 && mouseY<guiTop+109){
            isBgColorDisplayed = !isBgColorDisplayed;
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            updateSliderDisplay();
        }
        return super.mouseClicked(mouseX,mouseY,button);
    }

    private void onChangeIndice() {
        if (selTextInd == -1){
            addOrSetTextButton.active = false;
            addOrSetTextButton.visible = false;
        } else if (!getText().isEmpty()){
            addOrSetTextButton.setMessage("Set Text");
        }else {
            addOrSetTextButton.setMessage("Add Text");
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

    private int getCellClickedInd(double mouseX, double mouseY, int button) {
        DirectionSignTileEntity dste = getTileEntity();
        for (int i=0;i<10;i++){
            if (isInCell(i,mouseX,mouseY) && dste.isCellPresent(i/2)){
                return i;
            }
        }
        return -1;
    }

    private boolean isInCell(int i,double mouseX,double mouseY) {
        int guiLeft = (this.width-LENGTH) / 2;
        int guiTop = (this.height-HEIGHT) / 2;
        boolean isEnd = (i%2 == 1);
        DirectionSignTileEntity dste =getTileEntity();
        int x1;
        int ind = i/2;
        int length;
        if (isTextCenter){
            if (isEnd){
                return false;
            }
            x1 = guiLeft+st_gap;
            length = panelLength-2*st_gap;
        } else {
            if (form == Form.RECTANGLE || dste.isRightArrow(ind)) {
                x1 = guiLeft + ((isEnd) ? panelLength-st_gap-endTextLength : st_gap);
            } else {
                x1 = guiLeft + ((isEnd) ? st_gap : endTextLength+st_gap+center_gap);
            }
            length = (isEnd) ? endTextLength : panelLength-endTextLength-8;
        }
        //a gap of 25 and then 26
        int y1 = guiTop+16+(25*ind)+ind-(ind==0?0:1);

        return (mouseX>x1 && mouseX<x1+length) && (mouseY>y1 && mouseY<y1+21);
    }

    private void openTextGui() {
        Minecraft.getInstance().displayGuiScreen(null);
        DirectionSignTileEntity dste = getTileEntity();
        if (selTextInd != -1){
            Text t = getText();
            AddTextScreen.open(this,t);
        }
    }

    public boolean isEndSelected(){
        return selTextInd%2 == 1;
    }

    //when ticking one of the 5 part direction we need to update cursor possibility
    public void updateCursorAuthorisation(int ind) {
        DirectionSignTileEntity dste = getTileEntity();
        boolean isPartPresent = getPlacement(ind);
        switch (ind){
            //in all case if we connect two plain part we disable the last one
            case 0:
                //had panel 1 directly connected to panel 2
                if (dste.is12connected()){
                    arrowDirection[1].active = !isPartPresent;
                }
                break;
            case 1:
                if (dste.hasPanel(1)){
                    //if we join the two panel and there are not on the same arrow direction
                    if (isPartPresent && (getPlacement(5) != getPlacement(6))){
                        arrowDirection[1].onPress();
                    }
                    arrowDirection[1].active = !isPartPresent;
                }
                break;
            case 2:
                if (dste.is23connected()){
                    arrowDirection[2].active = !isPartPresent;
                }
                break;
            case 3:
                if (dste.hasPanel(2)){
                    //same as case 1
                    if (isPartPresent && (getPlacement(6) != getPlacement(7))) {
                        arrowDirection[2].onPress();
                    }
                    arrowDirection[2].active = !isPartPresent;
                }
                break;
        }
    }

    public void updateOtherArrowSide(int ind) {
        DirectionSignTileEntity dste = getTileEntity();
        if (ind == 0 && dste.is12connected()){
            arrowDirection[1].onPress();
        }else if (ind == 1 && dste.is23connected()){
            arrowDirection[2].onPress();
        }
    }
}
