package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.tileentity.primary.ArrowSignTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PacketFlipText {
    private final BlockPos panelPos;
    private final int ind;
    public PacketFlipText(BlockPos panelPos, int ind){
        this.panelPos = panelPos;
        this.ind = ind;
    }

    public PacketFlipText(FriendlyByteBuf buf){
        panelPos = buf.readBlockPos();
        ind = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(panelPos);
        buf.writeInt(ind);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> {
            BlockEntity te = Objects.requireNonNull(ctx.get().getSender()).getLevel().getBlockEntity(panelPos);
            if (te instanceof ArrowSignTileEntity dste){
                dste.flipText(ind);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
