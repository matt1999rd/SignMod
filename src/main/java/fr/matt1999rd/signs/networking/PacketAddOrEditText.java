package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.util.Text;
import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import fr.matt1999rd.signs.tileentity.EditingSignTileEntity;
import fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PacketAddOrEditText {
    private final BlockPos panelPos;
    private final Text t;
    private final int ind; // if text is only modified
    public PacketAddOrEditText(BlockPos panelPos, Text text, int ind){
        this.panelPos = panelPos;
        this.t = text;
        this.ind = ind;
    }

    public PacketAddOrEditText(FriendlyByteBuf buf){
        panelPos = buf.readBlockPos();
        t = Text.readText(buf);
        ind = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeBlockPos(panelPos);
        t.writeText(buf);
        buf.writeInt(ind);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> {
            BlockEntity te = Objects.requireNonNull(ctx.get().getSender()).getLevel().getBlockEntity(panelPos);
            if (te instanceof DrawingSignTileEntity dste){
                dste.addOrEditText(t,ind);
            }else if (te instanceof EditingSignTileEntity este){
                este.setText(t);
            }else if (te instanceof DirectionSignTileEntity dste) {
                dste.setText(ind / 2, ind % 2 == 1, t);
            }else if (te instanceof PlainSquareSignTileEntity psste){
                psste.setText(t,ind);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
