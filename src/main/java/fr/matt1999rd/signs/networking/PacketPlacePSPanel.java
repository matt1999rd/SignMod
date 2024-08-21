package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.enums.PSDisplayMode;
import fr.matt1999rd.signs.enums.PSPosition;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;


public class PacketPlacePSPanel {
    private final BlockPos panelFuturePos;
    private final int displayMode;
    private final byte facing;
    private final boolean rotated;

    public PacketPlacePSPanel(FriendlyByteBuf buf){
        panelFuturePos = buf.readBlockPos();
        byte[] array = buf.readByteArray();
        displayMode = array[0];
        facing = array[1];
        rotated = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(panelFuturePos);
        byte[] array = new byte[2];
        array[0] = (byte)displayMode;
        array[1] = facing;
        buf.writeByteArray(array);
        buf.writeBoolean(rotated);
    }

    public PacketPlacePSPanel(BlockPos pos, int displayMode, Direction facing, boolean rotated){
        panelFuturePos = pos;
        this.displayMode = displayMode;
        this.facing = (byte) facing.get2DDataValue();
        this.rotated = rotated;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerLevel world = Objects.requireNonNull(ctx.get().getSender()).getLevel();
            BlockState supportState = world.getBlockState(panelFuturePos);
            BlockState panelState = AbstractPanelBlock.getBlockStateFromSupport(Form.PLAIN_SQUARE,supportState,facing,rotated);
            PSDisplayMode m = PSDisplayMode.byIndex((byte) displayMode);
            if (m == null) {
                return;
            }
            List<PSPosition> directionOfPlacement = PSPosition.listPlaceable(world,panelFuturePos,Direction.from2DDataValue(facing),m.is2by2(),false);
            if (directionOfPlacement.isEmpty()) {
                return;
            }
            //by default take first one in the list
            PSPosition position = directionOfPlacement.get(0);
            EnumMap<PSPosition,BlockPos> neighbors = position.getNeighborPosition(panelFuturePos,Direction.from2DDataValue(facing),m.is2by2());
            for (PSPosition neiPos : neighbors.keySet()){
                BlockPos neiBlockPos = neighbors.get(neiPos);
                world.setBlock(neiBlockPos, panelState,11);
                BlockEntity tileEntity = world.getBlockEntity(neiBlockPos);
                if (tileEntity instanceof PlainSquareSignTileEntity){
                    ((PlainSquareSignTileEntity) tileEntity).registerData(neiPos,m);
                }else {
                    EntityBlock entityBlock = (EntityBlock) panelState.getBlock();
                    tileEntity = entityBlock.newBlockEntity(neiBlockPos,panelState);
                    assert tileEntity != null;
                    ((PlainSquareSignTileEntity) tileEntity).registerData(neiPos,m);
                }
                world.setBlockEntity(tileEntity); //todo : a check need to be done here to ensure good porting
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
