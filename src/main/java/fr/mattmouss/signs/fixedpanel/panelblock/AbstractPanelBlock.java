package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.PanelRegister;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupport;
import fr.mattmouss.signs.util.Functions;
import javafx.scene.layout.Pane;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import javax.annotation.Nullable;

public abstract class AbstractPanelBlock extends Block {
    public AbstractPanelBlock(String name) {
        super(Properties.create(Material.ROCK).doesNotBlockMovement());
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


    public static AbstractPanelBlock asPanel(Form form){
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

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null){
            world.setBlockState(pos,state.with(BlockStateProperties.HORIZONTAL_FACING, Functions.getDirectionFromEntity(entity,pos)));
        }
    }

    public static BlockState getBlockStateFromSupport(int form,BlockState supportState,int facing,boolean rotated){
        Form f = Form.byIndex(form);
        AbstractPanelBlock panelBlock = AbstractPanelBlock.asPanel(f);
        BlockState panelState = panelBlock.getStateContainer().getBaseState();
        if (supportState.getBlock() instanceof SignSupport){
            panelState = panelState.with(GRID,false);
        }else if (supportState.getBlock() instanceof GridSupport){
            panelState = panelState.with(GRID,true);
        }else{
            throw new IllegalArgumentException("The state in argument must be a sign_support or grid_support !!");
        }
        return panelState.with(GridSupport.ROTATED,rotated).with(BlockStateProperties.HORIZONTAL_FACING, Direction.byHorizontalIndex(facing));
    }



    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos up_pos = context.getPos().up();
        BlockPos down_pos = context.getPos().down();
        World world = context.getWorld();
        BlockState up_state = world.getBlockState(up_pos);
        BlockState down_state = world.getBlockState(down_pos);
        return super.getStateForPlacement(context).with(BlockStateProperties.UP,isSupportBlock(up_state)).with(BlockStateProperties.DOWN,isSupportBlock(down_state));
    }

    private boolean isSupportBlock(BlockState state) {
        return state.getBlock() instanceof SignSupport;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(
                BlockStateProperties.HORIZONTAL_FACING,
                BlockStateProperties.DOWN,
                BlockStateProperties.UP,
                GridSupport.ROTATED,
                GRID);
    }

}
