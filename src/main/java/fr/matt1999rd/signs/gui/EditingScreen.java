package fr.matt1999rd.signs.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.networking.Networking;
import fr.matt1999rd.signs.networking.PacketAddOrEditText;
import fr.matt1999rd.signs.tileentity.EditingSignTileEntity;
import fr.matt1999rd.signs.util.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;

import static net.minecraft.network.chat.Component.nullToEmpty;

public class EditingScreen extends Screen implements IWithEditTextScreen{

    Form form ;
    BlockPos panelPos;
    ResourceLocation LET_WAY = new ResourceLocation(SignMod.MODID,"textures/gui/let_way_gui.png");
    ResourceLocation STOP = new ResourceLocation(SignMod.MODID,"textures/gui/stop_gui.png");
    private static final int LENGTH = 144;
    private static final int HEIGHT = 165;

    protected EditingScreen(Form form,BlockPos panelPos) {
        super(new TextComponent("Editing Screen"));
        if (form.isNotForEditing())throw new IllegalArgumentException("form does not work for form given : "+form);
        this.form = form;
        this.panelPos = panelPos;
    }

    @Override
    protected void init() {
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        addRenderableWidget(new Button(relX+32,relY+137,74,20,nullToEmpty("Edit Text"),b->openTextGui()));
    }

    private void openTextGui() {
        Minecraft.getInstance().setScreen(null);
        EditingSignTileEntity este = getTileEntity();
        Text t = este.getText();
        if (t.isEmpty()){
            AddTextScreen.open(this,null);
        }else {
            AddTextScreen.open(this,t);
        }
    }

    @Override
    public void render(PoseStack stack,int p_render_1_, int p_render_2_, float p_render_3_) {
        ResourceLocation location = getTexture();
        int relX = (this.width-LENGTH) / 2;
        int relY = (this.height-HEIGHT) / 2;
        RenderSystem.setShaderTexture(0,location);
        this.blit(stack,relX,relY,0,0,LENGTH,HEIGHT);
        EditingSignTileEntity este = getTileEntity();
        int dec = (form == Form.OCTAGON)? 8 : 4;
        este.renderOnScreen(stack,relX+dec,relY+dec);
        super.render(stack,p_render_1_,p_render_2_,p_render_3_);
    }


    public static void open(Form form,BlockPos panelPos){
        Minecraft.getInstance().setScreen(new EditingScreen(form, panelPos));
    }

    private EditingSignTileEntity getTileEntity(){
        assert this.minecraft != null;
        Level world = this.minecraft.level;
        assert world != null;
        BlockEntity te = world.getBlockEntity(panelPos);
        if (te instanceof EditingSignTileEntity){
            return (EditingSignTileEntity) te;
        }
        if (te == null)throw new NullPointerException("Tile entity at panelPos is not in desired place !");
        throw new IllegalStateException("Editing Screen need editing sign tile entity in place !");
    }

    private ResourceLocation getTexture(){
        if (form == Form.OCTAGON)return STOP;
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
