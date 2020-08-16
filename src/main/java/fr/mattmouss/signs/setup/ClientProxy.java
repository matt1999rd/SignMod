package fr.mattmouss.signs.setup;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.tileentity.primary.*;
import fr.mattmouss.signs.tileentity.renderers.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CRecipeInfoPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.zip.CRC32;

public class ClientProxy implements IProxy {

    @Override
    public void init() {
        // screen : ajout de screen
        //ScreenManager.registerFactory(ModBlock.TOLLGATE_USER_CONTAINER, TGUserScreen::new);
        ClientRegistry.bindTileEntitySpecialRenderer(UpsideTriangleSignTileEntity.class, new EditingSignTileEntityRenderer<>(Form.UPSIDE_TRIANGLE));
        ClientRegistry.bindTileEntitySpecialRenderer(TriangleSignTileEntity.class      , new DrawingSignTileEntityRenderer<>(Form.TRIANGLE));
        ClientRegistry.bindTileEntitySpecialRenderer(OctogoneSignTileEntity.class      , new EditingSignTileEntityRenderer<>(Form.OCTOGONE));
        ClientRegistry.bindTileEntitySpecialRenderer(CircleSignTileEntity.class        , new DrawingSignTileEntityRenderer<>(Form.CIRCLE));
        ClientRegistry.bindTileEntitySpecialRenderer(SquareSignTileEntity.class        , new DrawingSignTileEntityRenderer<>(Form.SQUARE));
        ClientRegistry.bindTileEntitySpecialRenderer(RectangleSignTileEntity.class     , new RectangleSignTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ArrowSignTileEntity.class         , new ArrowSignTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(PlainSquareSignTileEntity.class   , new PlainSquareSignTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DiamondSignTileEntity.class       , new DrawingSignTileEntityRenderer<>(Form.DIAMOND));
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
