package fr.mattmouss.signs.tileentity.primary;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.capabilities.PSCapability;
import fr.mattmouss.signs.capabilities.PSStorage;
import fr.mattmouss.signs.enums.PSDisplayMode;
import fr.mattmouss.signs.enums.PSPosition;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.tileentity.PanelTileEntity;
import fr.mattmouss.signs.tileentity.TEType;
import fr.mattmouss.signs.util.QuadPSPosition;
import fr.mattmouss.signs.util.Text;
import fr.mattmouss.signs.util.Vec2i;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;


public class PlainSquareSignTileEntity extends PanelTileEntity {

    private LazyOptional<PSStorage> storage = LazyOptional.of(this::getStorage).cast();

    private final int SCREEN_LENGTH = 128;

    public PlainSquareSignTileEntity() {
        super(TEType.PLAIN_SQUARE_SIGN);
    }

    @Override
    public void tick() {
        BlockState state = getBlockState();
        if (!state.get(AbstractPanelBlock.GRID)){
            //if it is a support with grid
            super.tick();
        }
    }

    private PSStorage getStorage() {
        return new PSStorage();
    }

    public void renderOnScreen(int guiLeft, int guiTop) {
        //display background
        AbstractGui.fill(guiLeft,guiTop,guiLeft+SCREEN_LENGTH,guiTop+SCREEN_LENGTH,getBackgroundColor().getRGB());
        renderLimit(guiLeft,guiTop);
        renderText(guiLeft,guiTop);
        renderScheme(guiLeft,guiTop);
    }

    private void renderLimit(int guiLeft, int guiTop){
        int fg_color = getForegroundColor().getRGB();
        int limitLength = 2;
        //optimised so that no bar are recovered (bar of size sl-2 and 2)
        //top
        AbstractGui.fill(guiLeft,guiTop,guiLeft+SCREEN_LENGTH-limitLength,guiTop+limitLength,fg_color);
        //right
        AbstractGui.fill(guiLeft+SCREEN_LENGTH-limitLength,guiTop,guiLeft+SCREEN_LENGTH,guiTop+SCREEN_LENGTH-limitLength,fg_color);
        //bottom
        AbstractGui.fill(guiLeft+limitLength,guiTop+SCREEN_LENGTH-limitLength,guiLeft+SCREEN_LENGTH,guiTop+SCREEN_LENGTH,fg_color);
        //left
        AbstractGui.fill(guiLeft,guiTop+limitLength,guiLeft+limitLength,guiTop+SCREEN_LENGTH,fg_color);
    }

    private void renderText(int guiLeft, int guiTop){
        PSDisplayMode mode = getMode();
        GlStateManager.enableBlend();
        float scaleX = SCREEN_LENGTH/(mode.is2by2()?64.0F:96.0F);
        float scaleY = SCREEN_LENGTH/64.0F;
        for (int i=0;i<mode.getTotalText();i++){
            Text t = getText(i);
            Text rescaledText = t.rescale(scaleX,scaleY);
            rescaledText.renderOnScreen(guiLeft,guiTop,scaleX/scaleY);
        }
    }

    private void renderScheme(int guiLeft, int guiTop){
        PSDisplayMode mode = getMode();
        GlStateManager.enableBlend();
        float scaleX =SCREEN_LENGTH/(mode.is2by2()?32.0F:48.0F);
        float scaleY =SCREEN_LENGTH/32.0F;

        switch (mode){
            case EXIT:
                float length = 12.0F;
                float xBase = guiLeft+25.0F*scaleX;
                float yBase = guiTop+1.0F*scaleY;
                float u1 = 0.0F;
                float v1 = 14.0F;
                float u2 = u1 + length;
                float v2 = v1 + length;
                //rendering of the arrow
                renderTexture(xBase,yBase,xBase+length/2*scaleX,yBase+length/2*scaleY,u1,v1,u2,v2);
                break;
            case DIRECTION:
                //arrowId = 0 -> left arrow / 1 -> center arrow / 2 -> right arrow
                int arrowId = getArrowId();
                if (arrowId == -1)return;
                u1 = 20.0F*arrowId;
                v1 = 0.0F;
                u2 = 20.0F*(arrowId+1);
                v2 = 14.0F;
                xBase = guiLeft+34.0F*scaleX;
                yBase = guiTop+23.5F*scaleY;
                renderTexture(xBase,yBase,xBase+10.0F*scaleX,yBase+7.0F*scaleY,u1,v1,u2,v2);
                xBase -= 16.0F*scaleX;
                renderTexture(xBase,yBase,xBase+10.0F*scaleX,yBase+7.0F*scaleY,u1,v1,u2,v2);
                xBase -= 16.0F*scaleX;
                renderTexture(xBase,yBase,xBase+10.0F*scaleX,yBase+7.0F*scaleY,u1,v1,u2,v2);
                break;
            case SCH_EXIT:
                xBase = guiLeft+12.0F*scaleX;
                yBase = guiTop+12.5F*scaleY;
                u1= 12.0F;
                u2= 32.0F;
                v1= 14.0F;
                v2= 52.0F;
                //render of scheme
                renderTexture(xBase,yBase,xBase+10.0F*scaleX,yBase+19.0F*scaleY,u1,v1,u2,v2);
                break;
            case SCH_MUL_EXIT:
                xBase = guiLeft+9.0F*scaleX;
                yBase = guiTop+8.0F*scaleY;
                u1= 32.0F;
                u2= 84.0F;
                v1= 14.0F;
                v2= 61.0F;
                //render of scheme
                renderTexture(xBase,yBase,xBase+26.0F*scaleX,yBase+23.5F*scaleY,u1,v1,u2,v2);
                break;
        }

    }

