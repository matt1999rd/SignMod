package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.ClientAction;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.nio.BufferOverflowException;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.function.Supplier;

public class PacketDrawingAction {
    private final BlockPos panelPos;
    private final byte action;
    IntBuffer buffer;
    public PacketDrawingAction(BlockPos panelPos, ClientAction action, IntBuffer buffer){
        this.panelPos = panelPos;
        this.buffer = buffer;
        this.action = (byte)action.getMeta();
    }

    public PacketDrawingAction(FriendlyByteBuf buf){
        panelPos = buf.readBlockPos();
        int intBufferLength = buf.readInt();
        this.buffer = IntBuffer.allocate(intBufferLength);
        for (int i=0;i<intBufferLength;i++){
            try {
                this.buffer.put(buf.readInt());
            }catch (Exception e){
                throw new IllegalStateException("Error in buffer transmission. IntBuffer length may be incorrect. See the current error : "+e.getMessage());
            }
        }
        this.buffer.flip();
        action = buf.readByte();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(panelPos);
        int intBufferLength = this.buffer.capacity();
        buf.writeInt(intBufferLength);
        for (int i=0;i<intBufferLength;i++){
            buf.writeInt(this.buffer.get());
        }
        buf.writeByte(action);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> {
            BlockEntity te = Objects.requireNonNull(ctx.get().getSender()).getLevel().getBlockEntity(panelPos);
            if (te instanceof DrawingSignTileEntity dste){
                dste.makeOperationFromScreen(ClientAction.getAction(action),this.buffer);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
