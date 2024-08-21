package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.tileentity.primary.RectangleSignTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PacketCenterText {

    private final BlockPos panelPos;
    private final boolean isTextCenter;

    public PacketCenterText(BlockPos panelPos, boolean isTextCenter){
        this.panelPos = panelPos;
        this.isTextCenter = isTextCenter;
    }

    public PacketCenterText(FriendlyByteBuf buf){
        panelPos = buf.readBlockPos();
        isTextCenter = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(panelPos);
        buf.writeBoolean(isTextCenter);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> {
            BlockEntity te = Objects.requireNonNull(ctx.get().getSender()).getLevel().getBlockEntity(panelPos);
            if (te instanceof RectangleSignTileEntity dste){
                dste.setCenterText(isTextCenter);
                dste.centerText(isTextCenter);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