    private void renderTexture(float x1,float y1,float x2,float y2,float u1,float v1,float u2,float v2){
        ResourceLocation location = new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/ps_arrow.png");
        float texLength = 84.0F;
        float texHeight = 61.0F;
        Color color = getForegroundColor();
        Minecraft.getInstance().getTextureManager().bindTexture(location);
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        int red,green,blue,alpha;

        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        alpha = color.getAlpha();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        builder.pos(x1, y1, 0.0F).tex(u1/texLength, v1/texHeight).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y2, 0.0F).tex(u1/texLength, v2/texHeight).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y2, 0.0F).tex(u2/texLength, v2/texHeight).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y1, 0.0F).tex(u2/texLength, v1/texHeight).color(red, green, blue, alpha).endVertex();

        tessellator.draw();
    }


    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 9, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        this.read(nbt);
    }

    public void registerData(PSPosition position, PSDisplayMode mode){
        storage.ifPresent(psStorage -> {
            psStorage.setInternVariable(position,mode);
        });
        List<QuadPSPosition> quadPositions = mode.getTextPosition();
        int textIndex = 0;
        for (QuadPSPosition quadPosition : quadPositions){
            int xBase = quadPosition.getPosition().getX();
            int yBase = quadPosition.getPosition().getY();
            int maxNumber = quadPosition.getMaxText();
            for (int i=0;i<maxNumber;i++){
                Text t = new Text(xBase,yBase+9*i,"Test text "+textIndex,getForegroundColor(),1);
                setText(t,textIndex);
                textIndex++;
            }
        }
    }

    public PSPosition getPosition(){
        return storage.map(PSStorage::getPosition).orElse(PSPosition.DOWN_LEFT);
    }

    public PSDisplayMode getMode(){
        return storage.map(PSStorage::getDisplayMode).orElse(PSDisplayMode.DIRECTION);
    }

    public Color getBackgroundColor() { return storage.map(PSStorage::getBackgroundColor).orElse(Color.BLACK); }

    public Color getForegroundColor() { return storage.map(PSStorage::getForegroundColor).orElse(Color.BLACK); }

    public void setBackgroundColor(int color) { storage.ifPresent(psStorage -> psStorage.setBackgroundColor(color));}

    public void setForegroundColor(int color) { storage.ifPresent(psStorage -> psStorage.setForegroundColor(color));}

    public int getArrowId(){ return storage.map(PSStorage::getArrowId).orElse(-1);}

    public void setArrowId(int arrowId){ storage.ifPresent(psStorage -> psStorage.setArrowId(arrowId));}

    public Text getText(int ind) { return storage.map(psStorage -> psStorage.getText(ind)).orElse(Text.getDefaultText());}

    public void setText(Text newText,int ind) { storage.ifPresent(psStorage -> psStorage.setText(newText,ind));}

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == PSCapability.PS_STORAGE){
            return storage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT storage_tag = compound.getCompound("plain_square");
        getCapability(PSCapability.PS_STORAGE).ifPresent(s -> ((INBTSerializable<CompoundNBT>) s).deserializeNBT(storage_tag));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        getCapability(PSCapability.PS_STORAGE).ifPresent(storage -> {
            CompoundNBT compoundNBT = ((INBTSerializable<CompoundNBT>) storage).serializeNBT();
            tag.put("plain_square", compoundNBT);
        });
        return super.write(tag);
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

}
