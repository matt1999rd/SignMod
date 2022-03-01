package fr.matt1999rd.signs.networking;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.enums.ScreenType;
import fr.matt1999rd.signs.gui.DirectionScreen;
import fr.matt1999rd.signs.gui.DrawingScreen;
import fr.matt1999rd.signs.gui.EditingScreen;
import fr.matt1999rd.signs.gui.PlainSquareScreen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenScreen {
    private final BlockPos panelPos;
    private final byte form;
    private final int screenType;

    public PacketOpenScreen(PacketBuffer buf){
        panelPos = buf.readBlockPos();
        form = buf.readByte();
        screenType = buf.readInt();
    }

    public void toBytes(PacketBuffer buf){
        buf.writeBlockPos(panelPos);
        buf.writeByte(form);
        buf.writeInt(screenType);
    }

    public PacketOpenScreen(BlockPos pos, Form form, ScreenType type){
        panelPos = pos;
        this.form = (byte) form.getMeta();
        this.screenType = type.getMeta();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ScreenType type = ScreenType.getType(screenType);
            switch (type){
                case PLAIN_SQUARE_SCREEN:
                    PlainSquareScreen.open(panelPos);
                    break;
                case EDITING_SCREEN:
                    EditingScreen.open(Form.byIndex(form),panelPos);
                    break;
                case DRAWING_SCREEN:
                    DrawingScreen.open(Form.byIndex(form),panelPos);
                    break;
                case DIRECTION_SCREEN:
                    DirectionScreen.open(Form.byIndex(form),panelPos);
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
