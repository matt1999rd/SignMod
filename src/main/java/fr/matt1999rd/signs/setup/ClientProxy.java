package fr.matt1999rd.signs.setup;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.tileentity.renderers.DirectionSignTileEntityRenderer;
import fr.matt1999rd.signs.tileentity.renderers.DrawingSignTileEntityRenderer;
import fr.matt1999rd.signs.tileentity.renderers.EditingSignTileEntityRenderer;
import fr.matt1999rd.signs.tileentity.renderers.PlainSquareSignTileEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.entity.player.Player;
import static fr.matt1999rd.signs.tileentity.TEType.*;
import net.minecraft.world.level.Level;
//import net.minecraftforge.fmllegacy.client.registry.ClientRegistry;

public class ClientProxy implements IProxy {

    @Override
    public void init() {
        // screen : ajout de screen
        //ScreenManager.registerFactory(ModBlock.TOLLGATE_USER_CONTAINER, TGUserScreen::new);
        BlockEntityRenderers.register(UPSIDE_TRIANGLE_SIGN,context -> new EditingSignTileEntityRenderer<>(context,Form.UPSIDE_TRIANGLE));
        BlockEntityRenderers.register(TRIANGLE_SIGN,       context -> new DrawingSignTileEntityRenderer<>(context,Form.TRIANGLE));
        BlockEntityRenderers.register(OCTAGON_SIGN,        context -> new EditingSignTileEntityRenderer<>(context,Form.OCTAGON));
        BlockEntityRenderers.register(CIRCLE_SIGN,         context -> new DrawingSignTileEntityRenderer<>(context,Form.CIRCLE));
        BlockEntityRenderers.register(SQUARE_SIGN,         context -> new DrawingSignTileEntityRenderer<>(context,Form.SQUARE));
        BlockEntityRenderers.register(RECTANGLE_SIGN,      context -> new DirectionSignTileEntityRenderer<>(Form.RECTANGLE));
        BlockEntityRenderers.register(ARROW_SIGN,          context -> new DirectionSignTileEntityRenderer<>(Form.ARROW));
        BlockEntityRenderers.register(PLAIN_SQUARE_SIGN,PlainSquareSignTileEntityRenderer::new);
        BlockEntityRenderers.register(DIAMOND_SIGN,        context -> new DrawingSignTileEntityRenderer<>(context,Form.DIAMOND));
    }

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
