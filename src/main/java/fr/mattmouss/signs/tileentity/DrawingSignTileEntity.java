package fr.mattmouss.signs.tileentity;

import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.capabilities.SignStorage;
import fr.mattmouss.signs.enums.ClientAction;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Text;
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

    public int getNumberOfText(){
        return storage.map(signStorage -> signStorage.getTexts().length).orElse(0);
    }

    public Text getText(int n){
        return storage.map(signStorage -> {
            Text[] texts = signStorage.getTexts();
            int lim = texts.length;
            if (n>lim || n<0)return null;
            return texts[n];
        }).orElse(null);
    }

    public void addOrEditText(Text newText,int ind){
        if (ind == -1){
            storage.ifPresent(signStorage -> signStorage.addText(newText));
        } else {
            storage.ifPresent(signStorage -> signStorage.setText(newText,ind));
        }
    }

    public void delText(int ind){
        storage.ifPresent(signStorage -> signStorage.delText(ind));
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



    public void makeOperationFromScreen(ClientAction action,int x,int y,int color,int length){
        SignMod.LOGGER.info("doing operation in te with parameter : action : "+action+" x : "+x+" y : "+y+" color : "+color+" length : "+length);
        switch (action){
            case SET_BG:
                storage.ifPresent(signStorage -> signStorage.setBackGround(color));
                break;
            case SET_PIXEL:
                storage.ifPresent(signStorage -> {
                    signStorage.setPixel(x,y,color,length);
                });
                break;
            case ERASE_PIXEL:
                storage.ifPresent(signStorage -> {
                    signStorage.setPixel(x,y,0,length);
                });
                break;
            case MOVE_TEXT:
                break;
            case FILL_PIXEL:
                storage.ifPresent(signStorage -> {
                    signStorage.fill(x, y, color);
                });
                break;
        }

    }


    public void setText(Text t, int ind){
        storage.ifPresent(signStorage -> signStorage.setText(t,ind));
    }
}
