package fr.mattmouss.signs.tileentity;

import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.capabilities.SignCapability;
import fr.mattmouss.signs.capabilities.SignStorage;
import fr.mattmouss.signs.enums.ClientAction;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.util.Text;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DrawingSignTileEntity extends PanelTileEntity {

    private LazyOptional<SignStorage> storage = LazyOptional.of(this::getStorage).cast();
    private final ResourceLocation TEXT = new ResourceLocation(SignMod.MODID,"textures/gui/letter.png");

    private SignStorage getStorage() {
        return new SignStorage();
    }

    public DrawingSignTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    protected abstract Form getForm();

    @OnlyIn(Dist.CLIENT)
    public int getPixelColor(int i,int j){
        return storage.map(signStorage -> {
            return signStorage.getRGBPixel(i,j);
        }).orElse(0);
    }

    public int getNumberOfText(){
        return storage.map(signStorage -> signStorage.getTexts().size()).orElse(0);
    }

    public Text getText(int n){
        return storage.map(signStorage -> {
            int lim = signStorage.getTexts().size();
            if (n>lim || n<0)return null;
            return signStorage.getTexts().get(n);
        }).orElse(null);
    }

    public void addOrEditText(Text newText,int ind){
        if (ind == -1){
            storage.ifPresent(signStorage -> signStorage.addText(newText));
        } else {
            storage.ifPresent(signStorage -> signStorage.setText(newText,ind));
        }
    }

    public void delText(int ind){
        storage.ifPresent(signStorage -> signStorage.delText(ind));
    }

    @Override
    public void tick() {
        BlockState state = getBlockState();
        if (!state.get(AbstractPanelBlock.GRID)){
            //if it is a support with grid
            super.tick();
        }
    }

    @Override
    public void renderOnScreen(int guiLeft, int guiTop,int selTextInd) {
        Form form = getForm();
        for (int i=0;i<128;i++){
            for (int j=0;j<128;j++){
                if (form.isIn(i,j)){
                    int color = getPixelColor(i,j);
                    AbstractGui.fill(guiLeft+i,guiTop+j,guiLeft+i+1,guiTop+j+1,color);
                }
            }
        }
        List<Text> texts = storage.map(signStorage -> signStorage.getTexts()).orElse(new ArrayList<Text>());
        Minecraft.getInstance().getTextureManager().bindTexture(TEXT);
        AtomicInteger ind = new AtomicInteger(0);
        texts.forEach(text -> {
            text.renderOnScreen(guiLeft,guiTop);
            renderTextLimit(text,guiLeft,guiTop,ind.getAndIncrement() == selTextInd);
        });
    }

    private void renderTextLimit(Text t,int guiLeft,int guiTop,boolean isSelected){
        int L = t.getLength();
        int h = t.getHeight();
        float u = (isSelected) ? 0 :1/256.0F;
        float v = 35/256.0F+u;
        float horDu = (L+2)/256.0F;
        float verDu = 1/256.0F;
        float horDv = verDu;
        float verDv = (h+2)/256.0F;
        guiLeft+=t.getX();
        guiTop+=t.getY();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        //up bar
        builder.pos(guiLeft-1,  guiTop-1,0).tex(   u,          v)      .endVertex();
        builder.pos(guiLeft-1,     guiTop,  0).tex(   u,       v+horDv).endVertex();
        builder.pos(guiLeft+L+1,   guiTop,  0).tex(u+horDu, v+horDv).endVertex();
        builder.pos(guiLeft+L+1,guiTop-1,0).tex(u+horDu,    v)      .endVertex();
        //down bar
        builder.pos(guiLeft-1,  guiTop+h,  0).tex(   u    ,     v)      .endVertex();
        builder.pos(guiLeft-1,  guiTop+h+1,0).tex(   u    ,  v+horDv).endVertex();
        builder.pos(guiLeft+L+1,guiTop+h+1,0).tex(u+horDu,v+horDv).endVertex();
        builder.pos(guiLeft+L+1,guiTop+h,  0).tex(u+horDu,   v)      .endVertex();
        //left bar
        builder.pos(guiLeft-1,guiTop-1,  0).tex(   u,         v)      .endVertex();
        builder.pos(guiLeft-1,guiTop+h+1,0).tex(   u,      v+verDv).endVertex();
        builder.pos(   guiLeft,  guiTop+h+1,0).tex(u+verDu,v+verDv).endVertex();
        builder.pos(   guiLeft,  guiTop-1,  0).tex(u+verDu,   v)      .endVertex();
        //right bar
        builder.pos(   guiLeft+L,    guiTop-1,  0).tex(   u,         v)      .endVertex();
        builder.pos(   guiLeft+L,    guiTop+h+1,0).tex(   u,      v+verDv).endVertex();
        builder.pos(   guiLeft+L+1,  guiTop+h+1,0).tex(u+verDu,v+verDv).endVertex();
        builder.pos(   guiLeft+L+1,  guiTop-1,  0).tex(u+verDu,   v)      .endVertex();
        //finish drawing
        tessellator.draw();
    }



    public void makeOperationFromScreen(ClientAction action,int x,int y,int color,int length){
        SignMod.LOGGER.info("doing operation in te with parameter : action : "+action+" x : "+x+" y : "+y+" color : "+color+" length : "+length);
        switch (action){
            case SET_BG:
                storage.ifPresent(signStorage -> signStorage.setBackGround(color));
                break;
            case SET_PIXEL:
                storage.ifPresent(signStorage -> {
                    signStorage.setPixel(x,y,color,length);
                });
                break;
            case ERASE_PIXEL:
                storage.ifPresent(signStorage -> {
                    signStorage.setPixel(x,y,0,length);
                });
                break;
            case MOVE_TEXT:
                //information !! : color is not a color but indice of text
                int ind = color;
                if (ind != -1) {
                    storage.ifPresent(signStorage -> {
                        signStorage.setTextPosition(ind,x,y,getForm());
                    });
                }
                break;
            case FILL_PIXEL:
                storage.ifPresent(signStorage -> {
                    signStorage.fill(x, y, color);
                });
                break;
        }

    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == SignCapability.SIGN_STORAGE){
            return storage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT storage_tag = compound.getCompound("sign");
        getCapability(SignCapability.SIGN_STORAGE).ifPresent(s -> ((INBTSerializable<CompoundNBT>) s).deserializeNBT(storage_tag));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        getCapability(SignCapability.SIGN_STORAGE).ifPresent(storage -> {
            CompoundNBT compoundNBT = ((INBTSerializable<CompoundNBT>) storage).serializeNBT();
            tag.put("sign", compoundNBT);
        });
        return super.write(tag);
    }

    public void setText(Text t, int ind){
        storage.ifPresent(signStorage -> signStorage.setText(t,ind));
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }
}
