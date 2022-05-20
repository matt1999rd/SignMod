package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.ClientAction;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PacketDrawingAction {
    private final BlockPos panelPos;
    private final byte action;
    private final byte x;
    private final byte y;
    private final byte length;
    private final int color;
    public PacketDrawingAction(BlockPos panelPos, ClientAction action, int x, int y, int color, int length){
        this.panelPos = panelPos;
        this.x = (byte) x;
        this.y = (byte) y;
        this.color = color;
        this.action = (byte)action.getMeta();
        this.length = (byte)length;
    }

    public PacketDrawingAction(PacketBuffer buf){
        panelPos = buf.readBlockPos();
        color = buf.readInt();
        byte[] bytes = buf.readByteArray();
        action = bytes[0];
        x = bytes[1];
        y = bytes[2];
        length = bytes[3];
    }

    public void toBytes(PacketBuffer buf){
        buf.writeBlockPos(panelPos);
        buf.writeInt(color);
        byte[] byte_array = new byte[4];
        byte_array[0] = action;
        byte_array[1] = x;
        byte_array[2] = y;
        byte_array[3] = length;
        buf.writeByteArray(byte_array);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> {
            TileEntity te = Objects.requireNonNull(ctx.get().getSender()).getLevel().getBlockEntity(panelPos);
            if (te instanceof DrawingSignTileEntity){
                DrawingSignTileEntity dste = (DrawingSignTileEntity)te;
                dste.makeOperationFromScreen(ClientAction.getAction(action),x,y,color,length);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
