package fr.mattmouss.signs.tileentity.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.tileentity.EditingSignTileEntity;
import fr.mattmouss.signs.tileentity.model.SpecialSignModel;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Text;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class EditingSignTileEntityRenderer<T extends EditingSignTileEntity> extends TileEntityRenderer<T> {

    private static final ResourceLocation STOP_BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/stop.png");
    private static final ResourceLocation LW_BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/let_way.png");
    private final SpecialSignModel model ;
    private final Form form;

    public EditingSignTileEntityRenderer(Form form) {
        if (!form.isForEditing())throw new IllegalArgumentException("no such form are authorised in editing tileentity");
        model = new SpecialSignModel(form);
        this.form = form;
    }

    @Override
    public void render(EditingSignTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        BlockState blockstate = tileEntityIn.getBlockState();
        //code for display of background model
        GlStateManager.pushMatrix();
        //handle grid rotation for this block
        float angle= getAngleFromBlockState(blockstate);
        GlStateManager.translatef((float)x + 0.5F, (float)y , (float)z + 0.5F);
        GlStateManager.rotatef(angle, 0.0F, 1.0F, 0.0F);
        //code for changing background display
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0F, 2.0F, 1.0F);
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
            this.bindTexture(getTexture());
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.enableDepthTest();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.model.renderSign();
        if (destroyStage<0){
            renderText(tileEntityIn);
        }
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        if (destroyStage>=0){
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    private void renderText(EditingSignTileEntity tileEntity){
        GlStateManager.pushMatrix();
        Functions.setWorldGLState(true);
        Text t = tileEntity.getText();
        GlStateManager.enableTexture();
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        t.render(builder);
        tessellator.draw();
        Functions.resetWorldGLState();
        GlStateManager.popMatrix();
    }

    private float getAngleFromBlockState(BlockState blockstate) {
        Direction facing = blockstate.get(BlockStateProperties.HORIZONTAL_FACING);
        //to transform the horizontal index by a rotation angle that is proportionnal to 90°
        //make 0->2 1->1 2->0 3->3 --> 0->2 1->1 2->0 3-> (-1%4) --> (2-x)%4
        float angle = 90.0F * ((2-facing.getHorizontalIndex())%4);
        if (blockstate.get(GridSupport.ROTATED)){
            //if rotation add 45° rotation to the block
            return angle+45.0F;
        }
        return angle;
    }

    private ResourceLocation getTexture(){
        if (form == Form.OCTOGONE){
            return STOP_BACKGROUND;
        }else if (form == Form.UPSIDE_TRIANGLE){
            return LW_BACKGROUND;
        }else {
            //never happenning !!
            throw new IllegalArgumentException("form given in is not right for editing sign : "+form);
        }
    }
}
