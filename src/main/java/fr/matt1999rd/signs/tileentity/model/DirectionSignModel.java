package fr.matt1999rd.signs.tileentity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import fr.matt1999rd.signs.util.PictureRenderState;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DirectionSignModel extends Model {
    private final ModelRenderer panel = new ModelRenderer(this,0,0);
    private final ModelRenderer arrow1 = new ModelRenderer(this,0,0);
    private final ModelRenderer arrow3 = new ModelRenderer(this,0,0);
    private final ModelRenderer arrow5 = new ModelRenderer(this,0,0);
    private final boolean addArrow;

    public DirectionSignModel(Form form) {
        super(PictureRenderState::getModelRenderType);
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
        ModelRenderer arrowU = new ModelRenderer(this,0,0);
        //down
        ModelRenderer arrowD = new ModelRenderer(this,0,0);
        //center
        ModelRenderer arrowC = new ModelRenderer(this,0,0);
        //offset of root 2 (pytagore theoreme apply a isosceles right triangle of length i -> hypothenus of root 2 times i)
        //to match with the other square we translate of only the oR minus i
        float oR = MathHelper.sqrt(2)-1;
        arrowU.addBox(7     ,15-i  ,-2,i,i,1);
        setRotationPoint(7,15,arrowU,-Math.PI/4);
        arrowD.addBox(7     ,15-2*i,-2,i,i,1);
        setRotationPoint(7,15-2*i,arrowD,Math.PI/4);
        arrowC.addBox(7+oR*i,15-i,  -2,i,i,1);
        setRotationPoint(7,15,arrowC,-Math.PI/4);

        arrowU.zRot = (float) (-Math.PI/4);
        arrowC.zRot = (float) (-Math.PI/4);
        arrowD.zRot = (float) (+Math.PI/4);
        ModelRenderer arrow;
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

    private void setRotationPoint(float x,float y,ModelRenderer model,double angle){
        //angleXY is the angle between line ur of initial point and y axis.
        double angleXY = Math.atan(x/y);
        //distance between the point and the origin
        float D = Functions.distance(x,y);
        //the x coordinate after rotating
        float newX = D*MathHelper.sin((float) (angleXY-angle));
        //the y coordinate after rotating
        float newY = D*MathHelper.cos((float) (angleXY-angle));
        //what we need to do to get back to the initial position is to translate back
        model.setPos(x-newX,y-newY,0);
    }


    public void renderSign(MatrixStack stack,DirectionSignTileEntity dste,IVertexBuilder builder,int combinedLight,int combinedOverlay){
        if (addArrow){
            renderArrow(stack,dste,builder,combinedLight,combinedOverlay);
        }
        for (float i=1;i<3.5;i=i+0.5F){
            if (i == 1.5) {
                if (dste.is12connected() &&  dste.hasPanel(1)&& dste.hasPanel(2))panel.render(stack,builder,combinedLight,combinedOverlay);
            } else if (i == 2.5){
                if (dste.is23connected() && dste.hasPanel(2) && dste.hasPanel(3))panel.render(stack,builder,combinedLight,combinedOverlay);
            } else if (dste.hasPanel((int) i)){
                panel.render(stack,builder,combinedLight,combinedOverlay);
            }
            stack.translate(0,-2/16F,0);
        }
        stack.translate(0,10/16F,0);
    }

    private void renderArrow(MatrixStack stack,DirectionSignTileEntity dste,IVertexBuilder builder,int combinedLight,int combinedOverlay){
        //flag is a flag that indicate whether each possible panel are to be rendered
        int flag = dste.getLFlag();
        boolean flagRight;
        if ((flag&1) == 1){
            flagRight = dste.isRightArrow(0);
            moveStack(stack,14,flagRight);
            arrow1.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,14,flagRight);
        }
        if ((flag&2) == 2){
            flagRight = dste.isRightArrow(2);
            stack.translate(0,-4/16F,0);
            moveStack(stack,14,flagRight);
            arrow1.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,14,flagRight);
            stack.translate(0,+4/16F,0);
        }
        if ((flag&4) == 4){
            flagRight= dste.isRightArrow(4);
            stack.translate(0,-8/16F,0);
            moveStack(stack,14,flagRight);
            arrow1.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,14,flagRight);
            stack.translate(0,+8/16F,0);
        }
        if ((flag&8) == 8){
            flagRight = dste.isRightArrow(0);
            moveStack(stack,12,flagRight);
            arrow3.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,12,flagRight);
        }
        if ((flag&16) == 16){
            flagRight = dste.isRightArrow(2);
            stack.translate(0,-4/16F,0);
            moveStack(stack,12,flagRight);
            arrow3.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,12,flagRight);
            stack.translate(0,+4/16F,0);
        }
        if ((flag&32) == 32){
            flagRight = dste.isRightArrow(0);
            moveStack(stack,10,flagRight);
            arrow5.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,10,flagRight);
        }

    }

    private void moveStack(MatrixStack stack, int y, boolean isRightArrow){
        if (isRightArrow){
            stack.translate(0,+y/16F,0);
            Functions.rotate(stack, Direction.Axis.Z,180);
            stack.translate(0,-y/16F,0);
        }
    }

    @Override
    public void renderToBuffer(MatrixStack stack, IVertexBuilder builder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        panel.render(stack, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
        arrow1.render(stack, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
        arrow3.render(stack, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
        arrow5.render(stack, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }
}
