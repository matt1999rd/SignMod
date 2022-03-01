package fr.matt1999rd.signs.tileentity.primary;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.PSDisplayMode;
import fr.matt1999rd.signs.enums.PSPosition;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.matt1999rd.signs.tileentity.PanelTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;
import fr.matt1999rd.signs.util.QuadPSPosition;
import fr.matt1999rd.signs.util.Text;
import fr.matt1999rd.signs.capabilities.PSCapability;
import fr.matt1999rd.signs.capabilities.PSStorage;
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
import net.minecraft.util.math.vector.Vector2f;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;


public class PlainSquareSignTileEntity extends PanelTileEntity {

    private LazyOptional<PSStorage> storage = LazyOptional.of(this::getStorage).cast();

    public static final int SCREEN_LENGTH = 128;
    private final ResourceLocation TEXT = new ResourceLocation(SignMod.MODID,"textures/gui/letter.png");

    public PlainSquareSignTileEntity() {
        super(TEType.PLAIN_SQUARE_SIGN);
    }

    @Override
    public double getViewDistance() {
        return 256.0D;
    }

    @Override
    public void tick() {
        BlockState state = getBlockState();
        if (!state.getValue(AbstractPanelBlock.GRID)){
            //if it is a support with grid
            super.tick();
        }
    }

    private PSStorage getStorage() {
        return new PSStorage();
    }

    public void renderOnScreen(MatrixStack stack,int guiLeft, int guiTop, int selTextIndex) {
        //display background
        AbstractGui.fill(stack,guiLeft,guiTop,guiLeft+SCREEN_LENGTH,guiTop+SCREEN_LENGTH,getBackgroundColor().getRGB());
        renderLimit(stack,guiLeft,guiTop);
        renderText(stack,guiLeft,guiTop,selTextIndex);
        renderScheme(guiLeft,guiTop);
    }

    private void renderLimit(MatrixStack stack,int guiLeft, int guiTop){
        int fg_color = getForegroundColor().getRGB();
        int limitLength = 2;
        //optimised so that no bar are recovered (bar of size sl-2 and 2)
        //top
        AbstractGui.fill(stack,guiLeft,guiTop,guiLeft+SCREEN_LENGTH-limitLength,guiTop+limitLength,fg_color);
        //right
        AbstractGui.fill(stack,guiLeft+SCREEN_LENGTH-limitLength,guiTop,guiLeft+SCREEN_LENGTH,guiTop+SCREEN_LENGTH-limitLength,fg_color);
        //bottom
        AbstractGui.fill(stack,guiLeft+limitLength,guiTop+SCREEN_LENGTH-limitLength,guiLeft+SCREEN_LENGTH,guiTop+SCREEN_LENGTH,fg_color);
        //left
        AbstractGui.fill(stack,guiLeft,guiTop+limitLength,guiLeft+limitLength,guiTop+SCREEN_LENGTH,fg_color);
    }

    private void renderText(MatrixStack stack,int guiLeft, int guiTop,int selTextIndex){
        PSDisplayMode mode = getMode();
        float scaleX = SCREEN_LENGTH/(mode.is2by2()?64.0F:96.0F);
        float scaleY = SCREEN_LENGTH/64.0F;
        for (int i=0;i<mode.getTotalText();i++){
            Text t = getText(i);
            Text rescaleText = new Text(t);
            rescaleText.changeScale(1); // a text is rendered on screen with two pixels length
            rescaleText.renderOnScreen(stack,guiLeft,guiTop,scaleX/scaleY,false);
            renderTextLimit(rescaleText,guiLeft,guiTop,i==selTextIndex,scaleX,scaleY);
        }
    }

