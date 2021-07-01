package fr.mattmouss.signs.tileentity.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.tileentity.model.PSSignModel;
import fr.mattmouss.signs.tileentity.model.SpecialSignModel;
import fr.mattmouss.signs.tileentity.primary.PlainSquareSignTileEntity;
import fr.mattmouss.signs.tileentity.primary.RectangleSignTileEntity;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class PlainSquareSignTileEntityRenderer extends TileEntityRenderer<PlainSquareSignTileEntity> {

    private final PSSignModel model = new PSSignModel();

    public PlainSquareSignTileEntityRenderer() {
    }

    @Override
    public void render(PlainSquareSignTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
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
            this.bindTexture(new ResourceLocation(SignMod.MODID,"textures/block/sign.png"));
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.5F,0.5F,0.5F);
        GlStateManager.translatef(1.0F/16.0F,2.0F,0.0F);
        this.model.renderSign();
        GlStateManager.scalef(2.0F,2.0F,2.0F);
        renderPicture(tileEntityIn);
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

    private void renderPicture(PlainSquareSignTileEntity psste){
        GlStateManager.translatef(-3.5F/16F,-21F/16F,0);
        Functions.setWorldGLState(false);
        renderLimit(psste);
        Functions.resetWorldGLState();
    }

    private void renderLimit(PlainSquareSignTileEntity psste){
        Color color = psste.getForegroundColor();
        this.bindTexture(new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/background.png"));
        int red,green,blue,alpha;
        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        alpha = color.getAlpha();
        float z = -0.06F;
        int texLength = 1;
        float limitLength = 0.5F/16F;
        float panelLength = 1.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        builder.pos(panelLength,panelLength,z).tex(0,texLength).color(red,green,blue,alpha).endVertex();
        builder.pos(panelLength,0,z).tex(0,0).color(red,green,blue,alpha).endVertex();
        builder.pos(0,0,z).tex(texLength,0).color(red,green,blue,alpha).endVertex();
        builder.pos(0,panelLength,z).tex(texLength,texLength).color(red,green,blue,alpha).endVertex();
        tessellator.draw();
    }

}
