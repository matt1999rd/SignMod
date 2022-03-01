package fr.matt1999rd.signs.fixedpanel.support;

import fr.matt1999rd.signs.enums.ExtendDirection;
import fr.matt1999rd.signs.fixedpanel.ModBlock;
import fr.matt1999rd.signs.util.Functions;
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
import net.minecraft.world.World;

import java.util.Objects;

public class GridSupportItem extends BlockItem {
    public GridSupportItem(Properties builder) {
        super(ModBlock.GRID_SUPPORT, builder);
        this.setRegistryName("grid_support");
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        BlockState state = world.getBlockState(pos);
        BlockItemUseContext context1 = new BlockItemUseContext(context);
        PlayerEntity player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (Functions.isSignSupport(state) || Functions.isGridSupport(state)){
            assert player != null;
            ExtendDirection direction = ExtendDirection.getFacingFromPlayer(player,pos);
            if (Functions.isGridSupport(state) && !checkAxisFutureAlignment(state,direction)){
                return ActionResultType.FAIL;
            }
            BlockPos futureGridPos = direction.relative(pos);
            BlockState oldState = world.getBlockState(futureGridPos);
            if (oldState.canBeReplaced(context1)){
                BlockState state2 = ((GridSupport)getBlock()).getStateFromPos(futureGridPos,pos);
                if (state2 == null) {
                    return ActionResultType.FAIL;
                } else if (!this.placeBlock(world,state2,futureGridPos)) {
                    return ActionResultType.FAIL;
                } else {
                    BlockState blockState1 = world.getBlockState(futureGridPos);
                    Block block = blockState1.getBlock();
                    if (block == state2.getBlock()) {
                        if (player instanceof ServerPlayerEntity) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, futureGridPos, itemstack);
                        }
                    }
                    itemstack.shrink(1);
                    SoundType soundtype = blockState1.getSoundType(world, futureGridPos, context.getPlayer());
                    world.playSound(player, futureGridPos, this.getPlaceSound(blockState1, world, futureGridPos, Objects.requireNonNull(context.getPlayer())), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.FAIL;
    }

    private boolean checkAxisFutureAlignment(BlockState state, ExtendDirection direction) {
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        boolean isRotated = state.getValue(GridSupport.ROTATED);
        return (isRotated == direction.isRotated()) && (direction.getDirection().getAxis() == axis);
    }

    private boolean placeBlock(World world,BlockState state,BlockPos pos){
        return world.setBlock(pos, state, 11);
    }



}
