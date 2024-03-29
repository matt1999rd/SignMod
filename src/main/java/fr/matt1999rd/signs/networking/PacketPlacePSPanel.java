package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.enums.PSDisplayMode;
import fr.matt1999rd.signs.enums.PSPosition;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;


public class PacketPlacePSPanel {
    private final BlockPos panelFuturePos;
    private final int displayMode;
    private final byte facing;
    private final boolean rotated;

    public PacketPlacePSPanel(PacketBuffer buf){
        panelFuturePos = buf.readBlockPos();
        byte[] array = buf.readByteArray();
        displayMode = array[0];
        facing = array[1];
        rotated = buf.readBoolean();
    }

    public void toBytes(PacketBuffer buf){
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
            ServerWorld world = Objects.requireNonNull(ctx.get().getSender()).getLevel();
            BlockState supportState = world.getBlockState(panelFuturePos);
            //form meta for plain square is 7
            BlockState panelState = AbstractPanelBlock.getBlockStateFromSupport(7,supportState,facing,rotated);
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
                TileEntity tileEntity = world.getBlockEntity(neiBlockPos);
                if (tileEntity instanceof PlainSquareSignTileEntity){
                    ((PlainSquareSignTileEntity) tileEntity).registerData(neiPos,m);
                }else {
                    tileEntity = panelState.getBlock().createTileEntity(panelState,world);
                    assert tileEntity != null;
                    ((PlainSquareSignTileEntity) tileEntity).registerData(neiPos,m);
                }
                world.setBlockEntity(neiBlockPos,tileEntity);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
