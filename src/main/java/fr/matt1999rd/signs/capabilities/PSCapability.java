package fr.matt1999rd.signs.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;


public class PSCapability {
    public static Capability<IPSStorage> PS_STORAGE = CapabilityManager.get(new CapabilityToken<>() {});

    public PSCapability(){

    }
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IPSStorage.class);
    }

}
