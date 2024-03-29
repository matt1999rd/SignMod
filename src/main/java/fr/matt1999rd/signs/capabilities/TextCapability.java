package fr.matt1999rd.signs.capabilities;

import fr.matt1999rd.signs.util.Text;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.List;

public class TextCapability {
    @CapabilityInject(ITextStorage.class)
    public static Capability<ITextStorage> TEXT_STORAGE = null;
    public static void register()
    {
        CapabilityManager.INSTANCE.register(ITextStorage.class, new Capability.IStorage<ITextStorage>() {
            @Override
            public INBT writeNBT(Capability<ITextStorage> capability, ITextStorage instance, Direction side) {
                List<Text> texts = instance.getTexts();
                CompoundNBT nbt = new CompoundNBT();
                ListNBT listNBT = new ListNBT();
                texts.forEach(text -> {
                    CompoundNBT nbt2 = text.serializeNBT();
                    listNBT.add(nbt2);
                });
                nbt.put("list",listNBT);
                nbt.putInt("maxLength",instance.getMaxLength());
                return nbt;
            }

            @Override
            public void readNBT(Capability<ITextStorage> capability, ITextStorage instance, Direction side, INBT nbt) {
                CompoundNBT nbt1 = (CompoundNBT)nbt;
                ListNBT listNBT = (ListNBT) nbt1.get("list");
                assert listNBT != null;
                listNBT.forEach(iNbt -> {
                    CompoundNBT nbt2 = (CompoundNBT)iNbt;
                    instance.addText(Text.getTextFromNBT(nbt2),false);
                });
                int maxLength = nbt1.getInt("maxLength");
                instance.setMaxLength(maxLength);
            }
        },TextStorage::new);
    }

}
