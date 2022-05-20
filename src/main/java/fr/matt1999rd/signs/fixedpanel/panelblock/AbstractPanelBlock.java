package fr.matt1999rd.signs.fixedpanel.panelblock;

import fr.matt1999rd.signs.enums.*;
import fr.matt1999rd.signs.fixedpanel.ModBlock;
import fr.matt1999rd.signs.fixedpanel.PanelItem;
import fr.matt1999rd.signs.fixedpanel.PanelRegister;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.fixedpanel.support.GridSupportItem;
import fr.matt1999rd.signs.fixedpanel.support.SignSupportItem;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.networking.Networking;
import fr.matt1999rd.signs.networking.PacketOpenScreen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class AbstractPanelBlock extends Block {

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
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.getValue(GRID)){
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
            boolean isRotated = state.getValue(GridSupport.ROTATED);
            VoxelShape vs1 = Functions.getGridShape(isRotated,Direction.get(Direction.AxisDirection.POSITIVE,dir.getAxis()));
            VoxelShape vs2 = Functions.getGridShape(isRotated,Direction.get(Direction.AxisDirection.NEGATIVE,dir.getAxis()));
            return VoxelShapes.or(vs1,vs2);
        }else {
            VoxelShape vs = Functions.getSupportShape();
            int flags = Functions.getFlagsFromState(state);
            for (ExtendDirection direction : ExtendDirection.values()) {
                if ((flags | 1) == 1) {
                    VoxelShapes.or(vs, Functions.getGridShape(direction.isRotated(), direction.getDirection()));
                }
                flags = flags >> 1;
            }
            return vs;
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    public static AbstractPanelBlock createPanelInstance(Form form){
        return new PanelBlock(form);
    }

    public abstract ScreenType getScreenType();
    public abstract Form getForm();

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ScreenType type = this.getScreenType();
        Form form = this.getForm();
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (!worldIn.isClientSide() &&
                (player.getDirection() == facing.getOpposite())) {
            Networking.INSTANCE.sendTo(new PacketOpenScreen(pos,form,type),((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null){
            world.setBlockAndUpdate(pos,state.setValue(BlockStateProperties.HORIZONTAL_FACING, Functions.getDirectionFromEntity(entity,pos)));
        }
    }

    public static BlockState getBlockStateFromSupport(int form,BlockState supportState,int facing,boolean rotated){
        Form f = Form.byIndex(form);
        assert f != null;
        AbstractPanelBlock panelBlock = PanelRegister.asPanel(f);
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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
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
    public void playerDestroy(World world, PlayerEntity entity, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack) {
        super.playerDestroy(world, entity, pos, Blocks.AIR.defaultBlockState(), tileEntity, stack);
    }

    @Override
    public void destroy(IWorld world, BlockPos pos, BlockState state) {
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
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return new ItemStack(PanelItem.INSTANCE);
    }
}
