package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PacketPSScreenOperation {
    private final BlockPos panelPos;
    private final int colorModeOrArrowDir;
    private final int operationId;

     public PacketPSScreenOperation(FriendlyByteBuf buffer){
         panelPos = buffer.readBlockPos();
         colorModeOrArrowDir = buffer.readInt();
         operationId = buffer.readByte();
     }

     public void toBytes(FriendlyByteBuf buffer){
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
             BlockEntity te = Objects.requireNonNull(ctx.get().getSender()).getLevel().getBlockEntity(panelPos);
             if (te instanceof PlainSquareSignTileEntity psste){
                 psste.doOperation(operationId,colorModeOrArrowDir);
             }else {
                 SignMod.LOGGER.warn("unable to send packet to server : invalid position send");
             }
         });
         ctx.get().setPacketHandled(true);
     }

}
