package fr.matt1999rd.signs.tileentity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class DirectionSignModel extends Model {
    //private final ModelPart panel = new ModelPart(this,0,0)*/;
    private ModelPart panelPart;
    private ModelPart arrow1Part /*= new ModelPart(this,0,0)*/;
    private ModelPart arrow3Part /*= new ModelPart(this,0,0)*/;
    private ModelPart arrow5Part /*= new ModelPart(this,0,0)*/;
    private final boolean addArrow;

    public DirectionSignModel(Form form) {
        super(RenderType::entityCutoutNoCull);
        addArrow = (form == Form.ARROW);
        initModel();
    }

    private void initModel(){
        CubeListBuilder panel = CubeListBuilder.create();
        panel.addBox(-7,13,-2,14,2,1);
        panelPart = Functions.bake(panel);
        //panel.addBox(-7,13,-2,14,2,1);
        arrow1Part = getArrowPart(1);
        arrow3Part = getArrowPart(3);
        arrow5Part = getArrowPart(5);
    }

    private ModelPart getArrowPart(int i){
        ModelPart arrowUPart,arrowDPart,arrowCPart;
        //up
        //ModelPart arrowU = new ModelPart(this,0,0);
        CubeListBuilder arrowU = CubeListBuilder.create();
        //down
        //ModelPart arrowD = new ModelPart(this,0,0);
        CubeListBuilder arrowD = CubeListBuilder.create();
        //center
        //ModelPart arrowC = new ModelPart(this,0,0);
        CubeListBuilder arrowC = CubeListBuilder.create();
        //offset of root 2 (Pythagorean theorem apply an isosceles right triangle of length i -> hypotenuse of root 2 times i)
        //to match with the other square we translate of only the oR minus i
        float oR = Mth.sqrt(2)-1;
        arrowU.addBox(7     ,15-i  ,-2,i,i,1);
        arrowUPart = Functions.bake(arrowU);
        setRotationPoint(7,15,arrowUPart,-Math.PI/4);
        arrowD.addBox(7     ,15-2*i,-2,i,i,1);
        arrowDPart = Functions.bake(arrowD);
        setRotationPoint(7,15-2*i,arrowDPart,Math.PI/4);
        arrowC.addBox(7+oR*i,15-i,  -2,i,i,1);
        arrowCPart = Functions.bake(arrowC);
        setRotationPoint(7,15,arrowCPart,-Math.PI/4);

        arrowUPart.zRot = (float) (-Math.PI/4);
        arrowCPart.zRot = (float) (-Math.PI/4);
        arrowDPart.zRot = (float) (+Math.PI/4);
        //ModelPart arrow;
        return Functions.bake(CubeListBuilder.create(),Pair.of("arrowU",arrowUPart),Pair.of("arrowC",arrowCPart), Pair.of("arrowD",arrowDPart));
    }

    private void setRotationPoint(float x,float y,ModelPart model,double angle){
        //angleXY is the angle between line ur of initial point and y-axis.
        double angleXY = Math.atan(x/y);
        //distance between the point and the origin
        float D = Functions.distance(x,y);
        //the x coordinate after rotating
        float newX = D*Mth.sin((float) (angleXY-angle));
        //the y coordinate after rotating
        float newY = D*Mth.cos((float) (angleXY-angle));
        //what we need to do to get back to the initial position is to translate back
        model.setPos(x-newX,y-newY,0);
    }


    public void renderSign(PoseStack stack,DirectionSignTileEntity dste,VertexConsumer builder,int combinedLight,int combinedOverlay){
        if (addArrow){
            renderArrow(stack,dste,builder,combinedLight,combinedOverlay);
        }
        for (float i=1;i<3.5;i=i+0.5F){
            if (i == 1.5) {
                if (dste.is12connected() &&  dste.hasPanel(1)&& dste.hasPanel(2))panelPart.render(stack,builder,combinedLight,combinedOverlay);
            } else if (i == 2.5){
                if (dste.is23connected() && dste.hasPanel(2) && dste.hasPanel(3))panelPart.render(stack,builder,combinedLight,combinedOverlay);
            } else if (dste.hasPanel((int) i)){
                panelPart.render(stack,builder,combinedLight,combinedOverlay);
            }
            stack.translate(0,-2/16F,0);
        }
        stack.translate(0,10/16F,0);
    }

    private void renderArrow(PoseStack stack,DirectionSignTileEntity dste,VertexConsumer builder,int combinedLight,int combinedOverlay){
        //flag is a flag that indicate whether each possible panel are to be rendered
        int flag = dste.getLFlag();
        boolean flagRight;
        if ((flag&1) == 1){
            flagRight = dste.isRightArrow(0);
            moveStack(stack,14,flagRight);
            arrow1Part.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,14,flagRight);
        }
        if ((flag&2) == 2){
            flagRight = dste.isRightArrow(2);
            stack.translate(0,-4/16F,0);
            moveStack(stack,14,flagRight);
            arrow1Part.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,14,flagRight);
            stack.translate(0,+4/16F,0);
        }
        if ((flag&4) == 4){
            flagRight= dste.isRightArrow(4);
            stack.translate(0,-8/16F,0);
            moveStack(stack,14,flagRight);
            arrow1Part.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,14,flagRight);
            stack.translate(0,+8/16F,0);
        }
        if ((flag&8) == 8){
            flagRight = dste.isRightArrow(0);
            moveStack(stack,12,flagRight);
            arrow3Part.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,12,flagRight);
        }
        if ((flag&16) == 16){
            flagRight = dste.isRightArrow(2);
            stack.translate(0,-4/16F,0);
            moveStack(stack,12,flagRight);
            arrow3Part.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,12,flagRight);
            stack.translate(0,+4/16F,0);
        }
        if ((flag&32) == 32){
            flagRight = dste.isRightArrow(0);
            moveStack(stack,10,flagRight);
            arrow5Part.render(stack,builder,combinedLight,combinedOverlay);
            moveStack(stack,10,flagRight);
        }

    }

    private void moveStack(PoseStack stack, int y, boolean isRightArrow){
        if (isRightArrow){
            stack.translate(0,+y/16F,0);
            Functions.rotate(stack, Direction.Axis.Z,180);
            stack.translate(0,-y/16F,0);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderToBuffer(PoseStack stack, VertexConsumer builder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        panelPart.render(stack, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
        arrow1Part.render(stack, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
        arrow3Part.render(stack, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
        arrow5Part.render(stack, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }
}
