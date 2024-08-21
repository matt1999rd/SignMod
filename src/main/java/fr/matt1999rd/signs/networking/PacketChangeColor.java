package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Objects;
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

    public PacketChangeColor(FriendlyByteBuf buf){
        this.color = buf.readInt();
        this.isBackGround = buf.readBoolean();
        this.panelPos = buf.readBlockPos();
        this.ind = buf.readByte();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(this.color);
        buf.writeBoolean(isBackGround);
        buf.writeBlockPos(panelPos);
        buf.writeByte(ind);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            BlockEntity tileEntity = Objects.requireNonNull(ctx.get().getSender()).getLevel().getBlockEntity(panelPos);
            if (tileEntity instanceof DirectionSignTileEntity dste){
                dste.setColor(ind,isBackGround,color);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
