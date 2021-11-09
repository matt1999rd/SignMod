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
            Text rescaleText = new Text(t);
            rescaleText.changeScale(1); // a text is rendered on screen with two pixels length
            rescaleText.renderOnScreen(guiLeft,guiTop,scaleX/scaleY,false);
        }
    }

    private void renderScheme(int guiLeft, int guiTop){
        PSDisplayMode mode = getMode();
        GlStateManager.enableBlend();
        float scaleX =2*SCREEN_LENGTH/(mode.is2by2()?64.0F:96.0F);
        float scaleY =2*SCREEN_LENGTH/64.0F;
        float xBase = guiLeft+mode.getTextureXOrigin()*scaleX;
        float yBase = guiTop+ mode.getTextureYOrigin()*scaleY;
        float length = mode.getTexLength()*scaleX;
        float height = mode.getTexHeight()*scaleY;
        Vec2f uvOrigin = mode.getUVOrigin(getArrowId());
        Vec2f uvDimension = mode.getUVDimension();
        renderTexture(
                xBase,yBase,xBase+length,yBase+height,
                uvOrigin.x,uvOrigin.y,uvOrigin.x+uvDimension.x,uvOrigin.y+uvDimension.y);
        if (mode == PSDisplayMode.DIRECTION){
            float spaceBetweenArrow = 7.0F*scaleX;
            renderTexture(
                    xBase+spaceBetweenArrow+length,yBase,
                    xBase+spaceBetweenArrow+2*length,yBase+height,
                    uvOrigin.x, uvOrigin.y,
                    uvOrigin.x+uvDimension.x,uvOrigin.y+uvDimension.y
            );
            renderTexture(
                    xBase+2*(spaceBetweenArrow+length),yBase,
                    xBase+2*(spaceBetweenArrow+length)+length,yBase+height,
                    uvOrigin.x, uvOrigin.y,
                    uvOrigin.x+uvDimension.x,uvOrigin.y+uvDimension.y
            );
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
