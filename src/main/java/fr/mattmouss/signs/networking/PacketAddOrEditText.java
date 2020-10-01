package fr.mattmouss.signs.networking;

import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.tileentity.DirectionSignTileEntity;
import fr.mattmouss.signs.tileentity.DrawingSignTileEntity;
import fr.mattmouss.signs.tileentity.EditingSignTileEntity;
import fr.mattmouss.signs.util.Text;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public PacketAddOrEditText(PacketBuffer buf){
        panelPos = buf.readBlockPos();
        t = Text.readText(buf);
        ind = buf.readInt();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeBlockPos(panelPos);
        t.writeText(buf);
        buf.writeInt(ind);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> {
            TileEntity te = ctx.get().getSender().getServerWorld().getTileEntity(panelPos);
            if (te instanceof DrawingSignTileEntity){
                DrawingSignTileEntity dste = (DrawingSignTileEntity)te;
                dste.addOrEditText(t,ind);
            }else if (te instanceof EditingSignTileEntity){
                EditingSignTileEntity este = (EditingSignTileEntity)te;
                este.setText(t);
            }else if (te instanceof DirectionSignTileEntity) {
                DirectionSignTileEntity dste = (DirectionSignTileEntity)te;
                dste.setText(ind/2,ind%2 == 0,t);
            }else {
                SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
