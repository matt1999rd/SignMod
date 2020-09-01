package fr.mattmouss.signs.tileentity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.tileentity.DirectionSignTileEntity;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DirectionSignModel extends Model {
    private final RendererModel panel = new RendererModel(this,0,0);
    private final RendererModel arrow1 = new RendererModel(this,0,0);
    private final RendererModel arrow3 = new RendererModel(this,0,0);
    private final RendererModel arrow5 = new RendererModel(this,0,0);
    private final boolean addArrow;

    public DirectionSignModel(Form form) {
        addArrow = (form == Form.ARROW);
        initModel();
    }

    private void initModel(){
        panel.addBox(-7,13,-2,14,2,1);
        initArrow(1);
        initArrow(3);
        initArrow(5);
    }

    private void initArrow(int i){
        //up
        RendererModel arrowU = new RendererModel(this,0,0);
        //down
        RendererModel arrowD = new RendererModel(this,0,0);
        //center
        RendererModel arrowC = new RendererModel(this,0,0);
        //offset of root 2 (pytagore theoreme apply a isosceles right triangle of length i -> hypothenus of root 2 times i)
        //to match with the other square we translate of only the oR minus i
        float oR = MathHelper.sqrt(2)-1;
        arrowU.addBox(7     ,15-i  ,-2,i,i,1);
        arrowD.addBox(7     ,15-2*i,-2,i,i,1);
        arrowC.addBox(7+oR*i,15-i,  -2,i,i,1);
        arrowU.setRotationPoint(15,15    ,7);
        arrowD.setRotationPoint(15,15-2*i,7);
        arrowC.setRotationPoint(15,15    ,7);
        arrowU.rotateAngleZ = -45;
        arrowC.rotateAngleZ = -45;
        arrowD.rotateAngleZ = +45;
        arrow1.addChild(arrowU);
        arrow1.addChild(arrowD);
        arrow1.addChild(arrowC);
    }


    public void renderSign(DirectionSignTileEntity dste){
        if (addArrow){
            renderArrow(dste);
        }
        for (float i=1;i<3.5;i=i+0.5F){
            if (i == 1.5) {
                if (dste.is12connected() &&  dste.hasPanel(1)&& dste.hasPanel(2))panel.render(0.0625F);
            } else if (i == 2.5){
                if (dste.is23connected() && dste.hasPanel(2) && dste.hasPanel(3))panel.render(0.0625F);
            } else if (dste.hasPanel((int) i)){
                panel.render(0.0625F);
            }
            GlStateManager.translatef(0,-2/16F,0);
        }
        GlStateManager.translatef(0,10/16F,0);
    }

    private void renderArrow(DirectionSignTileEntity dste){
        if (dste.hasPanel(1)){
            
        }
    }
}
