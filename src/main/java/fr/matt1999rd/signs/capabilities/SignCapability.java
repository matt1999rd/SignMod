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

public class SignCapability {
    @CapabilityInject(ISignStorage.class)
    public static Capability<ISignStorage> SIGN_STORAGE = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ISignStorage.class, new Capability.IStorage<ISignStorage>() {
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
                List<Text> txts = instance.getTexts();
                txts.forEach(text -> {
                    CompoundNBT txtNBT = text.serializeNBT();
                    textsNBT.add(txtNBT);
                });
                tag.put("texts",textsNBT);
                return tag;
            }

            @Override
            public void readNBT(Capability<ISignStorage> capability, ISignStorage instance, Direction side, INBT nbt) {
                CompoundNBT tag = (CompoundNBT)nbt;
                ListNBT pixelsNBT = (ListNBT)(tag.get("pixels"));
                int[] pixels = new int[128*128];
                int index = 0;
                assert pixelsNBT != null;
                for (INBT inbt : pixelsNBT){
                    CompoundNBT pxlNBT = (CompoundNBT)inbt;
                    int pxl_color = pxlNBT.getInt("color");
                    pixels[index] = pxl_color;
                    index++;
                }
                instance.setAllPixel(pixels);
                ListNBT textsNBT = (ListNBT)(tag.get("texts"));
                assert textsNBT != null;
                for (INBT inbt : textsNBT){
                    CompoundNBT textNBT = (CompoundNBT)inbt;
                    Text t= Text.getTextFromNBT(textNBT);
                    instance.addText(t);
                }
            }
        },SignStorage::new);
    }

}
