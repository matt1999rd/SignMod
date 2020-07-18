package fr.mattmouss.signs.fixedpanel.panelblock;


import fr.mattmouss.signs.enums.ExtendDirection;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.ScreenType;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.tileentity.primary.SquareSignTileEntity;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.VoxelInts;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import org.omg.CORBA.portable.ValueInputStream;

import javax.annotation.Nullable;

public class SquarePanelBlock extends AbstractPanelBlock {
    public SquarePanelBlock() {
        super("square");
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape vs = super.getShape(state, worldIn, pos, context);
        VoxelInts vi_plane = new VoxelInts(3,3,6,10,10,1,true);
        VoxelInts[] vi_diag = new VoxelInts[]{
                new VoxelInts(3 ,3,10,1,10,1,true),
                new VoxelInts(4 ,3,9 ,1,10,1,true),
                new VoxelInts(5 ,3,8 ,1,10,1,true),
                new VoxelInts(6 ,3,7 ,1,10,1,true),
                new VoxelInts(7 ,3,6 ,1,10,1,true),
                new VoxelInts(8 ,3,5 ,1,10,1,true),
                new VoxelInts(9 ,3,4 ,1,10,1,true),
                new VoxelInts(10,3,3 ,1,10,1,true)
        };
        boolean isRotated = state.get(GridSupport.ROTATED);
        Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
        if (isRotated){
            for (int i=0;i<8;i++){
                vs = VoxelShapes.or(vs,vi_diag[i].rotate(Direction.NORTH,facing).getAssociatedShape());
            }
        }else {
            vs = VoxelShapes.or(vs,vi_plane.rotate(Direction.NORTH,facing).getAssociatedShape());
        }
        return vs;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SquareSignTileEntity();
    }

    @Override
    public ScreenType getScreenType() {
        return ScreenType.DRAWING_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.SQUARE;
    }

}
