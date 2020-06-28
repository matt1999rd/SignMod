package fr.mattmouss.signs.fixedpanel.support;

import com.mojang.datafixers.types.Func;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.fixedpanel.ModBlock;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GridSupportItem extends BlockItem {
    public GridSupportItem(Properties builder) {
        super(ModBlock.GRID_SUPPORT, builder);
        this.setRegistryName("grid_support");
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockItemUseContext blockItemUseContext = new BlockItemUseContext(context);
        BlockPos pos = blockItemUseContext.getPos();
        World world = blockItemUseContext.getWorld();
        if (existSupportNearBy(world,pos) || existGridInRightWay(world,pos)){
            return super.onItemUse(context);
        }
        return ActionResultType.FAIL;
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

    private boolean existSupportNearBy(World world, BlockPos pos) {
        Direction[] directions = Direction.values();
        //test for all horizontal direction and diagonal direction (direction offset and rotated direction offset)
        for (int i=2;i<6;i++){
            Direction dir = directions[i];
            BlockState horBlockState = world.getBlockState(pos.offset(dir));
            BlockState diagBlockState = world.getBlockState(pos.offset(dir).offset(dir.rotateY()));
            if (Functions.isSignSupport(horBlockState) || Functions.isSignSupport(diagBlockState)){
                return true;
            }
        }
        return false;
    }


}
