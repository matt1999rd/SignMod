package fr.matt1999rd.signs.tileentity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.util.PictureRenderState;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class SpecialSignModel extends Model {
    //private final ModelPart panel = new ModelPart(this,0,0);
    private ModelPart panel;
    private final Form form;

    public SpecialSignModel(Form form) {
        super(RenderType::entityCutoutNoCull);
        this.form = form;
        initModel();
    }

    private void initModel(){
        CubeListBuilder builder = CubeListBuilder.create();
        /*
        if (form == Form.OCTAGON)panel.setTexSize(132,129);
        else if (form == Form.UPSIDE_TRIANGLE)panel.setTexSize(193,129);
        todo : need to be moved to the rendering function
         */
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
                    j_end = j-1;
                    hasChanged = true;
                }
                isIn = form.isIn(i,j);
            }
            if (hasChanged){
                this.addPixelRow(builder,i,j_beg,j_end);
            }
        }
        panel = Functions.bake(builder);
    }


    private void addPixelRow(CubeListBuilder builder,int i, int j_beg, int j_end){
        float completeLength = form.getCompleteLength();
        if (form == Form.OCTAGON) {
            if (i == 0 || i == 1|| i == 127 || i==126) builder.texOffs(4, 0);
            else if (i<42) builder.texOffs(131-3*i, 0);
            else if (i>85) builder.texOffs(3*i-250,0);
            else builder.texOffs(0,0);
        }else if (form == Form.UPSIDE_TRIANGLE){
            if (i>63)builder.texOffs(3*(127-i),0);
            else builder.texOffs(3*i,0);
        }
        float pixelLength = completeLength/128;
        builder.addBox(-5/pixelLength+10/pixelLength*(1-(i+1)/128.0F),5/pixelLength+10/pixelLength*(1-(j_end+1)/128.0F),-2,1,(j_end-j_beg+1),1);
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer iVertexBuilder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        panel.render(stack, iVertexBuilder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }

    public void renderSign(PoseStack stack,VertexConsumer builder,int combinedLight,int combinedOverlay){
        float completeLength = form.getCompleteLength();
        float pixelLength = completeLength/128;
        stack.scale(pixelLength,pixelLength,1.0F);
        panel.render(stack,builder,combinedLight,combinedOverlay);
        stack.scale(1/pixelLength,1/pixelLength,1.0F);
    }
}
