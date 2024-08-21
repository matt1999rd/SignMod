package fr.matt1999rd.signs;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import fr.matt1999rd.signs.fixedpanel.ModBlock;
import fr.matt1999rd.signs.fixedpanel.PanelItem;
import fr.matt1999rd.signs.fixedpanel.PanelRegister;
import fr.matt1999rd.signs.fixedpanel.support.*;
import fr.matt1999rd.signs.tileentity.primary.*;
import fr.matt1999rd.signs.setup.ClientProxy;
import fr.matt1999rd.signs.setup.IProxy;
import fr.matt1999rd.signs.setup.ModSetup;
import fr.matt1999rd.signs.setup.ServerProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SignMod.MODID)
public class SignMod
{
    //5058 line of code for now before doing special panel
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static IProxy proxy = DistExecutor.runForDist(()->()->new ClientProxy(),()->()->new ServerProxy());

    public static ModSetup setup = new ModSetup();

    public static final String MODID = "sign";

    private static final ResourceLocation SHADER_PANEL = new ResourceLocation(MODID,"shader_panel");

    private static ShaderInstance shader_panel;

    public static void onRenderWorldLastEvent(RenderWorldLastEvent event){
        Minecraft mc = Minecraft.getInstance();
        mc.gameRenderer.loadEffect(SHADER_PANEL);
    }

    public SignMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerShaders);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerShaders(RegisterShadersEvent event) {
        boolean noError = true;
        try {
            event.registerShader(new ShaderInstance(event.getResourceManager(), SHADER_PANEL,
                    DefaultVertexFormat.POSITION_COLOR_LIGHTMAP),
                    shaderInstance -> {
                        shader_panel = shaderInstance;
            });

        } catch (IOException e){
            System.out.println("ERROR in loading of shader. Error type : "+e.getClass().getName());
            System.out.println("Message : "+e.getMessage());
            noError = false;
        }

        if (noError){
            System.out.println("-----------------Shader registered !------------------");
        }
    }

    public static ShaderInstance getPanelShader(){
        return shader_panel;
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        proxy.init();
        setup.init();
    }



    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            blockRegistryEvent.getRegistry().register(new SignSupport());
            blockRegistryEvent.getRegistry().register(new GridSupport());
            PanelRegister.registerBlocks(blockRegistryEvent);
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegister){
            Item.Properties properties = new Item.Properties().tab(ModSetup.itemGroup);
            itemRegister.getRegistry().register(new SignSupportItem(properties));
            itemRegister.getRegistry().register(new GridSupportItem(properties));
            itemRegister.getRegistry().register(PanelItem.INSTANCE);
        }

        @SubscribeEvent
        public static void onTERegistry(final RegistryEvent.Register<BlockEntityType<?>> register){
            register.getRegistry().register(BlockEntityType.Builder
                    .of(SignSupportTileEntity::new, ModBlock.SIGN_SUPPORT)
                    .build(null).setRegistryName("sign_support"));
            register.getRegistry().register(BlockEntityType.Builder
                    .of(SquareSignTileEntity::new, ModBlock.SQUARE_PANEL)
                    .build(null).setRegistryName("square_panel"));
            register.getRegistry().register(BlockEntityType.Builder
                    .of(TriangleSignTileEntity::new,ModBlock.TRIANGLE_PANEL)
                    .build(null).setRegistryName("triangle_panel"));
            register.getRegistry().register(BlockEntityType.Builder
                    .of(CircleSignTileEntity::new,ModBlock.CIRCLE_PANEL)
                    .build(null).setRegistryName("circle_panel"));
            register.getRegistry().register(BlockEntityType.Builder
                    .of(UpsideTriangleSignTileEntity::new,ModBlock.LET_WAY_PANEL)
                    .build(null).setRegistryName("let_way_panel"));
            register.getRegistry().register(BlockEntityType.Builder
                    .of(OctagonSignTileEntity::new,ModBlock.STOP_PANEL)
                    .build(null).setRegistryName("stop_panel"));
            register.getRegistry().register(BlockEntityType.Builder
                    .of(ArrowSignTileEntity::new,ModBlock.DIRECTION_PANEL)
                    .build(null).setRegistryName("direction_panel"));
            register.getRegistry().register(BlockEntityType.Builder
                    .of(PlainSquareSignTileEntity::new,ModBlock.HUGE_DIRECTION_PANEL)
                    .build(null).setRegistryName("huge_direction_panel"));
            register.getRegistry().register(BlockEntityType.Builder
                    .of(DiamondSignTileEntity::new,ModBlock.DIAMOND_PANEL)
                    .build(null).setRegistryName("diamond_panel"));
            register.getRegistry().register(BlockEntityType.Builder
                    .of(RectangleSignTileEntity::new,ModBlock.RECTANGLE_PANEL)
                    .build(null).setRegistryName("rectangle_panel"));


        }
    }
}
