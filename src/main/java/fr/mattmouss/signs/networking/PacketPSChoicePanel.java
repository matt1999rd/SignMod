package fr.mattmouss.signs.networking;

import fr.mattmouss.signs.gui.PSDisplayModeScreen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPSChoicePanel {
    private final BlockPos panelFuturePos;
    private final byte facing;
    private final boolean rotated;
    private final byte authoring;

    public PacketPSChoicePanel(PacketBuffer buf){
        panelFuturePos = buf.readBlockPos();
        facing = buf.readByte();
        rotated = buf.readBoolean();
        authoring = buf.readByte();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeBlockPos(panelFuturePos);
        buf.writeByte(facing);
        buf.writeBoolean(rotated);
        buf.writeByte(authoring);
    }

    public PacketPSChoicePanel(BlockPos pos, Direction facing, boolean rotated,byte authoring){
        panelFuturePos = pos;
        this.facing = (byte) facing.getHorizontalIndex();
        this.rotated = rotated;
        this.authoring = authoring;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            PSDisplayModeScreen.open(panelFuturePos,Direction.byHorizontalIndex(facing),rotated,authoring);
        });
        ctx.get().setPacketHandled(true);
    }
}
