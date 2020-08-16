package fr.mattmouss.signs.tileentity;

import fr.mattmouss.signs.capabilities.TextCapability;
import fr.mattmouss.signs.capabilities.TextStorage;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.util.Text;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class EditingSignTileEntity extends PanelTileEntity {

    private LazyOptional<TextStorage> storage = LazyOptional.of(this::getStorage);

    public EditingSignTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        BlockState state = getBlockState();
        if (!state.get(AbstractPanelBlock.GRID)){
            //if it is a support with grid
            super.tick();
        }
    }

    protected abstract Form getForm();

    private TextStorage getStorage() {
        return new TextStorage(1,Text.getDefaultText());
    }

    public Text getText(){
        return storage.map(textStorage -> textStorage.getText(0)).orElse(Text.getDefaultText());
    }

    @Override
    public void renderOnScreen(int guiLeft, int guiTop, int selTextInd) {
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == TextCapability.TEXT_STORAGE){
            return storage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT storage_tag = compound.getCompound("text");
        getCapability(TextCapability.TEXT_STORAGE).ifPresent(s -> ((INBTSerializable<CompoundNBT>) s).deserializeNBT(storage_tag));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        getCapability(TextCapability.TEXT_STORAGE).ifPresent(storage -> {
            CompoundNBT compoundNBT = ((INBTSerializable<CompoundNBT>) storage).serializeNBT();
            tag.put("text", compoundNBT);
        });
        return super.write(tag);
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }
}
