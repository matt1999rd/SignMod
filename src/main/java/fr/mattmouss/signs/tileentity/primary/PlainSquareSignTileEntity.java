package fr.mattmouss.signs.tileentity.primary;

import fr.mattmouss.signs.capabilities.PSCapability;
import fr.mattmouss.signs.capabilities.PSStorage;
import fr.mattmouss.signs.enums.PSDisplayMode;
import fr.mattmouss.signs.enums.PSPosition;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.tileentity.PanelTileEntity;
import fr.mattmouss.signs.tileentity.TEType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;


public class PlainSquareSignTileEntity extends PanelTileEntity {

    private LazyOptional<PSStorage> storage = LazyOptional.of(this::getStorage).cast();

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

    @Override
    public void renderOnScreen(int guiLeft, int guiTop,int selTextInd) {

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
