package fr.matt1999rd.signs.capabilities;

import net.minecraftforge.common.capabilities.*;

public class TextCapability {
    public static Capability<ITextStorage> TEXT_STORAGE = CapabilityManager.get(new CapabilityToken<>() {});

    public TextCapability(){

    }
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(ITextStorage.class);
    }

}
