package fr.matt1999rd.signs.capabilities;

import net.minecraftforge.common.capabilities.*;

public class SignCapability {
    public static Capability<ISignStorage> SIGN_STORAGE = CapabilityManager.get(new CapabilityToken<>() {});

    public SignCapability(){

    }
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(ISignStorage.class);
    }

}
