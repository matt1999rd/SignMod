package fr.matt1999rd.signs.setup;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.tileentity.renderers.DirectionSignTileEntityRenderer;
import fr.matt1999rd.signs.tileentity.renderers.DrawingSignTileEntityRenderer;
import fr.matt1999rd.signs.tileentity.renderers.EditingSignTileEntityRenderer;
import fr.matt1999rd.signs.tileentity.renderers.PlainSquareSignTileEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import static fr.matt1999rd.signs.tileentity.TEType.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy implements IProxy {

    @Override
    public void init() {
        // screen : ajout de screen
        //ScreenManager.registerFactory(ModBlock.TOLLGATE_USER_CONTAINER, TGUserScreen::new);
        ClientRegistry.bindTileEntityRenderer(UPSIDE_TRIANGLE_SIGN,dispatcher -> new EditingSignTileEntityRenderer<>(dispatcher,Form.UPSIDE_TRIANGLE));
        ClientRegistry.bindTileEntityRenderer(TRIANGLE_SIGN       ,dispatcher -> new DrawingSignTileEntityRenderer<>(dispatcher,Form.TRIANGLE));
        ClientRegistry.bindTileEntityRenderer(OCTOGONE_SIGN       ,dispatcher -> new EditingSignTileEntityRenderer<>(dispatcher,Form.OCTAGON));
        ClientRegistry.bindTileEntityRenderer(CIRCLE_SIGN         ,dispatcher -> new DrawingSignTileEntityRenderer<>(dispatcher,Form.CIRCLE));
        ClientRegistry.bindTileEntityRenderer(SQUARE_SIGN         ,dispatcher -> new DrawingSignTileEntityRenderer<>(dispatcher,Form.SQUARE));
        ClientRegistry.bindTileEntityRenderer(RECTANGLE_SIGN      ,dispatcher -> new DirectionSignTileEntityRenderer<>(dispatcher,Form.RECTANGLE));
        ClientRegistry.bindTileEntityRenderer(ARROW_SIGN          ,dispatcher -> new DirectionSignTileEntityRenderer<>(dispatcher,Form.ARROW));
        ClientRegistry.bindTileEntityRenderer(PLAIN_SQUARE_SIGN   , PlainSquareSignTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(DIAMOND_SIGN        ,dispatcher -> new DrawingSignTileEntityRenderer<>(dispatcher,Form.DIAMOND));
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
