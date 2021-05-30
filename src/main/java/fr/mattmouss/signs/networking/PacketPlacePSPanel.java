package fr.mattmouss.signs.networking;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

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
        this.facing = (byte) facing.getHorizontalIndex();
        this.rotated = rotated;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerWorld world = Objects.requireNonNull(ctx.get().getSender()).getServerWorld();
            BlockState supportState = world.getBlockState(panelFuturePos);
            BlockState panelState = AbstractPanelBlock.getBlockStateFromSupportForPS(displayMode,supportState,facing,rotated);
            world.setBlockState(panelFuturePos, panelState,11);
        });
        ctx.get().setPacketHandled(true);
    }
}
