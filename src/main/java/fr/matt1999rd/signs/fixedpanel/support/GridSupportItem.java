package fr.matt1999rd.signs.fixedpanel.support;

import fr.matt1999rd.signs.enums.ExtendDirection;
import fr.matt1999rd.signs.fixedpanel.ModBlock;
import fr.matt1999rd.signs.util.Functions;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class GridSupportItem extends BlockItem {
    public GridSupportItem(Properties builder) {
        super(ModBlock.GRID_SUPPORT, builder);
        this.setRegistryName("grid_support");
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        BlockState state = world.getBlockState(pos);
        BlockPlaceContext context1 = new BlockPlaceContext(context);
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (Functions.isSignSupport(state) || Functions.isGridSupport(state)){
            assert player != null;
            ExtendDirection direction = ExtendDirection.getFacingFromPlayer(player,pos);
            if (Functions.isGridSupport(state) && !checkAxisFutureAlignment(state,direction)){
                return InteractionResult.FAIL;
            }
            BlockPos futureGridPos = direction.relative(pos);
            BlockState oldState = world.getBlockState(futureGridPos);
            if (oldState.canBeReplaced(context1)){
                BlockState state2 = ((GridSupport)getBlock()).getStateFromPos(futureGridPos,pos);
                if (state2 == null) {
                    return InteractionResult.FAIL;
                } else if (!this.placeBlock(world,state2,futureGridPos)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockState blockState1 = world.getBlockState(futureGridPos);
                    Block block = blockState1.getBlock();
                    if (block == state2.getBlock()) {
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, futureGridPos, itemstack);
                        }
                    }
                    itemstack.shrink(1);
                    SoundType soundtype = blockState1.getSoundType(world, futureGridPos, context.getPlayer());
                    world.playSound(player, futureGridPos, this.getPlaceSound(blockState1, world, futureGridPos, Objects.requireNonNull(context.getPlayer())), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.FAIL;
    }

    private boolean checkAxisFutureAlignment(BlockState state, ExtendDirection direction) {
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS); //todo : changer cette ligne, des panneaux sur grid n'ont pas de horizontal_axis
        boolean isRotated = state.getValue(GridSupport.ROTATED);
        return (isRotated == direction.isRotated()) && (direction.getDirection().getAxis() == axis);
    }

    private boolean placeBlock(Level world,BlockState state,BlockPos pos){
        return world.setBlock(pos, state, 11);
    }



}
