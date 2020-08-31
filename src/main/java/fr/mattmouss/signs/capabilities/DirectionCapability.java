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

public class DirectionCapability {
    @CapabilityInject(IDirectionStorage.class)
    public static Capability<IDirectionStorage> DIRECTION_STORAGE = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IDirectionStorage.class, new Capability.IStorage<IDirectionStorage>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IDirectionStorage> capability, IDirectionStorage instance, Direction side) {
                ListNBT nbt = new ListNBT();
                Text[] allText = instance.getAllTexts();
                boolean[] panelPlacement = instance.getPanelPlacement();
                for (int i=0;i<5;i++){
                    CompoundNBT nbt3 = new CompoundNBT();
                    CompoundNBT nbt1 = allText[2*i].serializeNBT();
                    CompoundNBT nbt2 = allText[2*i+1].serializeNBT();
                    nbt3.put("beg",nbt1);
                    nbt3.put("end",nbt2);
                    nbt.add(nbt3);
                }
                for (int i=0;i<8;i++){
                    CompoundNBT nbt4 = new CompoundNBT();
                    nbt4.putBoolean("bool",panelPlacement[i]);
                    nbt.add(nbt4);
                }
                return nbt;
            }

            @Override
            public void readNBT(Capability<IDirectionStorage> capability, IDirectionStorage instance, Direction side, INBT nbt) {
                Text[] allText = new Text[10];
                boolean[] panelPlacement = new boolean[8];
                ListNBT listNBT = (ListNBT)nbt;
                for (int i=0;i<5;i++){
                    CompoundNBT nbt3 = (CompoundNBT) listNBT.get(i);
                    CompoundNBT nbt1 = nbt3.getCompound("beg");
                    CompoundNBT nbt2 = nbt3.getCompound("end");
                    allText[2*i]=Text.getTextFromNBT(nbt1);
                    allText[2*i+1]=Text.getTextFromNBT(nbt2);
                }
                for (int i=0;i<8;i++){
                    CompoundNBT nbt3 = (CompoundNBT) listNBT.get(i);
                    panelPlacement[i]=nbt3.getBoolean("bool");
                }
                instance.setPanelPlacement(panelPlacement);
                instance.setAllTexts(allText);
            }
        },DirectionStorage::new);

    }
}
