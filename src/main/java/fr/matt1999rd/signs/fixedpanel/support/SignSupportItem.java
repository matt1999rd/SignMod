package fr.matt1999rd.signs.fixedpanel.support;

import fr.matt1999rd.signs.fixedpanel.ModBlock;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;


public class SignSupportItem extends BlockItem {

    public SignSupportItem(Properties builder) {
        super(ModBlock.SIGN_SUPPORT, builder);
        this.setRegistryName("sign_support");
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPlaceContext blockItemUseContext = new BlockPlaceContext(context);
        BlockPos pos = blockItemUseContext.getClickedPos();
        Level world = blockItemUseContext.getLevel();
        BlockState state=world.getBlockState(pos.below());
        if (isSupportOrSolidBlock(state)){
            return super.useOn(context);
        }
        return InteractionResult.FAIL;
    }

    private boolean isSupportOrSolidBlock(BlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return !(block instanceof AirBlock || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable());
    }
}
