package fr.matt1999rd.signs.enums;

import fr.matt1999rd.signs.capabilities.SignCapability;
import fr.matt1999rd.signs.gui.screenutils.PencilMode;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;

import java.nio.IntBuffer;

public enum ClientAction {
    SET_PIXEL(0, "set pixel") {
        @Override
        public void doActionOnTileEntity(DrawingSignTileEntity tileEntity, IntBuffer buf) {
            int x = buf.get();
            int y = buf.get();
            int color = buf.get();
            int length = buf.get();
            tileEntity.getCapability(SignCapability.SIGN_STORAGE).ifPresent(signStorage -> signStorage.setPixel(x,y,color,length));
        }
    },
    FILL_PIXEL(1,"fill pixel") {
        @Override
        public void doActionOnTileEntity(DrawingSignTileEntity tileEntity, IntBuffer buf) {
            int x = buf.get();
            int y = buf.get();
            int color = buf.get();
            tileEntity.getCapability(SignCapability.SIGN_STORAGE).ifPresent(signStorage -> signStorage.fill(x, y, color));
        }
    },
    ERASE_PIXEL(2,"erase pixel") {
        @Override
        public void doActionOnTileEntity(DrawingSignTileEntity tileEntity, IntBuffer buf) {
            int x = buf.get();
            int y = buf.get();
            int length = buf.get();
            tileEntity.getCapability(SignCapability.SIGN_STORAGE).ifPresent(signStorage -> signStorage.setPixel(x,y,0,length));
        }
    },
    SET_BG(3,"set background") {
        @Override
        public void doActionOnTileEntity(DrawingSignTileEntity tileEntity, IntBuffer buf) {
            int color = buf.get();
            tileEntity.getCapability(SignCapability.SIGN_STORAGE).ifPresent(signStorage -> signStorage.setBackGround(color));
        }

    },
    MOVE_TEXT(4,"move text") {
        @Override
        public void doActionOnTileEntity(DrawingSignTileEntity tileEntity, IntBuffer buf) {
            int x = buf.get();
            int y = buf.get();
            int textIndices = buf.get();
            if (textIndices != -1) {
                tileEntity.getCapability(SignCapability.SIGN_STORAGE).ifPresent(signStorage -> signStorage.setTextPosition(textIndices,x,y,tileEntity.getForm()));
            }
        }


    },
    MAKE_LINE(5,"make line") {
        @Override
        public void doActionOnTileEntity(DrawingSignTileEntity tileEntity, IntBuffer buf) {
            int x1 = buf.get();
            int y1 = buf.get();
            int x2 = buf.get();
            int y2 = buf.get();
            int length = buf.get();
            int color = buf.get();
            tileEntity.getCapability(SignCapability.SIGN_STORAGE).ifPresent(iSignStorage -> iSignStorage.makeLine(x1,y1,x2,y2,length,color));
        }

    };

    final int meta;
    final String name;
    ClientAction(int meta,String name){
        this.meta = meta;
        this.name = name;
    }

    public int getMeta() {
        return meta;
    }

    public abstract void doActionOnTileEntity(DrawingSignTileEntity tileEntity, IntBuffer buf);




    public static ClientAction getAction(int meta){
        if (meta<0 || meta>ClientAction.values().length){
            return null;
        }else {
            return ClientAction.values()[meta];
        }
    }

    public static ClientAction getActionFromMode(PencilMode mode){
        return switch (mode) {
            case WRITE -> SET_PIXEL;
            case ERASE -> ERASE_PIXEL;
            case FILL -> FILL_PIXEL;
            case SELECT -> MOVE_TEXT;
            default -> null;
        };
    }

    //ALL STATIC FUNCTION TO MAKE BUFFER BECAUSE INNER FUNCTION STUPIDLY DOESN'T WORK
    //SET PIXEL
    public static IntBuffer makeBufferForSETPIXEL(int x,int y,int color,int length){
        IntBuffer buf = IntBuffer.allocate(4);
        buf.put(x);
        buf.put(y);
        buf.put(color);
        buf.put(length);
        return buf.flip();
    }

    //FILL PIXEL
    public static IntBuffer makeBufferForFILLPIXEL(int x, int y, int color){
        IntBuffer buf = IntBuffer.allocate(3);
        buf.put(x);
        buf.put(y);
        buf.put(color);
        return buf.flip();
    }

    //ERASE_PIXEL
    public static IntBuffer makeBufferForERASEPIXEL(int x,int y,int length){
        IntBuffer buf = IntBuffer.allocate(3);
        buf.put(x);
        buf.put(y);
        buf.put(length);
        return buf.flip();
    }

    //SET_BG
    public static IntBuffer makeBufferForSETBG(int color){
        IntBuffer buf = IntBuffer.allocate(1);
        buf.put(color);
        return buf.flip();
    }

    //MOVE_TEXT
    public static IntBuffer makeBufferForMOVETEXT(int x,int y,int textIndices){
        IntBuffer buf = IntBuffer.allocate(3);
        buf.put(x);
        buf.put(y);
        buf.put(textIndices);
        return buf.flip();
    }

    //MAKE_LINE
    public static IntBuffer makeBufferForMAKELINE(int x1,int y1,int x2,int y2,int length,int color){
        IntBuffer buf = IntBuffer.allocate(6);
        buf.put(x1);
        buf.put(y1);
        buf.put(x2);
        buf.put(y2);
        buf.put(length);
        buf.put(color);
        return buf.flip();
    }
}
