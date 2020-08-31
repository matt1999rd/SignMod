package fr.mattmouss.signs.tileentity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.tileentity.DirectionSignTileEntity;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DirectionSignModel extends Model {
    private final RendererModel panel = new RendererModel(this,0,0);
    private final RendererModel arrow1 = new RendererModel(this,0,0);
    private final RendererModel arrow2 = new RendererModel(this,0,0);
    private final RendererModel arrow3 = new RendererModel(this,0,0);
    private final RendererModel arrow4 = new RendererModel(this,0,0);
    private final RendererModel arrow5 = new RendererModel(this,0,0);
    private final boolean addArrow;

    public DirectionSignModel(Form form) {
        addArrow = (form == Form.ARROW);
        initModel();
    }

    private void initModel(){
        panel.addBox(-7,13,-2,14,2,1);
        RendererModel arrowUp = new RendererModel(this,0,0);
        RendererModel arrowDown = new RendererModel(this,0,0);
        arrow1.addChild(arrowUp);
        arrow1.addChild(arrowDown);
        arrow2.addChild(arrowUp);
        arrow2.addChild(arrowDown);
        arrow3.addChild(arrowUp);
        arrow3.addChild(arrowDown);
        arrow4.addChild(arrowUp);
        arrow4.addChild(arrowDown);
        arrow5.addChild(arrowUp);
        arrow5.addChild(arrowDown);
    }

    public void renderSign(DirectionSignTileEntity dste){
        if (addArrow){
            renderArrow(dste);
        }
        for (int i=1;i<3.5;i+=0.5){
            if (i == 1.5 && dste.is12connected()) {
                panel.render(0.0625F);
            } else if (i == 2.5 && dste.is23connected()){
                panel.render(0.0625F);
            } else if (dste.hasPanel(i)){
                panel.render(0.0625F);
            }
            GlStateManager.translatef(0,2/16F,0);
        }
        GlStateManager.translatef(0,-10/16F,0);
    }

    private void renderArrow(DirectionSignTileEntity dste){ /* todo : add here rendering of arrow */   }
}
