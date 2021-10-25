package fr.mattmouss.signs.tileentity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.tileentity.DirectionSignTileEntity;
import fr.mattmouss.signs.util.Functions;
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
        setRotationPoint(7,15,arrowU,-Math.PI/4);
        arrowD.addBox(7     ,15-2*i,-2,i,i,1);
        setRotationPoint(7,15-2*i,arrowD,Math.PI/4);
        arrowC.addBox(7+oR*i,15-i,  -2,i,i,1);
        setRotationPoint(7,15,arrowC,-Math.PI/4);

        arrowU.rotateAngleZ = (float) (-Math.PI/4);
        arrowC.rotateAngleZ = (float) (-Math.PI/4);
        arrowD.rotateAngleZ = (float) (+Math.PI/4);
        RendererModel arrow;
        if (i == 1) {
            arrow = arrow1;
        }else if (i == 3){
            arrow = arrow3;
        }else {
            arrow = arrow5;
        }
        arrow.addChild(arrowU);
        arrow.addChild(arrowC);
        arrow.addChild(arrowD);
    }

    private void setRotationPoint(float x,float y,RendererModel model,double angle){
        //angleXY is the angle between line ur of initial point and y axis.
        double angleXY = Math.atan(x/y);
        //distance between the point and the origin
        float D = Functions.distance(x,y);
        //the x coordinate after rotating
        float newX = D*MathHelper.sin((float) (angleXY-angle));
        //the y coordinate after rotating
        float newY = D*MathHelper.cos((float) (angleXY-angle));
        //what we need to do to get back to the initial position is to translate back
        model.setRotationPoint(x-newX,y-newY,0);
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
        //flag is a flag that indicate whether each possible panel are to be rendered
        int flag = dste.getLFlag();
        boolean flagRight;
        if ((flag&1) == 1){
            flagRight = dste.isRightArrow(0);
            setupGL(14,flagRight);
            arrow1.render(0.0625F);
            setupGL(14,flagRight);
        }
        if ((flag&2) == 2){
            flagRight = dste.isRightArrow(2);
            GlStateManager.translatef(0,-4/16F,0);
            setupGL(14,flagRight);
            arrow1.render(0.0625F);
            setupGL(14,flagRight);
            GlStateManager.translatef(0,+4/16F,0);
        }
        if ((flag&4) == 4){
            flagRight= dste.isRightArrow(4);
            GlStateManager.translatef(0,-8/16F,0);
            setupGL(14,flagRight);
            arrow1.render(0.0625F);
            setupGL(14,flagRight);
            GlStateManager.translatef(0,+8/16F,0);
        }
        if ((flag&8) == 8){
            flagRight = dste.isRightArrow(0);
            setupGL(12,flagRight);
            arrow3.render(0.0625F);
            setupGL(12,flagRight);
        }
        if ((flag&16) == 16){
            flagRight = dste.isRightArrow(2);
            GlStateManager.translatef(0,-4/16F,0);
            setupGL(12,flagRight);
            arrow3.render(0.0625F);
            setupGL(12,flagRight);
            GlStateManager.translatef(0,+4/16F,0);
        }
        if ((flag&32) == 32){
            flagRight = dste.isRightArrow(0);
            setupGL(10,flagRight);
            arrow5.render(0.0625F);
            setupGL(10,flagRight);
        }

    }

    private void setupGL(int y,boolean isRightArrow){
        if (isRightArrow){
            GlStateManager.translatef(0,+y/16F,0);
            GlStateManager.rotatef(180,0,0,1);
            GlStateManager.translatef(0,-y/16F,0);
        }
    }
}
