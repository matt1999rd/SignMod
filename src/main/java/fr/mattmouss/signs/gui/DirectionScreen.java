package fr.mattmouss.signs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.gui.widget.DirectionPartBox;
import fr.mattmouss.signs.tileentity.DirectionSignTileEntity;
import fr.mattmouss.signs.tileentity.DrawingSignTileEntity;
import fr.mattmouss.signs.util.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class DirectionScreen extends Screen implements IWithEditTextScreen {

    private static final int LENGTH = 200;
    private static final int HEIGHT = 200;
    private static final int white = MathHelper.rgb(1.0F,1.0F,1.0F);
    Form form ;
    BlockPos panelPos;
    DirectionPartBox[] changeBool = new DirectionPartBox[8];

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
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        for (int i=0;i<8;i++){
            boolean b = getPlacement(i);
            changeBool[i] = new DirectionPartBox(i,this,relX,relY,b);
            addButton(changeBool[i]);
        }
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
        blit(relX, relY , 0, 0, LENGTH, HEIGHT);
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public Form getForm() {
        return form;
    }

    public void updateBoolean(int ind){
        DirectionPartBox box = changeBool[ind];
        boolean newBool = box.func_212942_a();
        DirectionSignTileEntity dste = getTileEntity();
        if (ind==1){
            if (newBool)dste.add12connection();
            else dste.remove12connection();
        }else if (ind==3){
            if (newBool)dste.add23connection();
            else dste.remove23connection();
        } else if (ind<5){
            int newInd= (ind+2)/2;
            if (newBool)dste.addPanel(newInd);
            else dste.removePanel(newInd);
        } else {
            int newInd = ind-4;
            dste.changeArrowSide(newInd);
        }
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
}
