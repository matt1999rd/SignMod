package fr.matt1999rd.signs.fixedpanel;

import fr.matt1999rd.signs.enums.ExtendDirection;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.fixedpanel.support.SignSupport;
import fr.matt1999rd.signs.networking.Networking;
import fr.matt1999rd.signs.networking.PacketChoicePanel;
import fr.matt1999rd.signs.setup.ModSetup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

public class PanelItem extends Item {
    public static final PanelItem INSTANCE = new PanelItem( new Item.Properties().tab(ModSetup.itemGroup));

    public PanelItem(Properties properties) {
        super(properties);
        this.setRegistryName("panel_item");
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Direction facing =null;
        boolean rotated = false;
        if (block instanceof GridSupport){
            Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            rotated = state.getValue(GridSupport.ROTATED);
            facing = getGridFacingDirection(axis,player,pos,rotated);
        }else if (block instanceof SignSupport){
            ExtendDirection extendDirection = getSupportFacingDirection(state,player,pos);
            if (extendDirection == null)return ActionResultType.FAIL;
            facing = extendDirection.getDirection();
            rotated = extendDirection.isRotated();
        }

        if (player instanceof ServerPlayerEntity && (block instanceof SignSupport || block instanceof GridSupport)&& facing != null){
            boolean isGrid = (block instanceof GridSupport);
            boolean has4Grid = Functions.has4Grid(context);
            Networking.INSTANCE.sendTo(new PacketChoicePanel(pos,facing,rotated,isGrid,has4Grid),((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
        return super.useOn(context);
    }

    public ExtendDirection getSupportFacingDirection(BlockState state, PlayerEntity player, BlockPos pos) {
        ExtendDirection direction = ExtendDirection.getFacingFromPlayer(player,pos);
        BooleanProperty centerDirectionProperty = direction.getSupportProperty();
        ExtendDirection leftDirection = direction.getClockWise();
        ExtendDirection rightDirection = direction.getCounterClockWise();
        BooleanProperty leftDirectionProperty = leftDirection.getSupportProperty();
        BooleanProperty rightDirectionProperty = rightDirection.getSupportProperty();
        //if central direction is cut by a grid, this is not going to work
        //if both are activated it will not work
        if (state.getValue(centerDirectionProperty)||
                (state.getValue(leftDirectionProperty) && state.getValue(rightDirectionProperty))){
            return null;
        }else{
            if (state.getValue(leftDirectionProperty)){
                //right is free we return the right direction
                return rightDirection;
            }else if (state.getValue(rightDirectionProperty)){
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
                    //we are comparing point that are up of the line z = x+ Z_offset
                    //to make it we reduce to the blockPos as new origin and then compare to z= x line
                    Vector3d player_pos = player.position();
                    Vector3d panel_origin_pos = Functions.getVecFromBlockPos(pos,0.0F);
                    player_pos =player_pos.subtract(panel_origin_pos);
                    axisDirection = (player_pos.x>player_pos.z) ?
                            Direction.AxisDirection.POSITIVE :
                            Direction.AxisDirection.NEGATIVE;
                }else {
                    //just comparing vertically
                    axisDirection = (player.position().x < pos.getX() + 0.5F) ?
                            Direction.AxisDirection.NEGATIVE :
                            Direction.AxisDirection.POSITIVE;
                }
                break;
                case Z:
                    if (rotated){
                        //when rotated this way we translate position to the center of the grid
                        //then the position of the panel depends on the sum of x and z following the comparison to the line z = -x
                        Vector3d player_pos = player.position();
                        Vector3d panel_origin_pos = Functions.getVecFromBlockPos(pos,0.5F);
                        player_pos=player_pos.subtract(panel_origin_pos);
                        axisDirection = (player_pos.x+player_pos.z>0) ?
                                Direction.AxisDirection.POSITIVE :
                                Direction.AxisDirection.NEGATIVE;
                    }else {
                        //just comparing horizontally
                        axisDirection = (player.position().z < pos.getZ() + 0.5F) ?
                                Direction.AxisDirection.NEGATIVE :
                                Direction.AxisDirection.POSITIVE;
                    }
                    break;
                case Y:
                default:
                    return null;
            }

            return Direction.get(axisDirection, oppositeAxis);
    }


}
