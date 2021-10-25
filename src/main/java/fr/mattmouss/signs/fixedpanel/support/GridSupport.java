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
import net.minecraft.item.BlockItemUseContext;
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


import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


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

    public BlockState getStateFromPos(BlockPos pos,BlockPos supportPos) {
        BlockState state = this.getDefaultState();
        ExtendDirection dir = ExtendDirection.getDirectionFromPos(supportPos,pos);
        if (dir != null){
            return state.with(GridSupport.ROTATED,dir.isRotated()).with(BlockStateProperties.HORIZONTAL_AXIS,dir.getAxis());
        }else {
            return null;
        }
    }

    private boolean existGridInRightWay(World world, BlockPos pos) {
        Direction[] directions = Direction.values();
        //test for all horizontal direction and diagonal direction (direction offset and rotated direction offset)
        for (int i=2;i<6;i++){
            Direction dir = directions[i];
            BlockState horBlockState = world.getBlockState(pos.offset(dir));
            BlockState diagBlockState = world.getBlockState(pos.offset(dir).offset(dir.rotateY()));
            if (Functions.isGridSupport(horBlockState)){
                return true;
            }
            if (diagBlockState.getBlock() instanceof GridSupport){
                //the grid need to be rotated and the axis must not be the dir axis because of facingY choice
                return (diagBlockState.get(GridSupport.ROTATED))&& (dir.getAxis() != diagBlockState.get(BlockStateProperties.HORIZONTAL_AXIS));
            }
            if (diagBlockState.getBlock() instanceof AbstractPanelBlock) {
                //the grid need to be rotated and the facing must have the same axis as the dir axis because of facingY choice
                return (diagBlockState.get(GridSupport.ROTATED)) && (dir.getAxis() == diagBlockState.get(BlockStateProperties.HORIZONTAL_FACING).getAxis());
            }
        }
        return false;
    }

}
