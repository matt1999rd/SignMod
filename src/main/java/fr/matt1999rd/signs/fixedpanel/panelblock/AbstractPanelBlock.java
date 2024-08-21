package fr.matt1999rd.signs.fixedpanel.panelblock;

import fr.matt1999rd.signs.enums.*;
import fr.matt1999rd.signs.fixedpanel.ModBlock;
import fr.matt1999rd.signs.fixedpanel.PanelItem;
import fr.matt1999rd.signs.fixedpanel.PanelRegister;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.networking.Networking;
import fr.matt1999rd.signs.networking.PacketOpenScreen;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkDirection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


public abstract class AbstractPanelBlock extends Block implements EntityBlock {

    public AbstractPanelBlock(String name) {
        //why I cannot delete "no occlusion" ? if I do this +6min in loading of main screen why ?
        super(Properties.of(Material.STONE).noCollission().strength(2.0F).noOcclusion());
        this.setRegistryName(name+"_panel");
    }

    public static BooleanProperty GRID;
    public static EnumProperty<PSDisplayMode> MODE;
    public static EnumProperty<PSPosition> POSITION;
    public static IntegerProperty TEST;

    static {
        GRID = BooleanProperty.create("grid");
        MODE =  EnumProperty.create("mode",PSDisplayMode.class);
        POSITION = EnumProperty.create("position",PSPosition.class);
        TEST = IntegerProperty.create("test",0,3);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(GRID)){
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
            boolean isRotated = state.getValue(GridSupport.ROTATED);
            VoxelShape vs1 = Functions.getGridShape(isRotated,Direction.get(Direction.AxisDirection.POSITIVE,dir.getAxis()));
            VoxelShape vs2 = Functions.getGridShape(isRotated,Direction.get(Direction.AxisDirection.NEGATIVE,dir.getAxis()));
            return Shapes.or(vs1,vs2);
        }else {
            VoxelShape vs = Functions.getSupportShape();
            int flags = Functions.getFlagsFromState(state);
            for (ExtendDirection direction : ExtendDirection.values()) {
                if ((flags | 1) == 1) {
                    Shapes.or(vs, Functions.getGridShape(direction.isRotated(), direction.getDirection()));
                }
                flags = flags >> 1;
            }
            return vs;
        }
    }


    public static AbstractPanelBlock createPanelInstance(Form form){
        return new PanelBlock(form);
    }

    public abstract ScreenType getScreenType();
    public abstract Form getForm();

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ScreenType type = this.getScreenType();
        Form form = this.getForm();
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (!worldIn.isClientSide() &&
                (player.getDirection() == facing.getOpposite())) {
            Networking.INSTANCE.sendTo(new PacketOpenScreen(pos,form,type),((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null){
            world.setBlockAndUpdate(pos,state.setValue(BlockStateProperties.HORIZONTAL_FACING, Functions.getDirectionFromEntity(entity,pos)));
        }
    }

    public static BlockState getBlockStateFromSupport(Form form,BlockState supportState,int facing,boolean rotated){
        AbstractPanelBlock panelBlock = PanelRegister.asPanel(form);
        assert panelBlock != null;
        BlockState panelState = panelBlock.defaultBlockState();
        boolean grid = (supportState.getBlock() instanceof GridSupport);
        if (!grid){
            for (ExtendDirection direction : ExtendDirection.values()){
                BooleanProperty property = direction.getSupportProperty();
                panelState = panelState.setValue(property,supportState.getValue(property));
            }
        }
        return panelState.setValue(GridSupport.ROTATED,rotated)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.from2DDataValue(facing))
                .setValue(GRID,grid);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        //add enum property cause stupid long time running without issuing a result -> bad minecraft implementation
        builder.add(
                BlockStateProperties.HORIZONTAL_FACING,
                GridSupport.ROTATED,
                GRID
        );
        ExtendDirection.addAllBooleanProperty(builder);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void playerDestroy(Level world, Player entity, BlockPos pos, BlockState state, @Nullable BlockEntity tileEntity, ItemStack stack) {
        super.playerDestroy(world, entity, pos, Blocks.AIR.defaultBlockState(), tileEntity, stack);
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
        // here destroy will replace panel with grid by grid block and panel with support by support
        boolean grid = state.getValue(GRID);
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockState oldState;
        if (grid){
            oldState = ModBlock.GRID_SUPPORT.defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_AXIS,facing.getClockWise().getAxis())
                    .setValue(GridSupport.ROTATED,state.getValue(GridSupport.ROTATED));
        }else {
            int flags = Functions.getFlagsFromState(state);
            oldState = Functions.getNewBlockState(ModBlock.SIGN_SUPPORT.defaultBlockState(),flags);
        }
        world.setBlock(pos,oldState,11);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(PanelItem.INSTANCE);
    }
}
