package fr.matt1999rd.signs.tileentity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import fr.matt1999rd.signs.tileentity.model.DirectionSignModel;
import fr.matt1999rd.signs.util.DrawUtils;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.util.PictureRenderState;
import fr.matt1999rd.signs.util.Text;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;

import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec2;
import com.mojang.math.Vector3f;
import static fr.matt1999rd.signs.util.DirectionSignConstants.*;
import static fr.matt1999rd.signs.util.DrawUtils.renderRectangle;
import static fr.matt1999rd.signs.util.DrawUtils.renderTriangle;

public class DirectionSignTileEntityRenderer<T extends DirectionSignTileEntity> implements BlockEntityRenderer<T> {

    private final DirectionSignModel model ;
    private final Form form;
    private static final float blockLength = 16F;

    public DirectionSignTileEntityRenderer(BlockEntityRendererProvider.Context context, Form form) {
        if (!form.isForDirection())throw new IllegalArgumentException("no such form are authorised in direction tileentity");
        model = new DirectionSignModel(form);
        this.form = form;
    }

    @Override
    public void render(DirectionSignTileEntity tileEntityIn, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        BlockState blockstate = tileEntityIn.getBlockState();
        //code for display of background model
        stack.pushPose();
        //handle grid rotation for this block
        float angle= getAngleFromBlockState(blockstate);
        stack.translate(0.5F,0.0F, 0.5F);
        Functions.rotate(stack, Direction.Axis.Y, angle);
        //code for changing background display
        stack.pushPose();

        VertexConsumer vertexBuilder = buffer.getBuffer(model.renderType(Functions.SIGN_BACKGROUND));
        this.model.renderSign(stack,tileEntityIn,vertexBuilder,combinedLight,combinedOverlay);
        stack.translate(-totalLength /(2*blockLength),totalHeight/(2*blockLength),-2.001F/blockLength);

        renderBackground(stack,tileEntityIn,buffer,combinedLight);
        stack.translate(0,0,-0.1F/blockLength);
        renderText(stack,buffer,tileEntityIn,combinedLight);

        stack.popPose();
        RenderSystem.depthMask(true);
        stack.popPose();
    }

    private void renderBackground(PoseStack stack, DirectionSignTileEntity tileEntityIn, MultiBufferSource buffer, int combinedLight) {
        stack.pushPose();
        VertexConsumer pictureBuilder =  buffer.getBuffer(PictureRenderState.pictureRenderType());

        int flag = tileEntityIn.getLFlag();
        int pow2=1;
        Matrix4f matrix4f = stack.last().pose();
        for (int i=0;i<7;i++){
            if ((flag&pow2) == pow2){
                renderPanel(matrix4f,pictureBuilder,i,tileEntityIn,combinedLight);
            }
            pow2*=2;
        }
        stack.popPose();
    }

    private void renderPanel(Matrix4f matrix4f, VertexConsumer builder, int ind, DirectionSignTileEntity tileEntity, int combinedLight){
        float limLength = 0.1F/blockLength;
        float x1 = 0;
        float x2 = totalLength/blockLength;
        int panelInd;
        int panelLength;
        int colorInd;
        float y1,y2;
        if (ind <0 || ind > 5){
            SignMod.LOGGER.warn("Rendering not done : skip bad value of case !");
            return;
        }else if (ind < 3){
            panelLength = 1;
        }else if (ind < 5){
            ind -= 3;
            panelLength = 3;
        }else {
            ind -= 5;
            panelLength = 5;
        }

        y2 = (totalHeight - ind * (singlePanelHeight + gapBtwPanelHeight)) / blockLength;
        y1 = y2 - panelLength*singlePanelHeight/blockLength;
        colorInd = ind +1;
        panelInd = 2*ind;

        int limColor = tileEntity.getColor(colorInd,false);
        int bgColor = tileEntity.getColor(colorInd,true);
        //center rectangle
        renderRectangle(matrix4f,builder,x1+limLength,y1+limLength,x2-limLength,y2-limLength,bgColor,combinedLight);
        //down limit
        renderRectangle(matrix4f,builder,x1,y1,x2,y1+limLength,limColor,combinedLight);
        //up limit
        renderRectangle(matrix4f,builder,x1,y2-limLength,x2,y2,limColor,combinedLight);
        if (form == Form.ARROW){
            boolean isRightArrow = tileEntity.isRightArrow(panelInd);
            int rightColor = (isRightArrow) ? bgColor : limColor;
            int leftColor= (isRightArrow) ? limColor  : bgColor;
            float xDiff = -limLength;
            //right limit
            renderRectangle(matrix4f,builder, x1, y1+limLength, x1 + limLength, y2-limLength, rightColor,combinedLight);
            //left limit
            renderRectangle(matrix4f,builder, x2 - limLength, y1+limLength, x2, y2-limLength, leftColor, combinedLight);
            float xCommon = totalLength/blockLength;
            float xSolo = xCommon+panelLength*1F/blockLength;
            float ySolo = (y1+y2)/2;
            if (isRightArrow){
                xCommon = totalLength/blockLength-xCommon;
                xSolo = totalLength/blockLength - xSolo;
                xDiff *= -1;
            }
            //arrow of limit
            renderTriangle(matrix4f,builder,xCommon,xSolo,y1,ySolo,y2,limColor,combinedLight,false);
            //arrow of background
            renderTriangle(matrix4f,builder,xCommon,xSolo+xDiff,y1+limLength,ySolo,y2-limLength,bgColor,combinedLight,true);
        }else {
            //left limit
            renderRectangle(matrix4f,builder, x1, y1, x1 + limLength, y2, limColor,combinedLight);
            //right limit
            renderRectangle(matrix4f,builder, x2 - limLength, y1, x2, y2, limColor,combinedLight);
        }
    }



    private void renderText(PoseStack stack,MultiBufferSource buffer,DirectionSignTileEntity tileEntity,int combinedLight){
        boolean isTextCentered = tileEntity.isTextCentered();
        for (int i=0;i<5;i++) {
            if (tileEntity.isCellPresent(i)) {
                renderSpecificText(stack,buffer,i, false, tileEntity,combinedLight);
                if (!isTextCentered)renderSpecificText(stack,buffer,i, true, tileEntity,combinedLight);
            }
        }
    }

    private void renderSpecificText(PoseStack stack,MultiBufferSource buffer,int ind,boolean isEnd,DirectionSignTileEntity tileEntity,int combinedLight){
        stack.pushPose();
        Functions.setWorldGLState();
        Text t = tileEntity.getText(ind,isEnd);
        Vector3f origin = new Vector3f(totalLength/blockLength,totalHeight/blockLength,0.005F);
        float pixelLength = totalHeight/blockLength / verPixelNumber;
        Vec2 scale = new Vec2(pixelLength,pixelLength);
        t.render(stack,buffer,origin,scale,combinedLight);
        Functions.resetWorldGLState();
        stack.popPose();
    }

    private float getAngleFromBlockState(BlockState blockstate) {
        Direction facing = blockstate.getValue(BlockStateProperties.HORIZONTAL_FACING);
        //to transform the horizontal index by a rotation angle that is proportionnal to 90°
        //make 0->2 1->1 2->0 3->3 --> 0->2 1->1 2->0 3-> (-1%4) --> (2-x)%4
        float angle = 90.0F * ((2-facing.get2DDataValue())%4);
        if (blockstate.getValue(GridSupport.ROTATED)){
            //if rotation add 45° rotation to the block
            return angle+45.0F;
        }
        return angle;
    }

}