    private void renderScheme(int guiLeft, int guiTop){
        PSDisplayMode mode = getMode();
        GlStateManager._enableBlend();
        float scaleX =2*SCREEN_LENGTH/(mode.is2by2()?64.0F:96.0F);
        float scaleY =2*SCREEN_LENGTH/64.0F;
        float xBase = guiLeft+mode.getTextureXOrigin()*scaleX;
        float yBase = guiTop+ mode.getTextureYOrigin()*scaleY;
        float length = mode.getTexLength()*scaleX;
        float height = mode.getTexHeight()*scaleY;
        Vector2f uvOrigin = mode.getUVOrigin(getArrowId());
        Vector2f uvDimension = mode.getUVDimension();
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
        Minecraft.getInstance().getTextureManager().bind(location);
        GlStateManager._color4f(1.0F,1.0F,1.0F,1.0F);
        int red,green,blue,alpha;

        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        alpha = color.getAlpha();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        builder.vertex(x1, y1, 0.0F).uv(u1/texLength, v1/texHeight).color(red, green, blue, alpha).endVertex();
        builder.vertex(x1, y2, 0.0F).uv(u1/texLength, v2/texHeight).color(red, green, blue, alpha).endVertex();
        builder.vertex(x2, y2, 0.0F).uv(u2/texLength, v2/texHeight).color(red, green, blue, alpha).endVertex();
        builder.vertex(x2, y1, 0.0F).uv(u2/texLength, v1/texHeight).color(red, green, blue, alpha).endVertex();

        tessellator.end();
    }

    private void renderTextLimit(Text t,int guiLeft,int guiTop,boolean isSelected,float scaleX,float scaleY){
        float L = t.getLength()*scaleX/scaleY;
        float h = t.getHeight();
        float u = (isSelected) ? 0 :1/256.0F;
        float v = 35/256.0F+u;
        float horDu = (L+2)/256.0F;
        float verDu = 1/256.0F;
        float horDv = verDu;
        float verDv = (h+2)/256.0F;
        guiLeft+=t.getX()*scaleX;
        guiTop+=t.getY()*scaleY;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        //up bar
        builder.vertex(guiLeft-1,guiTop-1,0).uv(u,v).endVertex();
        builder.vertex(guiLeft-1,guiTop,0).uv(u,v+horDv).endVertex();
        builder.vertex(guiLeft+L+1,guiTop,0).uv(u+horDu,v+horDv).endVertex();
        builder.vertex(guiLeft+L+1,guiTop-1,0).uv(u+horDu,v).endVertex();
        //down bar
        builder.vertex(guiLeft-1,guiTop+h,0).uv(u,v).endVertex();
        builder.vertex(guiLeft-1,guiTop+h+1,0).uv(u,v+horDv).endVertex();
        builder.vertex(guiLeft+L+1,guiTop+h+1,0).uv(u+horDu,v+horDv).endVertex();
        builder.vertex(guiLeft+L+1,guiTop+h,0).uv(u+horDu,v).endVertex();
        //left bar
        builder.vertex(guiLeft-1,guiTop-1,0).uv(u,v).endVertex();
        builder.vertex(guiLeft-1,guiTop+h+1,0).uv(u,v+verDv).endVertex();
        builder.vertex(guiLeft,guiTop+h+1,0).uv(u+verDu,v+verDv).endVertex();
        builder.vertex(guiLeft,guiTop-1,0).uv(u+verDu,v).endVertex();
        //right bar
        builder.vertex(guiLeft+L,guiTop-1,0).uv(u,v).endVertex();
        builder.vertex(guiLeft+L,guiTop+h+1,0).uv(u,v+verDv).endVertex();
        builder.vertex(guiLeft+L+1,guiTop+h+1,0).uv(u+verDu,v+verDv).endVertex();
        builder.vertex(guiLeft+L+1,guiTop-1,0).uv(u+verDu,v).endVertex();
        //finish drawing
        tessellator.end();
    }


    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 9, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        this.load(this.getBlockState(),nbt);
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
    public void load(BlockState state,CompoundNBT compound) {
        CompoundNBT storage_tag = compound.getCompound("plain_square");
        getCapability(PSCapability.PS_STORAGE).ifPresent(s -> ((INBTSerializable<CompoundNBT>) s).deserializeNBT(storage_tag));
        super.load(state,compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        getCapability(PSCapability.PS_STORAGE).ifPresent(storage -> {
            CompoundNBT compoundNBT = ((INBTSerializable<CompoundNBT>) storage).serializeNBT();
            tag.put("plain_square", compoundNBT);
        });
        return super.save(tag);
    }

    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

}
