package fr.mattmouss.signs.fixedpanel.panelblock;

import fr.mattmouss.signs.enums.*;
import fr.mattmouss.signs.fixedpanel.ModBlock;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.tileentity.primary.PlainSquareSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
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
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity te = world.getTileEntity(pos);
        PlainSquareSignTileEntity psste = te instanceof PlainSquareSignTileEntity ? ((PlainSquareSignTileEntity) te) : null;
        if (psste == null)return;
        PSPosition psPosition = psste.getPosition();
        PSDisplayMode mode = psste.getMode();
        Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
        EnumMap<PSPosition,BlockPos> neiPos = psPosition.getNeighborPosition(pos,facing,mode.is2by2());
        neiPos.remove(psPosition);
        boolean grid = state.get(GRID);
        BlockState oldState;
        if (grid){
            oldState = ModBlock.GRID_SUPPORT.getDefaultState().with(BlockStateProperties.HORIZONTAL_AXIS,facing.rotateY().getAxis());
        }else {
            oldState = ModBlock.SIGN_SUPPORT.getDefaultState();
        }
        oldState = oldState.with(GridSupport.ROTATED,state.get(GridSupport.ROTATED));
        for (PSPosition position : neiPos.keySet()){
            world.setBlockState(neiPos.get(position), oldState);
        }
        super.onBlockHarvested(world, pos, state, player);
        world.setBlockState(pos,oldState);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        BlockState oldState = searchNeighBorGrid(worldIn.getWorld(),pos);
        worldIn.setBlockState(pos,oldState,11);
    }

    private BlockState searchNeighBorGrid(World world,BlockPos pos){
        for (ExtendDirection direction : ExtendDirection.values()){
            BlockState neiState = world.getBlockState(direction.offset(pos));
            if (neiState.getBlock() instanceof GridSupport){
                return neiState;
            }
        }
        return Blocks.AIR.getDefaultState();
    }
}
