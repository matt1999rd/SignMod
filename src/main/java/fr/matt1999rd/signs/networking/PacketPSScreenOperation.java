package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPSScreenOperation {
    private final BlockPos panelPos;
    private final int colorModeOrArrowDir;
    private final int operationId;

     public PacketPSScreenOperation(PacketBuffer buffer){
         panelPos = buffer.readBlockPos();
         colorModeOrArrowDir = buffer.readInt();
         operationId = buffer.readByte();
     }

     public void toBytes(PacketBuffer buffer){
         buffer.writeBlockPos(panelPos);
         buffer.writeInt(colorModeOrArrowDir);
         buffer.writeByte(operationId);
     }

     public PacketPSScreenOperation(BlockPos panelPos,int operationId,int colorModeOrArrowDir){
         this.panelPos = panelPos;
         this.colorModeOrArrowDir = colorModeOrArrowDir;
         this.operationId = operationId;
     }

     public void handle(Supplier<NetworkEvent.Context> ctx){
         ctx.get().enqueueWork(()->{
             TileEntity te = ctx.get().getSender().getLevel().getBlockEntity(panelPos);
             if (te instanceof PlainSquareSignTileEntity){
                 PlainSquareSignTileEntity psste = (PlainSquareSignTileEntity) te;
                 psste.doOperation(operationId,colorModeOrArrowDir);
             }else {
                 SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
             }
         });
         ctx.get().setPacketHandled(true);
     }

}
