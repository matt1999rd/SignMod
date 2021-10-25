package fr.mattmouss.signs.fixedpanel.support;

import com.mojang.datafixers.types.Func;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.ExtendDirection;
import fr.mattmouss.signs.fixedpanel.ModBlock;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class GridSupportItem extends BlockItem {
    public GridSupportItem(Properties builder) {
        super(ModBlock.GRID_SUPPORT, builder);
        this.setRegistryName("grid_support");
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos pos = context.getPos();
        World world = context.getWorld();
        BlockState state = world.getBlockState(pos);
        BlockItemUseContext context1 = new BlockItemUseContext(context);
        PlayerEntity player = context.getPlayer();
        ItemStack itemstack = context.getItem();
        if (Functions.isSignSupport(state) || Functions.isGridSupport(state)){
            ExtendDirection direction = ExtendDirection.getFacingFromPlayer(player,pos);
            if (Functions.isGridSupport(state) && !checkAxisFutureAlignement(state,direction)){
                return ActionResultType.FAIL;
            }
            BlockPos futureGridPos = direction.offset(pos);
            BlockState oldState = world.getBlockState(futureGridPos);
            if (oldState.isReplaceable(context1)){
                BlockState state2 = ((GridSupport)getBlock()).getStateFromPos(futureGridPos,pos);
                if (state2 == null) {
                    return ActionResultType.FAIL;
                } else if (!this.placeBlock(world,state2,futureGridPos)) {
                    return ActionResultType.FAIL;
                } else {
                    BlockState blockstate1 = world.getBlockState(futureGridPos);
                    Block block = blockstate1.getBlock();
                    if (block == state2.getBlock()) {
                        if (player instanceof ServerPlayerEntity) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, futureGridPos, itemstack);
                        }
                    }
                    itemstack.shrink(1);
                    SoundType soundtype = blockstate1.getSoundType(world, futureGridPos, context.getPlayer());
                    world.playSound(player, futureGridPos, this.getPlaceSound(blockstate1, world, futureGridPos, context.getPlayer()), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.FAIL;
    }

    private boolean checkAxisFutureAlignement(BlockState state, ExtendDirection direction) {
        Direction.Axis axis = state.get(BlockStateProperties.HORIZONTAL_AXIS);
        boolean isRotated = state.get(GridSupport.ROTATED);
        return (isRotated == direction.isRotated()) && (direction.getDirection().getAxis() == axis);
    }

    private boolean placeBlock(World world,BlockState state,BlockPos pos){
        return world.setBlockState(pos, state, 11);
    }



}
