package fr.matt1999rd.signs.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class DirectionCapability {
    public static Capability<IDirectionStorage> DIRECTION_STORAGE = CapabilityManager.get(new CapabilityToken<>() {});

    public DirectionCapability(){

    }
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IDirectionStorage.class);
    }
}
