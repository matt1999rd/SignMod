package fr.matt1999rd.signs.tileentity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.util.PictureRenderState;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public class PSSignModel extends Model {
    //private final ModelPart panel = new ModelPart(this,0,0);
    private ModelPart panel;
    public PSSignModel() {
        super(RenderType::entityCutoutNoCull);
        initModel();
    }

    private void initModel(){
        CubeListBuilder builder = CubeListBuilder.create();
        builder.addBox(-17.0F, -32.0F, -2, 32, 32, 1);
        panel = Functions.bake(builder);
        //panel.addBox(-17.0F, -32.0F, -2, 32, 32, 1);
    }

    public void renderSign(PoseStack stack, VertexConsumer builder,int combinedLight,int combinedOverlay){
        panel.render(stack, builder, combinedLight, combinedOverlay);
    }

    @Override
    public void renderToBuffer(PoseStack p_225598_1_, VertexConsumer p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        panel.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }
}
