package fr.matt1999rd.signs.tileentity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.tileentity.DrawingSignTileEntity;
import fr.matt1999rd.signs.tileentity.model.SpecialSignModel;
import fr.matt1999rd.signs.util.DrawUtils;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.util.PictureRenderState;
import fr.matt1999rd.signs.util.Text;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec2;
import com.mojang.math.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

public class DrawingSignTileEntityRenderer<T extends DrawingSignTileEntity> implements BlockEntityRenderer<T> {
    private final SpecialSignModel model ;
    private final Form form;

    public DrawingSignTileEntityRenderer(BlockEntityRendererProvider.Context context, Form form) {
        if (!form.isForDrawing())throw new IllegalArgumentException("no such form are authorised in drawing tile entity");
        model = new SpecialSignModel(form);
        this.form = form;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(DrawingSignTileEntity tileEntityIn, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        BlockState blockstate = tileEntityIn.getBlockState();
        //code for display of background model
        stack.pushPose();
        //handle grid rotation for this block
        float angle= getAngleFromBlockState(blockstate);
        stack.translate(0.5F, 0.0F, 0.5F);
        Functions.rotate(stack, Direction.Axis.Y, angle);
        //code for changing background display

        stack.pushPose();
        VertexConsumer vertexBuilder = buffer.getBuffer(model.renderType(Functions.SIGN_BACKGROUND));

        this.model.renderSign(stack,vertexBuilder,combinedLight,combinedOverlay);

        stack.translate(-5.0F/16F,5.0F/16F,-2.1F/16F);
        renderPicture(stack,tileEntityIn,buffer,combinedLight);

        stack.popPose();
        RenderSystem.depthMask(true);
        stack.popPose();
    }

    private void renderPicture(PoseStack stack, DrawingSignTileEntity tileEntity, MultiBufferSource buffer, int combinedLight) {
        stack.pushPose();
        VertexConsumer pictureBuilder = buffer.getBuffer(PictureRenderState.pictureRenderType());
        Matrix4f matrix4f = stack.last().pose();
        for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
                if (form.isIn(i, j)) {
                    int color = tileEntity.getPixelColor(i, j);
                    renderPixel(matrix4f, i, j, pictureBuilder, color, combinedLight);
                }
            }
        }
        Functions.setWorldGLState();
        int n = tileEntity.getNumberOfText();
        float completeLength = 10.0F / 16;
        float pixelLength = completeLength / 128.0F;
        Vector3f origin = new Vector3f(completeLength, completeLength, 0.005F);
        Vec2 scale = new Vec2(pixelLength, pixelLength);
        for (int i = 0; i < n; i++) {
            Text t = tileEntity.getText(i);
            t.render(stack, buffer, origin, scale, combinedLight);
        }
        Functions.resetWorldGLState();
        stack.popPose();
    }

    private void renderPixel(Matrix4f matrix4f,int i, int j, VertexConsumer builder,int color,int combinedLight) {
        float completeLength = 10.0F/16;
        float pixelLength = completeLength/128;
        float x1 = completeLength-pixelLength*i;
        float y1 = completeLength-pixelLength*j;
        float z = 0.006F;
        float x2 = x1-pixelLength;
        float y2 = y1-pixelLength;
        DrawUtils.renderQuad(matrix4f,builder,x1,y1,x2,y2,z,color,combinedLight);
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
}
