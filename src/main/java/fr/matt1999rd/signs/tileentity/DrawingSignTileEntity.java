package fr.matt1999rd.signs.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.ClientAction;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.matt1999rd.signs.util.Text;
import fr.matt1999rd.signs.capabilities.SignCapability;
import fr.matt1999rd.signs.capabilities.SignStorage;
import fr.matt1999rd.signs.util.Vector2i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
//import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DrawingSignTileEntity extends PanelTileEntity {

    private final LazyOptional<SignStorage> storage = LazyOptional.of(this::getStorage).cast();
    //private final ResourceLocation TEXT = new ResourceLocation(SignMod.MODID,"textures/gui/letter.png");

    private SignStorage getStorage() {
        return new SignStorage();
    }

    public DrawingSignTileEntity(BlockEntityType<?> tileEntityTypeIn,BlockPos pos,BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }
    public abstract Form getForm();

    @OnlyIn(Dist.CLIENT)
    public int getPixelColor(int i,int j){
        return storage.map(signStorage -> signStorage.getRGBPixel(i,j)).orElse(0);
    }

    public int getBGColor(){
        return storage.map(SignStorage::getBackGround).orElse(0);
    }

    public int getNumberOfText(){
        return storage.map(signStorage -> signStorage.getTexts().size()).orElse(0);
    }

    public Text getText(int n){
        return storage.map(signStorage -> {
            int lim = signStorage.getTexts().size();
            if (n>lim || n<0)throw new ArrayIndexOutOfBoundsException("Text indices is incorrect !");
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
    public void tick(Level level, BlockState state, BlockPos blockPos, PanelTileEntity t) {
        if (!state.getValue(AbstractPanelBlock.GRID)){
            //if it is a support with grid
            super.tick(level, state, blockPos, t);
        }
    }

    public void renderOnScreen(PoseStack stack,int guiLeft, int guiTop, int selTextInd) {
        Form form = getForm();
        for (int i=0;i<128;i++){
            for (int j=0;j<128;j++){
                if (form.isIn(i,j)){
                    int color = getPixelColor(i,j);
                    GuiComponent.fill(stack,guiLeft+i,guiTop+j,guiLeft+i+1,guiTop+j+1,color);
                }
            }
        }
        List<Text> texts = storage.map(SignStorage::getTexts).orElse(new ArrayList<>());
        AtomicInteger ind = new AtomicInteger(0);
        Vector2i origin = new Vector2i(guiLeft,guiTop);
        Vec2 pixelDimension = new Vec2(1.0F,1.0F);
        texts.forEach(text -> text.renderOnScreen(stack,origin,pixelDimension,ind.getAndIncrement() == selTextInd,false));
    }

    public ByteBuffer encodeSetBackGround(int color){
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putInt(color);
        return buf;
    }

    public void setBackGround(ByteBuffer buf){
        int color = buf.getInt();
        storage.ifPresent(signStorage -> signStorage.setBackGround(color));
    }

    public ByteBuffer encodeSetPixel(int x,int y,int color,int length){
        ByteBuffer buf = ByteBuffer.allocate(32);
        buf.putInt(x);
        buf.putInt(y);
        buf.putInt(color);
        buf.putInt(length);
        return buf;
    }

    public void setPixel(ByteBuffer buf){
        int x = buf.getInt();
        int y = buf.getInt();
        int color = buf.getInt();
        int length = buf.getInt();
        storage.ifPresent(signStorage -> signStorage.setPixel(x,y,color,length));
    }

    public ByteBuffer encodeFillPixel(int x,int y,int color){
        ByteBuffer buf = ByteBuffer.allocate(24);
        buf.putInt(x);
        buf.putInt(y);
        buf.putInt(color);
        return buf;
    }

    public void fillPixel(ByteBuffer buf){
        int x = buf.getInt();
        int y = buf.getInt();
        int color = buf.getInt();
        storage.ifPresent(signStorage -> signStorage.fill(x, y, color));
    }

    public ByteBuffer encodeErasePixel(int x,int y,int length){
        ByteBuffer buf = ByteBuffer.allocate(24);
        buf.putInt(x);
        buf.putInt(y);
        buf.putInt(length);
        return buf;
    }

    public void erasePixel(ByteBuffer buf){
        int x = buf.getInt();
        int y = buf.getInt();
        int length = buf.getInt();
        storage.ifPresent(signStorage -> signStorage.setPixel(x,y,0,length));
    }

    public ByteBuffer encodeSetTextPosition(int x,int y,int textIndices){
        ByteBuffer buf = ByteBuffer.allocate(24);
        buf.putInt(x);
        buf.putInt(y);
        buf.putInt(textIndices);
        return buf;
    }

    public void setTextPosition(ByteBuffer buf){
        int x = buf.getInt();
        int y = buf.getInt();
        int textIndices = buf.getInt();
        if (textIndices != -1) {
            storage.ifPresent(signStorage -> signStorage.setTextPosition(textIndices,x,y,getForm()));
        }
    }

    public void makeOperationFromScreen(ClientAction action, IntBuffer buf){
        //SignMod.LOGGER.info("doing operation in te with parameter : action : {} x : {} y : {} color : {} length : {}", action, x, y, color, length);
        action.doActionOnTileEntity(this,buf);
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
    public void load(CompoundTag compound) {
        CompoundTag storage_tag = compound.getCompound("sign");
        getCapability(SignCapability.SIGN_STORAGE).ifPresent(s -> ((INBTSerializable<CompoundTag>) s).deserializeNBT(storage_tag));
        super.load(compound);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        getCapability(SignCapability.SIGN_STORAGE).ifPresent(storage -> {
            CompoundTag compoundNBT = ((INBTSerializable<CompoundTag>) storage).serializeNBT();
            tag.put("sign", compoundNBT);
        });
        return super.save(tag);
    }

    public void setText(Text t, int ind){
        storage.ifPresent(signStorage -> signStorage.setText(t,ind));
    }

    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }
}
