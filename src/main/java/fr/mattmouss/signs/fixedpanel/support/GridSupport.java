package fr.mattmouss.signs.fixedpanel.support;

import fr.mattmouss.signs.enums.ExtendDirection;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static fr.mattmouss.signs.util.Functions.isSupportOrGrid;

public class GridSupport extends Block {

    //this property specified if the grid support is rotated in trigonometric rotation of angle 45°
    public static BooleanProperty ROTATED;
    static {
        ROTATED = BooleanProperty.create("rotated");
    }
    public GridSupport() {
        super(Properties.create(Material.ROCK));
        this.setRegistryName("grid_support");
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction.Axis axis = state.get(BlockStateProperties.HORIZONTAL_AXIS);
        boolean isRotated = state.get(ROTATED);
        VoxelShape vs1 = Functions.getGridShape(isRotated,Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE,axis));
        VoxelShape vs2 = Functions.getGridShape(isRotated,Direction.getFacingFromAxis(Direction.AxisDirection.NEGATIVE,axis));
        return VoxelShapes.or(vs1,vs2);
    }

    //1.14.4 function replaced by notSolid()
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_AXIS,ROTATED);
    }



    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer != null){
            Block eastBlock = worldIn.getBlockState(pos.east()).getBlock();
            Block westBlock = worldIn.getBlockState(pos.west()).getBlock();
            Block northBlock = worldIn.getBlockState(pos.north()).getBlock();
            Block southBlock = worldIn.getBlockState(pos.south()).getBlock();
            Block northEastBlock = worldIn.getBlockState(pos.east().north()).getBlock();
            Block northWestBlock = worldIn.getBlockState(pos.west().north()).getBlock();
            Block southEastBlock = worldIn.getBlockState(pos.south().east()).getBlock();
            Block southWestBlock = worldIn.getBlockState(pos.south().west()).getBlock();
            //todo : check context and make grid in the right direction if grid or support are clicked
            //todo :  check grid more completely (do not allow grids in both axis connected)
            if (isSupportOrGrid(eastBlock) || isSupportOrGrid(westBlock)){
                worldIn.setBlockState(pos,state
                        .with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X)
                        .with(ROTATED,false)
                );
            }else if (isSupportOrGrid(northBlock) || isSupportOrGrid(southBlock)){
                worldIn.setBlockState(pos,state
                        .with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.Z)
                        .with(ROTATED,false)
                );
            }else if (isSupportOrGrid(southEastBlock) || isSupportOrGrid(northWestBlock)) {
                worldIn.setBlockState(pos,state
                        .with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.Z)
                        .with(ROTATED,true)
                );
            }else if (isSupportOrGrid(northEastBlock) || isSupportOrGrid(southWestBlock)) {
                worldIn.setBlockState(pos,state
                        .with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X)
                        .with(ROTATED,true)
                );
            }else {
                throw new IllegalStateException("grid block cannot be created if there is no support block to support it.");
            }

        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void harvestBlock(World world, PlayerEntity entity, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack) {
        super.harvestBlock(world, entity, pos, Blocks.AIR.getDefaultState(), tileEntity, stack);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        Functions.deleteOtherGrid(pos,worldIn,player,state);
        Functions.deleteBlock(pos,worldIn,player);
        super.onBlockHarvested(worldIn, pos, state, player);
    }




}
