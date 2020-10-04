package fr.mattmouss.signs.networking;

import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.tileentity.primary.ArrowSignTileEntity;
import fr.mattmouss.signs.tileentity.primary.RectangleSignTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketCenterText {

    private final BlockPos panelPos;
    private final boolean isTextCenter;

    public PacketCenterText(BlockPos panelPos, boolean isTextCenter){
        this.panelPos = panelPos;
        this.isTextCenter = isTextCenter;
    }

    public PacketCenterText(PacketBuffer buf){
        panelPos = buf.readBlockPos();
        isTextCenter = buf.readBoolean();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeBlockPos(panelPos);
        buf.writeBoolean(isTextCenter);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> {
            TileEntity te = ctx.get().getSender().getServerWorld().getTileEntity(panelPos);
            if (te instanceof RectangleSignTileEntity){
                RectangleSignTileEntity dste = (RectangleSignTileEntity) te;
                dste.setCenterText(isTextCenter);
                dste.centerText(isTextCenter);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
