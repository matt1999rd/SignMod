package fr.mattmouss.signs.setup;

import fr.mattmouss.signs.tileentity.primary.SquareSignTileEntity;
import fr.mattmouss.signs.tileentity.renderers.SquareSignTileEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy implements IProxy {

    @Override
    public void init() {
        // screen : ajout de screen
        //ScreenManager.registerFactory(ModBlock.TOLLGATE_USER_CONTAINER, TGUserScreen::new);
        ClientRegistry.bindTileEntitySpecialRenderer(SquareSignTileEntity.class,new SquareSignTileEntityRenderer());
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
