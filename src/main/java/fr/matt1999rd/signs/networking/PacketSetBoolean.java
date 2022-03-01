package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetBoolean {
    private final BlockPos panelPos;
    private final int booleanInd;
    private final boolean newValue;
    public PacketSetBoolean(BlockPos pos, int ind,boolean newValue){
        this.panelPos = pos;
        this.booleanInd =ind;
        this.newValue = newValue;
    }

    public PacketSetBoolean(PacketBuffer buf){
        this.panelPos = buf.readBlockPos();
        this.booleanInd = buf.readInt();
        this.newValue = buf.readBoolean();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeBlockPos(panelPos);
        buf.writeInt(booleanInd);
        buf.writeBoolean(this.newValue);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            TileEntity tileEntity = ctx.get().getSender().getLevel().getBlockEntity(panelPos);
            if (tileEntity instanceof DirectionSignTileEntity){
                DirectionSignTileEntity dste = (DirectionSignTileEntity)tileEntity;
                dste.updateBoolean(booleanInd,newValue);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
