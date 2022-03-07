package fr.matt1999rd.signs.fixedpanel.panelblock;

import fr.matt1999rd.signs.enums.*;
import fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumMap;


public class PlainSquarePanelBlock extends AbstractPanelBlock {
    public PlainSquarePanelBlock() {
        super("huge_direction");
    }
    public static final PSPosition DEFAULT_RIGHT_POSITION = PSPosition.DOWN_RIGHT;
    public static final PSPosition DEFAULT_LEFT_POSITION = PSPosition.UP_LEFT;


    @Override
    public ScreenType getScreenType() {
        return ScreenType.PLAIN_SQUARE_SCREEN;
    }

    @Override
    public Form getForm() {
        return Form.PLAIN_SQUARE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PlainSquareSignTileEntity();
    }

    @Override
    public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
    }

    @Override
    public void destroy(IWorld worldIn, BlockPos pos, BlockState state) {
        TileEntity te = worldIn.getBlockEntity(pos);
        PlainSquareSignTileEntity psste = te instanceof PlainSquareSignTileEntity ? ((PlainSquareSignTileEntity) te) : null;
        if (psste == null)return;
        PSPosition psPosition = psste.getPosition();
        PSDisplayMode mode = psste.getMode();
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        EnumMap<PSPosition,BlockPos> neiPos = psPosition.getNeighborPosition(pos,facing,mode.is2by2());
        neiPos.remove(psPosition);
        neiPos.values().forEach(pos1 -> super.destroy(worldIn,pos1,worldIn.getBlockState(pos1)));
        super.destroy(worldIn, pos, state);
    }

}
