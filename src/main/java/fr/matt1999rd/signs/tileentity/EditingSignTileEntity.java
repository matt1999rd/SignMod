package fr.matt1999rd.signs.tileentity;


import com.mojang.blaze3d.vertex.PoseStack;
//import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.matt1999rd.signs.util.Text;
import fr.matt1999rd.signs.capabilities.TextCapability;
import fr.matt1999rd.signs.capabilities.TextStorage;
import fr.matt1999rd.signs.util.Vector2i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
//import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class EditingSignTileEntity extends PanelTileEntity {

    private final LazyOptional<TextStorage> storage = LazyOptional.of(this::getStorage).cast();
    //private final ResourceLocation TEXT = new ResourceLocation(SignMod.MODID,"textures/gui/letter.png");

    public EditingSignTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos,BlockState state) {
        super(tileEntityTypeIn,pos,state);
    }

    @Override
    public void tick(Level level, BlockState state, BlockPos blockPos, PanelTileEntity t) {
        if (!state.getValue(AbstractPanelBlock.GRID)){
            //if it is a support with grid
            super.tick(level, state, blockPos, t);
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

    public void renderOnScreen(PoseStack stack,int guiLeft, int guiTop) {
        Text t = getText();
        Vector2i origin = new Vector2i(guiLeft,guiTop);
        Vec2 scale = new Vec2(1.0F,1.0F);
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
    public void load(CompoundTag compound) {
        CompoundTag storage_tag = compound.getCompound("text");
        getCapability(TextCapability.TEXT_STORAGE).ifPresent(s -> ((INBTSerializable<CompoundTag>) s).deserializeNBT(storage_tag));
        super.load(compound);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        getCapability(TextCapability.TEXT_STORAGE).ifPresent(storage -> {
            CompoundTag compoundNBT = ((INBTSerializable<CompoundTag>) storage).serializeNBT();
            tag.put("text", compoundNBT);
        });
        return super.save(tag);
    }

    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }
}
