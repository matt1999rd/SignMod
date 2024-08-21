package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Objects;
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

    public PacketSetBoolean(FriendlyByteBuf buf){
        this.panelPos = buf.readBlockPos();
        this.booleanInd = buf.readInt();
        this.newValue = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(panelPos);
        buf.writeInt(booleanInd);
        buf.writeBoolean(this.newValue);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            BlockEntity tileEntity = Objects.requireNonNull(ctx.get().getSender()).getLevel().getBlockEntity(panelPos);
            if (tileEntity instanceof DirectionSignTileEntity dste){
                dste.updateBoolean(booleanInd,newValue);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
