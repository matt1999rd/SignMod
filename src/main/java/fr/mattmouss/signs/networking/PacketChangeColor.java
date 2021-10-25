package fr.mattmouss.signs.networking;

import com.google.common.graph.Network;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.tileentity.DirectionSignTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import org.lwjgl.system.CallbackI;

import java.util.function.Supplier;

public class PacketChangeColor {
    private final BlockPos panelPos;
    private final int color;
    private final boolean isBackGround;
    private final byte ind;
    public PacketChangeColor(BlockPos pos,int color,boolean isBackGround,int ind){
        this.panelPos = pos;
        this.color = color;
        this.isBackGround = isBackGround;
        this.ind =(byte)ind;
    }

    public PacketChangeColor(PacketBuffer buf){
        this.color = buf.readInt();
        this.isBackGround = buf.readBoolean();
        this.panelPos = buf.readBlockPos();
        this.ind = buf.readByte();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeInt(this.color);
        buf.writeBoolean(isBackGround);
        buf.writeBlockPos(panelPos);
        buf.writeByte(ind);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            TileEntity tileEntity = ctx.get().getSender().getServerWorld().getTileEntity(panelPos);
            if (tileEntity instanceof DirectionSignTileEntity){
                DirectionSignTileEntity dste = (DirectionSignTileEntity)tileEntity;
                dste.setColor(ind,isBackGround,color);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
