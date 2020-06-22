package fr.mattmouss.signs.fixedpanel.support;

import fr.mattmouss.signs.fixedpanel.ModBlock;
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
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockItemUseContext blockItemUseContext = new BlockItemUseContext(context);
        BlockPos pos = blockItemUseContext.getPos();
        World world = blockItemUseContext.getWorld();
        BlockState state=world.getBlockState(pos.down());
        if (isSupportOrSolidBlock(state)){
            return super.onItemUse(context);
        }
        return ActionResultType.FAIL;
    }

    private boolean isSupportOrSolidBlock(BlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return !(block instanceof AirBlock || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable());
    }
}
