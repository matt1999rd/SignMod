package fr.mattmouss.signs.networking;

import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.gui.PSDisplayModeScreen;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PacketPlacePanel {
    private final BlockPos panelFuturePos;
    private final int form;
    private final byte facing;
    private final boolean rotated;

    public PacketPlacePanel(PacketBuffer buf){
        panelFuturePos = buf.readBlockPos();
        byte[] array = buf.readByteArray();
        form = array[0];
        facing = array[1];
        rotated = buf.readBoolean();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeBlockPos(panelFuturePos);
        byte[] array = new byte[2];
        array[0] = (byte)form;
        array[1] = facing;
        buf.writeByteArray(array);
        buf.writeBoolean(rotated);
    }

    public PacketPlacePanel(BlockPos pos, int form, Direction facing,boolean rotated){
        panelFuturePos = pos;
        this.form = form;
        this.facing = (byte) facing.getHorizontalIndex();
        this.rotated = rotated;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if (form == Form.PLAIN_SQUARE.getMeta()){
                byte authoring = Functions.getAuthoring(ctx.get().getSender().getServerWorld(),panelFuturePos,Direction.byHorizontalIndex(facing));
                PSDisplayModeScreen.open(panelFuturePos,Direction.byHorizontalIndex(facing),rotated,authoring);
            }else {
                ServerWorld world = Objects.requireNonNull(ctx.get().getSender()).getServerWorld();
                BlockState supportState = world.getBlockState(panelFuturePos);
                BlockState panelState = AbstractPanelBlock.getBlockStateFromSupport(form, supportState, facing, rotated);
                world.setBlockState(panelFuturePos, panelState, 11);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
