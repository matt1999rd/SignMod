package fr.matt1999rd.signs.tileentity;


import com.mojang.blaze3d.matrix.MatrixStack;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.matt1999rd.signs.util.Text;
import fr.matt1999rd.signs.capabilities.TextCapability;
import fr.matt1999rd.signs.capabilities.TextStorage;
import fr.matt1999rd.signs.util.Vector2i;
import net.minecraft.block.BlockState;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class EditingSignTileEntity extends PanelTileEntity {

    private final LazyOptional<TextStorage> storage = LazyOptional.of(this::getStorage).cast();
    private final ResourceLocation TEXT = new ResourceLocation(SignMod.MODID,"textures/gui/letter.png");

    public EditingSignTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        BlockState state = getBlockState();
        if (!state.getValue(AbstractPanelBlock.GRID)){
            //if it is a support with grid
            super.tick();
        }
    }

    protected abstract Form getForm();

    private TextStorage getStorage() {
        return new TextStorage(1, Text.getDefaultText());
    }

    public Text getText(){
        return storage.map(textStorage -> textStorage.getText(0)).orElse(Text.getDefaultText());
    }

    public void setText(Text newText){
        storage.ifPresent(textStorage -> textStorage.setText(newText,0));
    }

    public void renderOnScreen(MatrixStack stack,int guiLeft, int guiTop) {
        Text t = getText();
        Vector2i origin = new Vector2i(guiLeft,guiTop);
        Vector2f scale = new Vector2f(1.0F,1.0F);
        t.renderOnScreen(stack,origin,scale,true,false);
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
    public void load(BlockState state,CompoundNBT compound) {
        CompoundNBT storage_tag = compound.getCompound("text");
        getCapability(TextCapability.TEXT_STORAGE).ifPresent(s -> ((INBTSerializable<CompoundNBT>) s).deserializeNBT(storage_tag));
        super.load(state,compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        getCapability(TextCapability.TEXT_STORAGE).ifPresent(storage -> {
            CompoundNBT compoundNBT = ((INBTSerializable<CompoundNBT>) storage).serializeNBT();
            tag.put("text", compoundNBT);
        });
        return super.save(tag);
    }

    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }
}
