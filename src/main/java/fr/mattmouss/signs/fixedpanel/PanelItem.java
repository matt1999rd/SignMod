package fr.mattmouss.signs.fixedpanel;

import fr.mattmouss.signs.enums.ExtendDirection;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupport;
import fr.mattmouss.signs.networking.Networking;
import fr.mattmouss.signs.networking.PacketChoicePanel;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import sun.security.x509.EDIPartyName;

public class PanelItem extends Item {
    public PanelItem(Properties properties) {
        super(properties);
        this.setRegistryName("panel_item");
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        World world = context.getWorld();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Direction facing =null;
        boolean rotated = false;
        if (block instanceof GridSupport){
            Direction.Axis axis = state.get(BlockStateProperties.HORIZONTAL_AXIS);
            rotated = state.get(GridSupport.ROTATED);
            facing = getGridFacingDirection(axis,player,pos,rotated);
        }else if (block instanceof SignSupport){
            ExtendDirection extendDirection = getSupportFacingDirection(state,player,pos);
            if (extendDirection == null)return ActionResultType.FAIL;
            facing = extendDirection.getDirection();
            rotated = extendDirection.isRotated();
        }

        if (player instanceof ServerPlayerEntity && (block instanceof SignSupport || block instanceof GridSupport)&& facing != null){
            Networking.INSTANCE.sendTo(new PacketChoicePanel(pos,facing,rotated),((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
        return super.onItemUse(context);
    }

    public ExtendDirection getSupportFacingDirection(BlockState state, PlayerEntity player, BlockPos pos) {
        ExtendDirection direction = ExtendDirection.getFacingFromPlayer(player,pos);
        BooleanProperty centerDirectionProperty = direction.getSupportProperty();
        ExtendDirection leftDirection = direction.rotateY();
        ExtendDirection rightDirection = direction.rotateYCCW();
        BooleanProperty leftDirectionProperty = leftDirection.getSupportProperty();
        BooleanProperty rightDirectionProperty = rightDirection.getSupportProperty();
        //if central direction is cut by a grid, this is not going to work
        //if both are activated it will not work
        if (state.get(centerDirectionProperty)||
                (state.get(leftDirectionProperty) && state.get(rightDirectionProperty))){
            return null;
        }else{
            if (state.get(leftDirectionProperty)){
                //right is free we return the right direction
                return rightDirection;
            }else if (state.get(rightDirectionProperty)){
                //left is free we return the left direction
                return leftDirection;
            }
        }
        return direction; // no grid on support : we return the right direction
    }


    private Direction getGridFacingDirection(Direction.Axis axis, PlayerEntity player, BlockPos pos,boolean rotated) {
        Direction.AxisDirection axisDirection;
        Direction.Axis oppositeAxis = (axis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;
        switch (oppositeAxis) {
            case X:
                if (rotated){
                    //we are comparing point that are up of the line z = x+ Zoffset
                    //to make it we reduce to the blockPos as new origin and then compare to z= x line
                    Vec3d player_pos = player.getPositionVec();
                    Vec3d panel_origin_pos = Functions.getVecFromBlockPos(pos,0.0F);
                    player_pos =player_pos.subtract(panel_origin_pos);
                    axisDirection = (player_pos.x>player_pos.z) ?
                            Direction.AxisDirection.POSITIVE :
                            Direction.AxisDirection.NEGATIVE;
                }else {
                    //just comparing vertically
                    axisDirection = (player.getPositionVec().x < pos.getX() + 0.5F) ?
                            Direction.AxisDirection.NEGATIVE :
                            Direction.AxisDirection.POSITIVE;
                }
                break;
                case Z:
                    if (rotated){
                        //when rotated this way we translate position to the center of the grid
                        //then the position of the panel depends on the sum of x and z following the comparison to the line z = -x
                        Vec3d player_pos = player.getPositionVec();
                        Vec3d panel_origin_pos = Functions.getVecFromBlockPos(pos,0.5F);
                        player_pos=player_pos.subtract(panel_origin_pos);
                        axisDirection = (player_pos.x+player_pos.z>0) ?
                                Direction.AxisDirection.POSITIVE :
                                Direction.AxisDirection.NEGATIVE;
                    }else {
                        //just comparing horizontally
                        axisDirection = (player.getPositionVec().z < pos.getZ() + 0.5F) ?
                                Direction.AxisDirection.NEGATIVE :
                                Direction.AxisDirection.POSITIVE;
                    }
                    break;
                case Y:
                default:
                    return null;
            }

            return Direction.getFacingFromAxis(axisDirection, oppositeAxis);
    }


}
