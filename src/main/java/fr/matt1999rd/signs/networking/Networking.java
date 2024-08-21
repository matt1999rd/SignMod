package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.SignMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

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

        INSTANCE.registerMessage(nextID(),
                PacketOpenScreen.class,
                PacketOpenScreen::toBytes,
                PacketOpenScreen::new,
                PacketOpenScreen::handle);

        INSTANCE.registerMessage(nextID(),
                PacketDrawingAction.class,
                PacketDrawingAction::toBytes,
                PacketDrawingAction::new,
                PacketDrawingAction::handle);

        INSTANCE.registerMessage(nextID(),
                PacketAddOrEditText.class,
                PacketAddOrEditText::toBytes,
                PacketAddOrEditText::new,
                PacketAddOrEditText::handle);

        INSTANCE.registerMessage(nextID(),
                PacketDelText.class,
                PacketDelText::toBytes,
                PacketDelText::new,
                PacketDelText::handle);


        INSTANCE.registerMessage(nextID(),
                PacketChangeColor.class,
                PacketChangeColor::toBytes,
                PacketChangeColor::new,
                PacketChangeColor::handle);

        INSTANCE.registerMessage(nextID(),
                PacketSetBoolean.class,
                PacketSetBoolean::toBytes,
                PacketSetBoolean::new,
                PacketSetBoolean::handle);

        INSTANCE.registerMessage(nextID(),
                PacketFlipText.class,
                PacketFlipText::toBytes,
                PacketFlipText::new,
                PacketFlipText::handle);

        INSTANCE.registerMessage(nextID(),
                PacketCenterText.class,
                PacketCenterText::toBytes,
                PacketCenterText::new,
                PacketCenterText::handle);

        INSTANCE.registerMessage(nextID(),
                PacketPlacePSPanel.class,
                PacketPlacePSPanel::toBytes,
                PacketPlacePSPanel::new,
                PacketPlacePSPanel::handle);

        INSTANCE.registerMessage(nextID(),
                PacketPSScreenOperation.class,
                PacketPSScreenOperation::toBytes,
                PacketPSScreenOperation::new,
                PacketPSScreenOperation::handle);


        /*
        INSTANCE.registerMessage(nextID(),
                PacketOpenScreen.class,
                PacketOpenScreen::toBytes,
                PacketOpenScreen::new,
                PacketOpenScreen::handle);
        */
    }
}
