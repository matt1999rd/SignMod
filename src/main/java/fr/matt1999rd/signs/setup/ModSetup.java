package fr.matt1999rd.signs.setup;

import fr.matt1999rd.signs.fixedpanel.ModBlock;
import fr.matt1999rd.signs.networking.Networking;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {

   public static ItemGroup itemGroup = new ItemGroup("assets/sign") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlock.SIGN_SUPPORT);
        }
    };

    public void init(){
        //si envoi de donnée
        Networking.registerMessages();
    }
}
