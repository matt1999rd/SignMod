package fr.matt1999rd.signs.fixedpanel.panelblock;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.enums.PSDisplayMode;
import fr.matt1999rd.signs.enums.PSPosition;
import fr.matt1999rd.signs.enums.ScreenType;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity;
import fr.matt1999rd.signs.util.VoxelInts;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

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
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return form.createTileEntity();
    }

    @Override
    public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (form == Form.SQUARE) {
            VoxelShape vs = super.getShape(state, worldIn, pos, context);
            VoxelInts vi_plane = new VoxelInts(3, 5, 6, 10, 10, 1, true);
            VoxelInts[] vi_diagonal = new VoxelInts[]{
                    new VoxelInts(3, 5, 10, 1, 10, 1, true),
                    new VoxelInts(4, 5, 9, 1, 10, 1, true),
                    new VoxelInts(5, 5, 8, 1, 10, 1, true),
                    new VoxelInts(6, 5, 7, 1, 10, 1, true),
                    new VoxelInts(7, 5, 6, 1, 10, 1, true),
                    new VoxelInts(8, 5, 5, 1, 10, 1, true),
                    new VoxelInts(9, 5, 4, 1, 10, 1, true),
                    new VoxelInts(10, 5, 3, 1, 10, 1, true)
            };
            boolean isRotated = state.getValue(GridSupport.ROTATED);
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            if (isRotated) {
                for (int i = 0; i < 8; i++) {
                    vs = VoxelShapes.or(vs, vi_diagonal[i].rotate(Direction.NORTH, facing).getAssociatedShape());
                }
            } else {
                vs = VoxelShapes.or(vs, vi_plane.rotate(Direction.NORTH, facing).getAssociatedShape());
            }
            return vs;
        }
        return super.getShape(state, worldIn, pos, context);
    }

    @Override
    public void destroy(IWorld worldIn, BlockPos pos, BlockState state) {
        if (form == Form.PLAIN_SQUARE) {
            TileEntity te = worldIn.getBlockEntity(pos);
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
