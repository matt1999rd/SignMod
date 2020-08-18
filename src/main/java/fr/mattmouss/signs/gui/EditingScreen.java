package fr.mattmouss.signs.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.networking.Networking;
import fr.mattmouss.signs.networking.PacketAddOrEditText;
import fr.mattmouss.signs.tileentity.DrawingSignTileEntity;
import fr.mattmouss.signs.tileentity.EditingSignTileEntity;
import fr.mattmouss.signs.util.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class EditingScreen extends Screen implements IWithEditTextScreen{

    Form form ;
    BlockPos panelPos;
    ResourceLocation LET_WAY = new ResourceLocation(SignMod.MODID,"textures/gui/let_way_gui.png");
    ResourceLocation STOP = new ResourceLocation(SignMod.MODID,"textures/gui/stop_gui.png");
    private static final int LENGTH = 144;
    private static final int HEIGHT = 165;

    protected EditingScreen(Form form,BlockPos panelPos) {
        super(new StringTextComponent("Editing Screen"));
        if (!form.isForEditing())throw new IllegalArgumentException("form does not work for form given : "+form);
        this.form = form;
        this.panelPos = panelPos;
    }

    @Override
    protected void init() {
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        addButton(new Button(relX+32,relY+137,74,20,"Edit Text",b->openTextGui()));
    }

    private void openTextGui() {
        Minecraft.getInstance().displayGuiScreen(null);
        EditingSignTileEntity este = getTileEntity();
        Text t = este.getText();
        t.setPosition(0,50);
        AddTextScreen.open(this,t);
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        ResourceLocation location = getTexture();
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        this.minecraft.getTextureManager().bindTexture(location);
        this.blit(relX,relY,0,0,LENGTH,HEIGHT);
        super.render(p_render_1_,p_render_2_,p_render_3_);
        EditingSignTileEntity este = getTileEntity();
        este.renderOnScreen(relX+8,relY+8,-1);
        GlStateManager.enableBlend();
    }

    public static void open(Form form,BlockPos panelPos){
        Minecraft.getInstance().displayGuiScreen(new EditingScreen(form, panelPos));
    }

    private EditingSignTileEntity getTileEntity(){
        World world = this.minecraft.world;
        TileEntity te = world.getTileEntity(panelPos);
        if (te != null && te instanceof EditingSignTileEntity){
            return (EditingSignTileEntity) te;
        }
        if (te == null)throw new NullPointerException("Tile entity at panelPos is not in desired place !");
        throw new IllegalStateException("Editing Screen need editing sign tile entity in place !");
    }

    private ResourceLocation getTexture(){
        if (form == Form.OCTOGONE)return STOP;
        return LET_WAY;
    }

    @Override
    public Form getForm() {
        return form;
    }

    @Override
    public void addOrEditText(Text t) {
        EditingSignTileEntity este = getTileEntity();
        Networking.INSTANCE.sendToServer(new PacketAddOrEditText(panelPos,t,-1));
        este.setText(t);
    }

    @Override
    public Screen getScreen() {
        return this;
    }
}
