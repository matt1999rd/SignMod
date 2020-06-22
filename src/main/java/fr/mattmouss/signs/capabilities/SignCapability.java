package fr.mattmouss.signs.capabilities;

import fr.mattmouss.signs.util.Text;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class SignCapability {
    @CapabilityInject(ISignStorage.class)
    public static Capability<ISignStorage> SIGN_STORAGE = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ISignStorage.class, new Capability.IStorage<ISignStorage>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<ISignStorage> capability, ISignStorage instance, Direction side) {
                CompoundNBT tag = new CompoundNBT();
                int[] pixels = instance.getAllPixel();
                ListNBT pixelsNBT = new ListNBT();
                for (int i : pixels){
                    CompoundNBT pxlNBT = new CompoundNBT();
                    pxlNBT.putInt("color",i);
                    pixelsNBT.add(pxlNBT);
                }
                tag.put("pixels",pixelsNBT);
                ListNBT textsNBT = new ListNBT();
                Text[] txts = instance.getTexts();
                for (Text t : txts){
                    CompoundNBT txtNBT = t.serializeNBT();
                    textsNBT.add(txtNBT);
                }
                tag.put("texts",textsNBT);
                return tag;
            }

            @Override
            public void readNBT(Capability<ISignStorage> capability, ISignStorage instance, Direction side, INBT nbt) {
                CompoundNBT tag = (CompoundNBT)nbt;
                ListNBT pixelsNBT = (ListNBT)(tag.get("pixels"));
                int[] pixels = new int[16*16];
                int index = 0;
                for (INBT inbt : pixelsNBT){
                    CompoundNBT pxlNBT = (CompoundNBT)inbt;
                    int pxl_color = pxlNBT.getInt("color");
                    pixels[index] = pxl_color;
                    index++;
                }
                instance.setAllPixel(pixels);
                ListNBT textsNBT = (ListNBT)(tag.get("texts"));
                for (INBT inbt : textsNBT){
                    CompoundNBT textNBT = (CompoundNBT)inbt;
                    Text t = new Text(0,0," ");
                    t.deserializeNBT(textNBT);
                    instance.addText(t);
                }
            }
        },SignStorage::new);
    }

}
