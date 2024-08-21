package fr.matt1999rd.signs.setup;

import fr.matt1999rd.signs.fixedpanel.ModBlock;
import fr.matt1999rd.signs.networking.Networking;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModSetup {

   public static CreativeModeTab itemGroup = new CreativeModeTab("assets/sign") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlock.SIGN_SUPPORT);
        }
    };

    public void init(){
        //if data send
        Networking.registerMessages();
    }
}
