package fr.mattmouss.signs.tileentity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.enums.Form;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpecialSignModel extends Model {
    private final RendererModel panel = new RendererModel(this,0,0);
    private final Form form;

    public SpecialSignModel(Form form) {
        this.form = form;
        for (int i=0;i<128;i++){
            int j_beg =0,j_end=127;
            boolean isIn = false;
            boolean hasChanged = false;
            for (int j=0;j<128;j++){
                //if we arrived on the form space
                if (form.isIn(i,j) && !isIn){
                    j_beg = j;
                    hasChanged = true;
                }
                //if we quit the form space
                if (!form.isIn(i,j) && isIn){
                    j_end = j;
                    hasChanged = true;
                }
                isIn = form.isIn(i,j);
            }
            if (hasChanged){
                this.addPixelRow(i,j_beg,j_end);
            }
        }
    }

    private void addPixelRow(int i, int j_beg, int j_end){
        float completeLength = form.getCompleteLength();
        float pixelLength = completeLength/128;
        panel.addBox(-5/pixelLength+i,5/pixelLength+10/pixelLength*(1-j_end/128.0F),-2,1,(j_end-j_beg+1),1);
    }

    public void renderSign(){
        float completeLength = form.getCompleteLength();
        float pixelLength = completeLength/128;
        GlStateManager.scalef(pixelLength,pixelLength,1.0F);
        //GlStateManager.translatef(-3.55F,0.0F,0.0F);
        panel.render(0.0625F);
        GlStateManager.scalef(1/pixelLength,1/pixelLength,1.0F);
    }

    /*
    public RendererModel getGrid() {
        return grid_background;
    }

    public RendererModel getSupport() {
        return support_background;
    }

     */
}
