package fr.mattmouss.signs.networking;

import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.tileentity.DrawingSignTileEntity;
import fr.mattmouss.signs.util.Text;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketDelText {
    private final BlockPos panelPos;
    private final int ind;
    public PacketDelText(BlockPos panelPos, int ind){
        this.panelPos = panelPos;
        this.ind = ind;
    }

    public PacketDelText(PacketBuffer buf){
        panelPos = buf.readBlockPos();
        ind = buf.readInt();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeBlockPos(panelPos);
        buf.writeInt(ind);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> {
            TileEntity te = ctx.get().getSender().getServerWorld().getTileEntity(panelPos);
            if (te instanceof DrawingSignTileEntity){
                DrawingSignTileEntity dste = (DrawingSignTileEntity)te;
                dste.delText(ind);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
