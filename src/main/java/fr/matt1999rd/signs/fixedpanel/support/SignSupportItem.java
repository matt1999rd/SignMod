package fr.matt1999rd.signs.fixedpanel.support;

import fr.matt1999rd.signs.fixedpanel.ModBlock;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class SignSupportItem extends BlockItem {

    public SignSupportItem(Properties builder) {
        super(ModBlock.SIGN_SUPPORT, builder);
        this.setRegistryName("sign_support");
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockItemUseContext blockItemUseContext = new BlockItemUseContext(context);
        BlockPos pos = blockItemUseContext.getClickedPos();
        World world = blockItemUseContext.getLevel();
        BlockState state=world.getBlockState(pos.below());
        if (isSupportOrSolidBlock(state)){
            return super.useOn(context);
        }
        return ActionResultType.FAIL;
    }

    private boolean isSupportOrSolidBlock(BlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return !(block instanceof AirBlock || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable());
    }
}
