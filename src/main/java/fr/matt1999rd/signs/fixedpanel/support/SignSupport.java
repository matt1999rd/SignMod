package fr.matt1999rd.signs.fixedpanel.support;

import fr.matt1999rd.signs.enums.ExtendDirection;
import fr.matt1999rd.signs.util.Functions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;


public class SignSupport extends Block implements EntityBlock {

    public SignSupport() {
        super(Properties.of(Material.STONE, MaterialColor.STONE).noOcclusion());
        this.setRegistryName("sign_support");
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        VoxelShape vs = Functions.getSupportShape();
        int flags = Functions.getFlagsFromState(state);
        for (ExtendDirection direction : ExtendDirection.values()){
            if ((flags&1)==1){
                assert direction != null;
                vs=Shapes.or(vs,Functions.getGridShape(direction.isRotated(),direction.getDirection()));
            }
            flags = flags >> 1;
        }
        return vs;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SignSupportTileEntity(blockPos,blockState);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void playerDestroy(Level world, Player entity, BlockPos pos, BlockState state, @Nullable BlockEntity tileEntity, ItemStack stack) {
        super.playerDestroy(world, entity, pos, Blocks.AIR.defaultBlockState(), tileEntity, stack);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        Functions.manageDeletionOfSupportedBlock(world, pos, state, player);
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer != null){
            Functions.setBlockState(worldIn,pos,state,0);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockState downBlockState = worldIn.getBlockState(pos.below());
        if (Functions.isSignSupport(downBlockState)){
            return true;
        }else {
            return downBlockState.isFaceSturdy(worldIn,pos.below(), Direction.UP);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(Arrays.stream(ExtendDirection.values()).map(ExtendDirection::getSupportProperty).toArray(BooleanProperty[]::new));
    }

}
