package fr.mattmouss.signs.capabilities;

import fr.mattmouss.signs.enums.PSDisplayMode;
import fr.mattmouss.signs.enums.PSPosition;
import fr.mattmouss.signs.util.Text;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.List;

public class PSCapability {
    @CapabilityInject(IPSStorage.class)
    public static Capability<IPSStorage> PS_STORAGE = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IPSStorage.class, new Capability.IStorage<IPSStorage>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IPSStorage> capability, IPSStorage instance, Direction side) {
                CompoundNBT tag = new CompoundNBT();
                ListNBT textsNBT = new ListNBT();
                List<Text> txts = instance.getTexts();
                txts.forEach(text -> {
                    CompoundNBT txtNBT = text.serializeNBT();
                    textsNBT.add(txtNBT);
                });
                tag.put("texts",textsNBT);
                tag.putInt("bg_color",instance.getBackgroundColor());
                tag.putByte("display_mode",instance.getDisplayMode().getMeta());
                tag.putByte("ps_position",instance.getPosition().getMeta());
                return tag;
            }

            @Override
            public void readNBT(Capability<IPSStorage> capability, IPSStorage instance, Direction side, INBT nbt) {
                CompoundNBT tag = (CompoundNBT)nbt;
                ListNBT textsNBT = (ListNBT)(tag.get("texts"));
                for (INBT inbt : textsNBT){
                    CompoundNBT textNBT = (CompoundNBT)inbt;
                    Text t= Text.getTextFromNBT(textNBT);
                    instance.addText(t,false);
                }
                int bg_color = tag.getInt("bg_color");
                instance.setBackgroundColor(bg_color);
                byte mode = tag.getByte("display_mode");
                byte position = tag.getByte("ps_position");
                instance.setInternVariable(PSPosition.byIndex(position), PSDisplayMode.byIndex(mode));
            }
        },PSStorage::new);
    }

}
