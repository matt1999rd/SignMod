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
            facing = extendDirection.getDirection();
            rotated = extendDirection.isRotated();
        }

        if (player instanceof ServerPlayerEntity && (block instanceof SignSupport || block instanceof GridSupport)&& facing != null){
            Networking.INSTANCE.sendTo(new PacketChoicePanel(pos,facing,rotated),((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
        return super.onItemUse(context);
    }

    private ExtendDirection getSupportFacingDirection(BlockState state, PlayerEntity player, BlockPos pos) {
        //for the purpose of getting the state of the panel we divide space around the center of the support in 8 part
        //division are for angle 22.5/67.5/112.5/157.5/202.5/247.5/292.5/337.5 (22.5+45*i for 0<=i<=7)
        Vec3d support_center = Functions.getVecFromBlockPos(pos,0.5F);
        Vec3d offsetPlayerPos = player.getPositionVec().subtract(support_center);
        //we compare our player position to the position of the support's center
        //we get angle using arctan function
        double angle = MathHelper.atan2(offsetPlayerPos.x,offsetPlayerPos.z);
        //we convert to degree and make it positive
        double degreeAngle =Functions.toDegree(angle);
        //then to index from 2 to 9 corresponding to the part of stage where the player is
        int index = MathHelper.ceil((degreeAngle-22.5D)/45.0D);
        //we consider the part split by angle origin which correpond to 0
        // that we move of a complete circle for math simplification
        if (index == 0){
            index =8;
        }else if (index == 1){
            index =9;
        }
        //we get facing using a special index that is for i : 0->7 --> 0 0 3 3 2 2 1 1
        //we have translated 0 to 8 and 1 to 9 to get a decreasing linear function -->
        // 2->3 3->3 4->2 5->2 6->1 7->1 8->0 9->0
        Direction facing = Direction.byHorizontalIndex((9-index)/2);
        boolean isRotated = (index%2 == 1);
        ExtendDirection direction = ExtendDirection.getExtendedDirection(facing,isRotated);
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
                    //we are comparing point that are up of the line y = x+ Zoffset
                    //to make it we reduce to the blockPos as new origin and then compare to y= x line
                    Vec3d player_pos = player.getPositionVec();
                    Vec3d panel_origin_pos = Functions.getVecFromBlockPos(pos,0.0F);
                    player_pos =player_pos.subtract(panel_origin_pos);
                    axisDirection = (player_pos.x>player_pos.z) ?
                            Direction.AxisDirection.NEGATIVE :
                            Direction.AxisDirection.POSITIVE;
                }else {
                    //just comparing vertically
                    axisDirection = (player.getPositionVec().x < pos.getX() + 0.5F) ?
                            Direction.AxisDirection.POSITIVE :
                            Direction.AxisDirection.NEGATIVE;
                }
                break;
                case Z:
                    if (rotated){
                        Vec3d player_pos = player.getPositionVec();
                        Vec3d panel_origin_pos = Functions.getVecFromBlockPos(pos,0.5F);
                        player_pos=player_pos.subtract(panel_origin_pos);
                        axisDirection = (player_pos.x+player_pos.z<0) ?
                                Direction.AxisDirection.NEGATIVE :
                                Direction.AxisDirection.POSITIVE;
                    }else {
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
