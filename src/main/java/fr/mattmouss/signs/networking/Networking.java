package fr.mattmouss.signs.networking;

import fr.mattmouss.signs.SignMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(SignMod.MODID, "assets/sign"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(),
                PacketChoicePanel.class,
                PacketChoicePanel::toBytes,
                PacketChoicePanel::new,
                PacketChoicePanel::handle);

        INSTANCE.registerMessage(nextID(),
                PacketPlacePanel.class,
                PacketPlacePanel::toBytes,
                PacketPlacePanel::new,
                PacketPlacePanel::handle);

        /*
        INSTANCE.registerMessage(nextID(),
                PacketSpawn.class,
                PacketSpawn::toBytes,
                PacketSpawn::new,
                PacketSpawn::handle);
        */
    }
}