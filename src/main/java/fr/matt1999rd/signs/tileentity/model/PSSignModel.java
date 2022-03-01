package fr.matt1999rd.signs.tileentity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fr.matt1999rd.signs.util.PictureRenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class PSSignModel extends Model {
    private final ModelRenderer panel = new ModelRenderer(this,0,0);
    public PSSignModel() {
        super(PictureRenderState::getModelRenderType);
        initModel();
    }

    private void initModel(){
        panel.addBox(-17.0F, -32.0F, -2, 32, 32, 1);
    }

    public void renderSign(MatrixStack stack, IVertexBuilder builder,int combinedLight,int combinedOverlay){
        panel.render(stack, builder, combinedLight, combinedOverlay);
    }

    @Override
    public void renderToBuffer(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        panel.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }
}
