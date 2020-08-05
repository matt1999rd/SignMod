package fr.mattmouss.signs.tileentity.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.tileentity.DrawingSignTileEntity;
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

import java.util.List;
import java.awt.*;

public class DrawingSignTileEntityRenderer<T extends DrawingSignTileEntity> extends TileEntityRenderer<T> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/block/sign.png");
    private final SpecialSignModel model ;
    private final Form form;

    public DrawingSignTileEntityRenderer(Form form) {
        if (!form.isForDrawing())throw new IllegalArgumentException("no such form are authorised in drawing tileentity");
        model = new SpecialSignModel(form);
        this.form = form;
    }

    @Override
    public void render(DrawingSignTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
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
            this.bindTexture(BACKGROUND);
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.model.renderSign();
        if (destroyStage<0){
            renderPicture(tileEntityIn);
        }
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
        if (destroyStage>=0){
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    private void renderPicture(DrawingSignTileEntity tileEntity) {
        GlStateManager.pushMatrix();
        Functions.setWorldGLState();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        for (int i=0;i<128;i++){
            for (int j=0;j<128;j++){
                if (form.isIn(i,j)) {
                    int color = tileEntity.getPixelColor(i, j);
                    renderPixel(i, j, builder, color);
                }
            }
        }
        tessellator.draw();
        GlStateManager.enableTexture();
        GlStateManager.enableBlend();
        builder.begin(7,DefaultVertexFormats.POSITION_TEX_COLOR);
        int n= tileEntity.getNumberOfText();
        for (int i=0;i<n;i++){
            Text t = tileEntity.getText(i);
            t.render(builder);
        }
        tessellator.draw();
        Functions.resetWorldGLState();
        GlStateManager.popMatrix();
    }

    private void renderPixel(int i, int j, BufferBuilder builder,int color) {
        float completeLength = 10.0F/16;
        float pixelLength = completeLength/128;
        float x1 = completeLength-pixelLength*i;
        float y1 = completeLength-pixelLength*j;
        float z = 0.006F;
        float x2 = x1-pixelLength;
        float y2 = y1-pixelLength;
        Color color1 = new Color(color,true);
        int red,green,blue,alpha;
        red = color1.getRed();
        green = color1.getGreen();
        blue = color1.getBlue();
        alpha = color1.getAlpha();
        builder.pos(x1,y1,z).color(red,green,blue,alpha).endVertex();
        builder.pos(x1,y2,z).color(red,green,blue,alpha).endVertex();
        builder.pos(x2,y2,z).color(red,green,blue,alpha).endVertex();
        builder.pos(x2,y1,z).color(red,green,blue,alpha).endVertex();
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
}
