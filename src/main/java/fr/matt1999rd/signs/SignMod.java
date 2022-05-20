package fr.matt1999rd.signs;

import fr.matt1999rd.signs.fixedpanel.ModBlock;
import fr.matt1999rd.signs.fixedpanel.PanelItem;
import fr.matt1999rd.signs.fixedpanel.PanelRegister;
import fr.matt1999rd.signs.fixedpanel.support.*;
import fr.matt1999rd.signs.tileentity.primary.*;
import fr.matt1999rd.signs.setup.ClientProxy;
import fr.matt1999rd.signs.setup.IProxy;
import fr.matt1999rd.signs.setup.ModSetup;
import fr.matt1999rd.signs.setup.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public SignMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
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
        public static void onTERegistry(final RegistryEvent.Register<TileEntityType<?>> register){
            register.getRegistry().register(TileEntityType.Builder
                    .of(SignSupportTileEntity::new, ModBlock.SIGN_SUPPORT)
                    .build(null).setRegistryName("sign_support"));
            register.getRegistry().register(TileEntityType.Builder
                    .of(SquareSignTileEntity::new, ModBlock.SQUARE_PANEL)
                    .build(null).setRegistryName("square_panel"));
            register.getRegistry().register(TileEntityType.Builder
                    .of(TriangleSignTileEntity::new,ModBlock.TRIANGLE_PANEL)
                    .build(null).setRegistryName("triangle_panel"));
            register.getRegistry().register(TileEntityType.Builder
                    .of(CircleSignTileEntity::new,ModBlock.CIRCLE_PANEL)
                    .build(null).setRegistryName("circle_panel"));
            register.getRegistry().register(TileEntityType.Builder
                    .of(UpsideTriangleSignTileEntity::new,ModBlock.LET_WAY_PANEL)
                    .build(null).setRegistryName("let_way_panel"));
            register.getRegistry().register(TileEntityType.Builder
                    .of(OctagonSignTileEntity::new,ModBlock.STOP_PANEL)
                    .build(null).setRegistryName("stop_panel"));
            register.getRegistry().register(TileEntityType.Builder
                    .of(ArrowSignTileEntity::new,ModBlock.DIRECTION_PANEL)
                    .build(null).setRegistryName("direction_panel"));
            register.getRegistry().register(TileEntityType.Builder
                    .of(PlainSquareSignTileEntity::new,ModBlock.HUGE_DIRECTION_PANEL)
                    .build(null).setRegistryName("huge_direction_panel"));
            register.getRegistry().register(TileEntityType.Builder
                    .of(DiamondSignTileEntity::new,ModBlock.DIAMOND_PANEL)
                    .build(null).setRegistryName("diamond_panel"));
            register.getRegistry().register(TileEntityType.Builder
                    .of(RectangleSignTileEntity::new,ModBlock.RECTANGLE_PANEL)
                    .build(null).setRegistryName("rectangle_panel"));


        }
    }
}
