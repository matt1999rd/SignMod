package fr.matt1999rd.signs.fixedpanel.panelblock;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.enums.PSDisplayMode;
import fr.matt1999rd.signs.enums.PSPosition;
import fr.matt1999rd.signs.enums.ScreenType;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.tileentity.PanelTileEntity;
import fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity;
import fr.matt1999rd.signs.util.VoxelDouble;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumMap;

public class PanelBlock extends AbstractPanelBlock {

    public final Form form ;
    public static final PSPosition DEFAULT_RIGHT_POSITION = PSPosition.DOWN_RIGHT;

    public PanelBlock(Form form) {
        super(form.getObjName());
        this.form = form;
    }

    @Override
    public ScreenType getScreenType() {
        return form.getScreenType();
    }

    @Override
    public Form getForm() {
        return form;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (level1, blockPos, blockState, t) -> {
            if (t instanceof PanelTileEntity) {
                ((PanelTileEntity) t).tick(level1, blockState, blockPos, (PanelTileEntity) t);
            }
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return form.createTileEntity(blockPos, blockState);
    }

    @Override
    public void onRemove(BlockState p_196243_1_, Level p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (form == Form.SQUARE) {
            VoxelShape vs = super.getShape(state, worldIn, pos, context);
            VoxelDouble vi_plane = new VoxelDouble(3, 5, 6, 10, 10, 1, true);
            VoxelDouble[] vi_diagonal = new VoxelDouble[]{
                    new VoxelDouble(3, 5, 10, 1, 10, 1, true),
                    new VoxelDouble(4, 5, 9, 1, 10, 1, true),
                    new VoxelDouble(5, 5, 8, 1, 10, 1, true),
                    new VoxelDouble(6, 5, 7, 1, 10, 1, true),
                    new VoxelDouble(7, 5, 6, 1, 10, 1, true),
                    new VoxelDouble(8, 5, 5, 1, 10, 1, true),
                    new VoxelDouble(9, 5, 4, 1, 10, 1, true),
                    new VoxelDouble(10, 5, 3, 1, 10, 1, true)
            };
            boolean isRotated = state.getValue(GridSupport.ROTATED);
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            if (isRotated) {
                for (int i = 0; i < 8; i++) {
                    vs = Shapes.or(vs, vi_diagonal[i].rotate(Direction.NORTH, facing).getAssociatedShape());
                }
            } else {
                vs = Shapes.or(vs, vi_plane.rotate(Direction.NORTH, facing).getAssociatedShape());
            }
            return vs;
        }
        return super.getShape(state, worldIn, pos, context);
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (form == Form.PLAIN_SQUARE) {
            BlockEntity te = worldIn.getBlockEntity(pos);
            PlainSquareSignTileEntity psste = te instanceof PlainSquareSignTileEntity ? ((PlainSquareSignTileEntity) te) : null;
            if (psste == null) return;
            PSPosition psPosition = psste.getPosition();
            PSDisplayMode mode = psste.getMode();
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            EnumMap<PSPosition, BlockPos> neiPos = psPosition.getNeighborPosition(pos, facing, mode.is2by2());
            neiPos.remove(psPosition);
            neiPos.values().forEach(pos1 -> super.destroy(worldIn, pos1, worldIn.getBlockState(pos1)));
        }
        super.destroy(worldIn, pos, state);
    }
}
