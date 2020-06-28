package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.ExtendDirection;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.ScreenType;
import fr.mattmouss.signs.fixedpanel.PanelRegister;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupport;
import fr.mattmouss.signs.networking.Networking;
import fr.mattmouss.signs.networking.PacketChoicePanel;
import fr.mattmouss.signs.networking.PacketOpenScreen;
import fr.mattmouss.signs.util.Functions;
import javafx.scene.layout.Pane;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static fr.mattmouss.signs.util.Functions.*;
import static fr.mattmouss.signs.util.Functions.SOUTH_WEST;

public abstract class AbstractPanelBlock extends Block {
    public AbstractPanelBlock(String name) {
        super(Properties.create(Material.ROCK).doesNotBlockMovement().hardnessAndResistance(2.0F).lightValue(0));
        this.setRegistryName(name+"_panel");
    }

    public static BooleanProperty GRID;

    static {
        GRID = BooleanProperty.create("grid");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    public static AbstractPanelBlock createPanelInstance(Form form){
        switch (form){
            case UPSIDE_TRIANGLE:
                return new LetWayPanelBlock();
            case TRIANGLE:
                return new TrianglePanelBlock();
            case OCTOGONE:
                return new OctogonePanelBlock();
            case CIRCLE:
                return new CirclePanelBlock();
            case SQUARE:
                return new SquarePanelBlock();
            case RECTANGLE:
                return new RectanglePanelBlock();
            case ARROW:
                return new ArrowPanelBlock();
            case PLAIN_SQUARE:
                return new PlainSquarePanelBlock();
            case DIAMOND:
                return new DiamondPanelBlock();
            default:
                return null;
        }
    }

    public abstract ScreenType getScreenType();
    public abstract Form getForm();

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ScreenType type = this.getScreenType();
        Form form = this.getForm();
        if (!worldIn.isRemote()) {
            Networking.INSTANCE.sendTo(new PacketOpenScreen(pos,form,type),((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null){
            world.setBlockState(pos,state.with(BlockStateProperties.HORIZONTAL_FACING, Functions.getDirectionFromEntity(entity,pos)));
        }
    }

    public static BlockState getBlockStateFromSupport(int form,BlockState supportState,int facing,boolean rotated){
        Form f = Form.byIndex(form);
        AbstractPanelBlock panelBlock = PanelRegister.asPanel(f);
        BlockState panelState = panelBlock.getDefaultState();
        boolean grid = (supportState.getBlock() instanceof GridSupport);
        return panelState.with(GridSupport.ROTATED,rotated).with(BlockStateProperties.HORIZONTAL_FACING, Direction.byHorizontalIndex(facing)).with(GRID,grid);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(
                BlockStateProperties.HORIZONTAL_FACING,
                GridSupport.ROTATED,
                GRID,
                NORTH_EAST,
                NORTH_WEST,
                SOUTH_EAST,
                SOUTH_WEST,
                BlockStateProperties.NORTH,
                BlockStateProperties.SOUTH,
                BlockStateProperties.WEST,
                BlockStateProperties.EAST
        );
    }

    @Override
    @ParametersAreNonnullByDefault
    public void harvestBlock(World world, PlayerEntity entity, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack) {
        super.harvestBlock(world, entity, pos, Blocks.AIR.getDefaultState(), tileEntity, stack);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        boolean grid = state.get(GRID);
        if (grid){
            Functions.deleteOtherGrid(pos,world,player,state);
        }else {
            Functions.deleteConnectingGrid(pos,world,player,state);
            BlockPos pos_offset = pos.up();
            //we delete all block that this support block was handling
            while (Functions.isSignSupport(world.getBlockState(pos_offset))){
                BlockState state1 = world.getBlockState(pos_offset);
                Functions.deleteBlock(pos.up(),world,player);
                Functions.deleteConnectingGrid(pos_offset,world,player,state1);
                pos_offset = pos_offset.up();
            }
        }
        Functions.deleteBlock(pos,world,player);
        super.onBlockHarvested(world, pos, state, player);
    }


}
