package fr.mattmouss.signs.setup;

import fr.mattmouss.signs.fixedpanel.ModBlock;
import fr.mattmouss.signs.fixedpanel.PanelRegister;
import fr.mattmouss.signs.networking.Networking;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {

   public static ItemGroup itemGroup = new ItemGroup("assets/sign") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlock.SIGN_SUPPORT);
        }
    };

    public void init(){
        //si envoi de donnée
        Networking.registerMessages();
    }
}
