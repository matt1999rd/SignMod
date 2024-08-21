package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.gui.ChoiceScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketChoicePanel {
    private final BlockPos panelFuturePos;
    private final byte facing;
    private final boolean rotated;
    private final boolean isGrid;
    private final boolean has4Grid;

    public PacketChoicePanel(FriendlyByteBuf buf){
        panelFuturePos = buf.readBlockPos();
        facing = buf.readByte();
        rotated = buf.readBoolean();
        isGrid = buf.readBoolean();
        has4Grid = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(panelFuturePos);
        buf.writeByte(facing);
        buf.writeBoolean(rotated);
        buf.writeBoolean(isGrid);
        buf.writeBoolean(has4Grid);
    }

    public PacketChoicePanel(BlockPos pos, Direction facing,boolean rotated,boolean isGrid,boolean has4Grid){
        panelFuturePos = pos;
        this.facing = (byte) facing.get2DDataValue();
        this.rotated = rotated;
        this.isGrid = isGrid;
        this.has4Grid = has4Grid;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> ChoiceScreen.open(panelFuturePos,Direction.from2DDataValue(facing),rotated,isGrid,has4Grid));
        ctx.get().setPacketHandled(true);
    }
}
