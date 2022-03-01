package fr.matt1999rd.signs.tileentity.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import fr.matt1999rd.signs.tileentity.EditingSignTileEntity;
import fr.matt1999rd.signs.tileentity.model.SpecialSignModel;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.util.PictureRenderState;
import fr.matt1999rd.signs.util.Text;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;

import java.awt.*;

public class EditingSignTileEntityRenderer<T extends EditingSignTileEntity> extends TileEntityRenderer<T> {

    private final SpecialSignModel model ;
    private final Form form;

    public EditingSignTileEntityRenderer(TileEntityRendererDispatcher dispatcher,Form form) {
        super(dispatcher);
        this.form = form;
        if (form.isNotForEditing())throw new IllegalArgumentException("no such form are authorised in editing tile entity");
        model = new SpecialSignModel(form);
    }

    @Override
    public void render(EditingSignTileEntity tileEntityIn, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        BlockState blockstate = tileEntityIn.getBlockState();
        //code for display of background model
        stack.pushPose();
        //handle grid rotation for this block
        float angle= getAngleFromBlockState(blockstate);
        stack.translate(0.5F, 0.0F, 0.5F);
        Functions.rotate(stack, Direction.Axis.Y,angle);

        stack.pushPose();
        IVertexBuilder vertexBuilder = buffer.getBuffer(model.renderType(Functions.SIGN_BACKGROUND));
        this.model.renderSign(stack,vertexBuilder,combinedLight,combinedOverlay);
        stack.translate(-5.0F/16F,5.0F/16F,-2.1F/16F);
        renderPicture(stack,buffer,combinedLight);
        renderText(stack,buffer,tileEntityIn,combinedLight);
        stack.popPose();
        RenderSystem.depthMask(true);
        stack.popPose();
    }

    private void renderPicture(MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight) {
        stack.pushPose();
        IVertexBuilder pictureBuilder =  buffer.getBuffer(PictureRenderState.pictureRenderType);
        Matrix4f matrix4f = stack.last().pose();
        for (int i=0;i<128;i++){
            for (int j=0;j<128;j++){
                if (form.isIn(i,j)) {
                    int color = getColor(i,j);
                    renderPixel(matrix4f,i, j, pictureBuilder, color,combinedLight);
                }
            }
        }
        stack.popPose();
    }

    private void renderPixel(Matrix4f matrix4f,int i, int j, IVertexBuilder builder,int color,int combinedLight) {
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
        builder.vertex(matrix4f,x1,y1,z).color(red,green,blue,alpha).uv2(combinedLight).endVertex();
        builder.vertex(matrix4f,x1,y2,z).color(red,green,blue,alpha).uv2(combinedLight).endVertex();
        builder.vertex(matrix4f,x2,y2,z).color(red,green,blue,alpha).uv2(combinedLight).endVertex();
        builder.vertex(matrix4f,x2,y1,z).color(red,green,blue,alpha).uv2(combinedLight).endVertex();
    }

    private void renderText(MatrixStack stack, IRenderTypeBuffer buffer, EditingSignTileEntity tileEntity, int combinedLight){
        Functions.setWorldGLState();
        Text t = tileEntity.getText();
        float completeLength = 10.0F/16;
        float pixelLength = completeLength / 128.0F;
        t.render(stack,buffer,completeLength,completeLength,0.005F,pixelLength,pixelLength,combinedLight);
        Functions.resetWorldGLState();
    }

    private float getAngleFromBlockState(BlockState blockstate) {
        Direction facing = blockstate.getValue(BlockStateProperties.HORIZONTAL_FACING);
        //to transform the horizontal index by a rotation angle that is proportional to 90°
        //make 0->2 1->1 2->0 3->3 --> 0->2 1->1 2->0 3-> (-1%4) --> (2-x)%4
        float angle = 90.0F * ((2-facing.get2DDataValue())%4);
        if (blockstate.getValue(GridSupport.ROTATED)){
            //if rotation add 45° rotation to the block
            return angle+45.0F;
        }
        return angle;
    }

    private int getColor(int i,int j){
        if (form == Form.UPSIDE_TRIANGLE){
            int i_mod = (int) (63.5F - MathHelper.abs(63.5F-i));
            if (j==0 || j==1 || j==2*i_mod-1 || j==2*i_mod)return Color.RED.getRGB();
            else return Color.WHITE.getRGB();
        }else if (form == Form.OCTAGON){
            int i_mod = (int) (256/3F - MathHelper.abs(i-63.5F));
            int j_mod = (int) MathHelper.abs(j-63.5F);
            if (i==0 || i==1 || i==126 || i==127 || j==0 || j==1 || j==126 || j==127 || j_mod == i_mod || j_mod == i_mod + 1) return Color.WHITE.getRGB();
            else return Color.RED.getRGB();
        }else {
            return new Color(0,0,0,0).getRGB();
        }
    }
}
