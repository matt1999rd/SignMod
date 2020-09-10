package fr.mattmouss.signs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.screenutils.ColorOption;
import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.screenutils.PencilOption;
import fr.mattmouss.signs.gui.widget.ColorSlider;
import fr.mattmouss.signs.gui.widget.DirectionCursorButton;
import fr.mattmouss.signs.gui.widget.DirectionPartBox;
import fr.mattmouss.signs.networking.Networking;
import fr.mattmouss.signs.networking.PacketChangeColor;
import fr.mattmouss.signs.networking.PacketSetBoolean;
import fr.mattmouss.signs.tileentity.DirectionSignTileEntity;
import fr.mattmouss.signs.tileentity.DrawingSignTileEntity;
import fr.mattmouss.signs.util.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.awt.*;

public class DirectionScreen extends Screen implements IWithEditTextScreen {

    private static final int LENGTH = 324;
    private static final int HEIGHT = 245;
    private int selPanel = 2;
    private static final int white = MathHelper.rgb(1.0F,1.0F,1.0F);
    private static ColorOption backgroundColorOption;
    private static ColorOption edgingColorOption;

    Form form ;
    BlockPos panelPos;
    DirectionPartBox[] changeBool = new DirectionPartBox[5];
    ColorSlider[] sliders = new ColorSlider[6];
    DirectionCursorButton[] arrowDirection = new DirectionCursorButton[3];
    Button applyColorButton;
    Button[] choiceButton = new Button[3];

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
        for (int i=0;i<2;i++){
            arrowDirection[i] = new DirectionCursorButton(relX,relY,b->{},this,i);
            addButton(arrowDirection[i]);
        }
        for (int i=0;i<6;i++){
            ColorOption opt = (i<3)? backgroundColorOption : edgingColorOption;
            sliders[i] = new ColorSlider(relX+160-(i/3)*156,relY+172+i%3*25,opt, ColorType.byIndex(i%3));
            addButton(sliders[i]);
        }
        applyColorButton = new Button(relX+109,relY+147,73,20,"apply Color",b->applyColor());
        addButton(applyColorButton);
        for (int i=0;i<3;i++){
            int finalI = i;
            choiceButton[i] = new Button(relX+100+10*i,relY+200,10,10,String.valueOf(i), b->{
                selPanel = finalI+1;
            });
            addButton(choiceButton[i]);
        }
    }

    private void applyColor() {
        DirectionSignTileEntity tileEntity = getTileEntity();
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
        return dste.isRightArrow(i-4);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(relX, relY ,this.blitOffset,0.0F, 0.0F, LENGTH, HEIGHT,256,512);
        super.render(mouseX, mouseY, partialTicks);
        GlStateManager.enableBlend();
        AbstractGui.fill(relX+76,relY+152,relX+76+9,relY+152+9,edgingColorOption.getColor());
        AbstractGui.fill(relX+232,relY+152,relX+232+9,relY+152+9,backgroundColorOption.getColor());
    }

    @Override
    public Form getForm() {
        return form;
    }

    public void updateBoolean(int ind){
        DirectionPartBox box = changeBool[ind];
        boolean newBool = box.func_212942_a();
        DirectionSignTileEntity dste = getTileEntity();
        Networking.INSTANCE.sendToServer(new PacketSetBoolean(panelPos,ind,newBool));
        dste.updateBoolean(ind,newBool);
    }

    @Override
    public void addOrEditText(Text t) {

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
        dste.updateBoolean(ind+5,b);
        Networking.INSTANCE.sendToServer(new PacketSetBoolean(panelPos,ind+5,b));
    }
}
