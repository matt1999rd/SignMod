package fr.mattmouss.signs.tileentity;

import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.capabilities.SignStorage;
import fr.mattmouss.signs.enums.ClientAction;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

public abstract class DrawingSignTileEntity extends PanelTileEntity {

    private LazyOptional<SignStorage> storage = LazyOptional.of(this::getStorage).cast();

    private SignStorage getStorage() {
        return new SignStorage();
    }

    public DrawingSignTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    protected abstract Form getForm();

    @OnlyIn(Dist.CLIENT)
    public int getPixelColor(int i,int j){
        return storage.map(signStorage -> {
            return signStorage.getRGBPixel(i,j);
        }).orElse(0);
    }

    @Override
    public void tick() {
        BlockState state = getBlockState();
        if (!state.get(AbstractPanelBlock.GRID)){
            //if it is a support with grid
            super.tick();
        }
    }

    @Override
    public void renderOnScreen(int guiLeft,int guiTop) {
        Form form = getForm();
        for (int i=0;i<128;i++){
            for (int j=0;j<128;j++){
                if (form.isIn(i,j)){
                    int color = getPixelColor(i,j);
                    AbstractGui.fill(guiLeft+i,guiTop+j,guiLeft+i+1,guiTop+j+1,color);
                }
            }
        }
    }

    public int getScale(){
        int i = getBlockState().get(Functions.SCALE);
        return Functions.Pow2(i+4);
    }

    public void makeOperationFromScreen(ClientAction action,int x,int y,int color,int length){
        SignMod.LOGGER.info("doing operation in te with parameter : action : "+action+" x : "+x+" y : "+y+" color : "+color+" length : "+length);
        switch (action){
            case SET_BG:
                storage.ifPresent(signStorage -> signStorage.setBackGround(color));
                break;
            case SET_PIXEL:
                storage.ifPresent(signStorage -> {
                    int pixelLength = 128/getScale();
                    int x_large_screen = x/pixelLength;
                    int y_large_screen = y/pixelLength;
                    signStorage.setPixel(x_large_screen,y_large_screen,color,length,pixelLength);
                });
                break;
            case ERASE_PIXEL:
                storage.ifPresent(signStorage -> {
                    int pixelLength = 128/getScale();
                    int x_large_screen = x/pixelLength;
                    int y_large_screen = y/pixelLength;
                    signStorage.setPixel(x_large_screen,y_large_screen,0,length,pixelLength);
                });
                break;
            case MOVE_TEXT:
                break;
            case FILL_PIXEL:
                storage.ifPresent(signStorage -> {
                    int pixelLength = 128/getScale();
                    int scale= getScale();
                    int x_large_screen = x/pixelLength;
                    int y_large_screen = y/pixelLength;
                    signStorage.fill(x_large_screen, y_large_screen, color, scale);
                });
                break;
        }

    }


}
