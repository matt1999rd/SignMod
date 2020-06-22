package fr.mattmouss.signs.networking;

import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PacketPlacePanel {
    private final BlockPos panelFuturePos;
    private final int form;
    private final byte facing;
    private final boolean rotated;

    public PacketPlacePanel(PacketBuffer buf){
        panelFuturePos = buf.readBlockPos();
        form = buf.readInt();
        facing = buf.readByte();
        rotated = buf.readBoolean();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeBlockPos(panelFuturePos);
        buf.writeInt(form);
        buf.writeByte(facing);
        buf.writeBoolean(rotated);
    }

    public PacketPlacePanel(BlockPos pos, int form, Direction facing,boolean rotated){
        panelFuturePos = pos;
        this.form = form;
        this.facing = (byte) facing.getHorizontalIndex();
        this.rotated = rotated;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerWorld world = Objects.requireNonNull(ctx.get().getSender()).getServerWorld();
            BlockState supportState = world.getBlockState(panelFuturePos);
            BlockState panelState = AbstractPanelBlock.getBlockStateFromSupport(form,supportState,facing,rotated);
            world.setBlockState(panelFuturePos,panelState);
        });
        ctx.get().setPacketHandled(true);
    }
}
